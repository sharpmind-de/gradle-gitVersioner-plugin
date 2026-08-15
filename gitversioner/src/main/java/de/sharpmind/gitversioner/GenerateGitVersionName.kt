package de.sharpmind.gitversioner

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.util.Properties

internal abstract class GenerateGitVersionName : DefaultTask() {

    @get:Input
    abstract val versionProperties: MapProperty<String, String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val file = outputFile.get().asFile
        file.parentFile.mkdirs()

        val properties = Properties().apply {
            putAll(versionProperties.get())
        }

        file.writer().use {
            properties.store(it, "gitVersioner plugin - extracted data from git repository")
        }
        logger.lifecycle("git versionName: ${versionProperties.get()["versionName"]}")
        logger.lifecycle("gitVersion output: $file")
    }
}
