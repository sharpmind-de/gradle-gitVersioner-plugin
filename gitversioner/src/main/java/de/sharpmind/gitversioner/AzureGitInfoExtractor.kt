package de.sharpmind.gitversioner

/**
 * Azure DevOps Azure Build Pipeline Git implementation of [GitInfoExtractor].
 */
internal class AzureGitInfoExtractor(delegate: GitInfoExtractor) : GitInfoExtractor by delegate {
    /**
     * Get the current branch name from the environment variable Build.SourceBranchName
     */
    override val currentBranch: String?
        get() = System.getenv("BUILD_SOURCEBRANCHNAME")
}
