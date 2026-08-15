package de.sharpmind.gitversioner

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.Properties

class ConfigurationCacheTest {

    @get:Rule
    val projectDir = TemporaryFolder()

    @Test
    fun `tasks reuse the configuration cache`() {
        val rootDir = projectDir.root
        rootDir.resolve("settings.gradle").writeText("rootProject.name = 'configuration-cache-test'\n")
        rootDir.resolve("build.gradle").writeText(
            """
            plugins {
                id 'java'
                id 'de.sharpmind.gitversioner'
            }
            """.trimIndent()
        )
        rootDir.resolve(".gitignore").writeText(".gradle/\nbuild/\n")
        rootDir.resolve("README.md").writeText("Configuration cache test project\n")
        initializeGitRepository(rootDir)

        val arguments = listOf(
            "gitVersion",
            "generateGitVersionName",
            "--configuration-cache",
            "--configuration-cache-problems=fail",
            "--stacktrace"
        )

        val firstRun = runner(rootDir, arguments).build()
        assertThat(firstRun.output).contains("Configuration cache entry stored.")

        val secondRun = runner(rootDir, arguments).build()
        assertThat(secondRun.output).contains("Configuration cache entry reused.")

        val propertiesFile = rootDir.resolve("build/gitversioner/version.properties")
        assertThat(propertiesFile).isFile
        val properties = Properties().apply {
            propertiesFile.inputStream().use(::load)
        }
        assertThat(properties.getProperty("versionName")).isNotBlank()
    }

    private fun runner(rootDir: File, arguments: List<String>): GradleRunner =
        GradleRunner.create()
            .withProjectDir(rootDir)
            .withPluginClasspath()
            .withArguments(arguments)

    private fun initializeGitRepository(rootDir: File) {
        runGit(rootDir, "init", "--initial-branch=main")
        runGit(rootDir, "config", "user.name", "GitVersioner Test")
        runGit(rootDir, "config", "user.email", "gitversioner-test@sharpmind.de")
        runGit(rootDir, "add", ".")
        runGit(rootDir, "commit", "-m", "Initial commit")
    }

    private fun runGit(rootDir: File, vararg arguments: String) {
        val process = ProcessBuilder(listOf("git") + arguments)
            .directory(rootDir)
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().use { it.readText() }
        check(process.waitFor() == 0) {
            "git ${arguments.joinToString(" ")} failed:\n$output"
        }
    }
}
