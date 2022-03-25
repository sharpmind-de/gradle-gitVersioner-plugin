package de.sharpmind.gitversioner

data class Commit(
    val sha1: String,
    val parent: String?,
    val date: Long
)

/**
 * Mocks a real git repo with commit history and branches
 */
open class MockGitRepo(
    val graph: Collection<Commit> = emptyList(),
    val head: String? = null,
    val branchHeads: List<Pair<String /*sha1*/, String/*name*/>> = emptyList(),
    final override val localChanges: LocalChanges = NO_CHANGES,
    final override val isHistoryShallowed: Boolean = false,
    final override val isGitWorking: Boolean = true
) : GitInfoExtractor {

    val headCommit: Commit? = if (head == null) null else
        requireNotNull(commitInGraph(head)) { "head commit not in graph" }

    init {
        graph.forEach { commit ->
            // orphan commits are valid
            if (commit.parent != null) {
                // allow unknown parents for shallow clones
                if (!isHistoryShallowed) {
                    // unknown parents are invalid
                    requireNotNull(commit.parentCommit()) { "parent commit not in graph" }
                }
            }
        }

        branchHeads.forEach { (sha1, name) ->
            requireNotNull(commitInGraph(sha1)) { "$sha1 ($name) not a commit in graph" }
        }
        if (isHistoryShallowed) {
            require(graph.size <= 1) { "shallow clones only have one commit" }
        }
    }

    override val currentSha1: String? = headCommit?.sha1

    override val currentBranch: String? = if (headCommit == null) null else
        sha1ToBranch(headCommit.sha1)

    override val initialCommitDate: Long
        get() {
            if (headCommit == null) return 0
            val commits = commitsToHead
            if (commits.isEmpty()) return 0

            val initialCommit = commitInGraph(commitsToHead.last())

            return initialCommit?.date ?: 0
        }

    override fun commitDate(rev: String): Long = commitInGraph(rev)?.date
        ?: throw IllegalStateException("commit $rev not in graph")

    override val commitsToHead: List<String>
        get() = if (headCommit == null) emptyList() else commitsUpTo(headCommit.sha1)

    override fun commitsUpTo(rev: String, args: String): List<String> {
        val commit = commitInGraph(rev) ?: return emptyList()
        val history: MutableList<Commit> = mutableListOf(commit)

        while (true) {
            val parent = history.last().parentCommit()
            if (parent == null) {
                return history.map { it.sha1 }
            } else {
                history.add(parent)
            }
        }
    }

    fun commitInGraph(rev: String): Commit? {
        val inGraph = graph.find { it.sha1 == rev }
        if (inGraph != null) return inGraph

        // rev is branch, branchName -> sha1
        val sha1 = branchToSha1(rev) ?: return null
        return graph.find { it.sha1 == sha1 }
    }

    fun branchToSha1(branchName: String): String? = branchHeads.find { it.second == branchName }?.first
    fun sha1ToBranch(sha1: String): String? = branchHeads.find { it.first == sha1 }?.second

    fun Commit.parentCommit(): Commit? {
        if (parent == null) return null
        return graph.find { it.sha1 == parent }
    }
}

class GitInfoExtractorStub(
    override val currentSha1: String? = null,
    override val currentBranch: String? = null,
    override val localChanges: LocalChanges = NO_CHANGES,
    override val isGitWorking: Boolean = true,
    override val isHistoryShallowed: Boolean = false,
    val commits: List<String> = emptyList()
) : GitInfoExtractor {

    override fun commitsUpTo(rev: String, args: String): List<String> {
        return commits
    }

    override val initialCommitDate: Long
        get() = 0L

    override val commitsToHead: List<String>
        get() = commits

    override fun commitDate(rev: String): Long {
        return 0L
    }
}
