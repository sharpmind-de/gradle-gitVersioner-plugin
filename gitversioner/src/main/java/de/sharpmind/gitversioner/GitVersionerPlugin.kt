package de.sharpmind.gitversioner

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
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
        val gitVersionExtractor = ShellGitInfoExtractor(rootProject)
        val gitVersioner = rootProject.extensions.create(
            "gitVersioner",
            GitVersioner::class.java, gitVersionExtractor, project.logger
        )

        project.task("gitVersion").apply {
            group = "Help"
            description = "Displays the version information extracted from git history"
            doLast {
                with(gitVersioner) {

                    if (!gitVersioner.isGitProjectCorrectlyInitialized) {

                        val why = if (gitVersioner.isHistoryShallowed) {
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

                        println(
                            """
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
                        )
                        return@doLast
                    }

                    val baseBranchRange = (initialCommit?.take(7) ?: "") +
                            "..${featureBranchOriginCommit?.take(7) ?: ""}"

                    val featureBranchRange = (featureBranchOriginCommit?.take(7) ?: "") +
                            "..${currentSha1Short ?: ""}"

                    println(
                        """
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
                    )
                }
            }
        }

        val task = project.tasks.create("generateGitVersionName", GenerateGitVersionName::class.java).apply {
            this.gitVersioner = gitVersioner

            group = "Build"
            description = "analyzes the git history and creates a version name (generates machine readable output file)"
        }

        project.plugins.withType(JavaPlugin::class.java) {
            project.tasks.named(JavaPlugin.CLASSES_TASK_NAME).configure {
                it.dependsOn(task)
            }
        }
    }

    val pluginVersion: String by lazy<String> {
        val props = Properties()
        props.load(GitVersionerPlugin::class.java.getResourceAsStream("/version.properties"))
        props.getProperty("version")
    }
}
