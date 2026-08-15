package de.sharpmind.gitversioner

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import java.util.*


@Suppress("RedundantVisibilityModifier")
public class GitVersionerPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val rootProject = project.rootProject
        if (project != rootProject) {
            throw IllegalStateException(
                "Register the 'de.sharpmind.gitversioner' plugin only once " +
                        "in the root project build.gradle."
            )
        }

        // add extension to root project, makes sense only once per project

        // determine build environment
        // if TF_BUILD is set to true, we are running in azure pipelines
        val isRunningInAzurePipelines = if (System.getenv("TF_BUILD") == null) {
            false
        } else {
            System.getenv("TF_BUILD").uppercase() == "TRUE"
        }

        if (isRunningInAzurePipelines) {
            println("Azure Pipeline environment detected.")
        }

        // the default git info extractor
        val shellGitInfoExtractor = ShellGitInfoExtractor(rootProject.projectDir, project.providers)

        val gitVersionExtractor =
            // use azure git info extractor if running in azure pipelines
            if (isRunningInAzurePipelines) AzureGitInfoExtractor(shellGitInfoExtractor)
            // use shell git info extractor as default
            else shellGitInfoExtractor

        val gitVersioner = rootProject.extensions.create(
            "gitVersioner",
            GitVersioner::class.java, gitVersionExtractor, project.logger
        )

        val gitVersionTask = project.tasks.register("gitVersion", DisplayGitVersion::class.java) {
            it.group = "Help"
            it.description = "Displays the version information extracted from git history"
        }

        val generateTask = project.tasks.register("generateGitVersionName", GenerateGitVersionName::class.java) {
            it.group = "Build"
            it.description = "analyzes the git history and creates a version name (generates machine readable output file)"
            it.outputFile.convention(project.layout.buildDirectory.file("gitversioner/version.properties"))
        }

        project.afterEvaluate {
            val versionProperties = gitVersioner.asPropertiesMap()
            generateTask.configure {
                it.versionProperties.set(versionProperties)
            }
            gitVersionTask.configure {
                it.report.set(gitVersioner.createReport(pluginVersion))
            }
        }

        project.plugins.withType(JavaPlugin::class.java) { javaPlugin ->
            val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
            val main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

            main.resources.srcDir(project.layout.buildDirectory.dir("gitversioner"))

            project.tasks.named(JavaPlugin.PROCESS_RESOURCES_TASK_NAME).configure {
                it.dependsOn(generateTask)
            }
        }
    }

    val pluginVersion: String by lazy<String> {
        val props = Properties()
        props.load(GitVersionerPlugin::class.java.getResourceAsStream("/version.properties"))
        props.getProperty("version")
    }
}

private fun GitVersioner.asPropertiesMap(): Map<String, String> {
    val properties = linkedMapOf<String, String>()
    properties.putWhenSet("versionCode", versionCode)
    properties.putWhenSet("versionName", versionName)
    properties.putWhenSet("baseBranch", baseBranch)
    properties.putWhenSet("branchName", branchName)
    properties.putWhenSet("currentSha1", currentSha1)
    properties.putWhenSet("baseBranchCommitCount", baseBranchCommitCount)
    properties.putWhenSet("featureBranchCommitCount", featureBranchCommitCount)
    properties.putWhenSet("timeComponent", timeComponent)
    properties.putWhenSet("yearFactor", yearFactor)
    properties.putWhenSet("localChanges", localChanges)
    return properties
}

private fun MutableMap<String, String>.putWhenSet(key: String, value: Any?) {
    if (value != null) {
        put(key, value.toString())
    }
}

private fun GitVersioner.createReport(pluginVersion: String): String {
    if (!isGitProjectCorrectlyInitialized) {
        val why = if (isHistoryShallowed) {
            "WARNING: Git history is incomplete (shallow clone)\n" +
                    "The de.sharpmind.gitversioner gradle plugin requires the complete git history to calculate " +
                    "the version. The history is shallowed, therefore the version code would be incorrect.\n" +
                    "Default values versionName: 'undefined', versionCode: 1 are used instead.\n\n" +
                    "Please fetch the complete history with:\n" +
                    "\tgit fetch --unshallow"
        } else {
            "WARNING: git not initialized. Run:\n" +
                    "\tgit init"
        }

        return """
            |
            |GitVersioner Plugin v$pluginVersion
            |-------------------
            |VersionCode: $versionCode
            |VersionName: $versionName
            |
            |baseBranch: $baseBranch
            |
            |$why
        """.replaceIndentByMargin()
    }

    val baseBranchRange = (initialCommit?.take(7) ?: "") +
            "..${featureBranchOriginCommit?.take(7) ?: ""}"
    val featureBranchRange = (featureBranchOriginCommit?.take(7) ?: "") +
            "..${currentSha1Short ?: ""}"

    return """
        |
        |GitVersioner Plugin v$pluginVersion
        |-------------------
        |VersionCode: $versionCode
        |VersionName: $versionName
        |
        |baseBranch: $baseBranch
        |current branch: $branchName
        |current commit: $currentSha1Short
        |
        |baseBranch commits: $baseBranchCommitCount ($baseBranchRange)
        |featureBranch commits: $featureBranchCommitCount ($featureBranchRange)
        |
        |timeComponent: $timeComponent (yearFactor:$yearFactor)
        |
        |LocalChanges: ${localChanges.shortStats()}
    """.replaceIndentByMargin()
}
