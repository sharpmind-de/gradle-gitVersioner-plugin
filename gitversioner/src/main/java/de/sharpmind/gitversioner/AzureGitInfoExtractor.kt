package de.sharpmind.gitversioner

import org.gradle.api.Project

/**
 * Azure DevOps Azure Build Pipeline Git implementation of [GitInfoExtractor].
 */
internal class AzureGitInfoExtractor(private val project: Project, delegate: GitInfoExtractor) :
    GitInfoExtractor by delegate {
    /**
     * Get the current branch name from the environment variable Build.SourceBranchName
     */
    override val currentBranch: String?
        get() = System.getenv("BUILD_SOURCEBRANCHNAME")
}
