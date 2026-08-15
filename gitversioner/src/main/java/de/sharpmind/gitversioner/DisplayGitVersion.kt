package de.sharpmind.gitversioner

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

internal abstract class DisplayGitVersion : DefaultTask() {

    @get:Input
    abstract val report: Property<String>

    @TaskAction
    fun display() {
        logger.lifecycle(report.get())
    }
}
