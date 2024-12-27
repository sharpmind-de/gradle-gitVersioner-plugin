package de.sharpmind.gitversioner

import org.assertj.core.api.SoftAssertions
import org.junit.*
import org.junit.runner.*
import org.junit.runners.*

/*

@RunWith(JUnit4::class)
class FeatureBranchTest {
    @Test
    fun `on feature branch - few commits - local changes`() {
        val graph = listOf(
            Commit(sha1 = "X", parent = "j", date = 150_010_000), // <-- feature/bug_123, HEAD
            Commit(sha1 = "j", parent = "i", date = 150_009_000),
            Commit(sha1 = "i", parent = "h", date = 150_008_000),
            Commit(sha1 = "h", parent = "g", date = 150_007_000),
            Commit(sha1 = "g", parent = "f", date = 150_006_000), // <-- main
            Commit(sha1 = "f", parent = "e", date = 150_005_000),
            Commit(sha1 = "e", parent = "d", date = 150_004_000),
            Commit(sha1 = "d", parent = "c", date = 150_003_000),
            Commit(sha1 = "c", parent = "b", date = 150_002_000),
            Commit(sha1 = "b", parent = "a", date = 150_001_000),
            Commit(sha1 = "a", parent = null, date = 150_000_000)
        )

        val localChanges = LocalChanges(3, 5, 7)
        val git = MockGitRepo(graph, "X", listOf("g" to "main", "X" to "feature/bug_123"), localChanges)
        val versioner = GitVersioner(git)

        SoftAssertions.assertSoftly { softly ->
            softly.assertThat(versioner.versionCode).isEqualTo(7)
            softly.assertThat(versioner.versionName).isEqualTo("7-bug_123+4-SNAPSHOT(3 +5 -7)")
            softly.assertThat(versioner.baseBranchCommitCount).isEqualTo(7)
            softly.assertThat(versioner.featureBranchCommitCount).isEqualTo(4)
            softly.assertThat(versioner.branchName).isEqualTo("feature/bug_123")
            softly.assertThat(versioner.currentSha1).isEqualTo("X")
            softly.assertThat(versioner.baseBranch).isEqualTo("main")
            softly.assertThat(versioner.initialCommit).isEqualTo("a")
            softly.assertThat(versioner.localChanges).isEqualTo(localChanges)
            softly.assertThat(versioner.yearFactor).isEqualTo(1000)
            softly.assertThat(versioner.timeComponent).isEqualTo(0)
            softly.assertThat(versioner.featureBranchOriginCommit).isEqualTo("g")
        }
    }

    @Test
    fun `on feature branch - few commits - local changes - attachDiffToSnapshot false`() {
        val graph = listOf(
            Commit(sha1 = "X", parent = "j", date = 150_010_000), // <-- feature/bug_123, HEAD
            Commit(sha1 = "j", parent = "i", date = 150_009_000),
            Commit(sha1 = "i", parent = "h", date = 150_008_000),
            Commit(sha1 = "h", parent = "g", date = 150_007_000),
            Commit(sha1 = "g", parent = "f", date = 150_006_000), // <-- main
            Commit(sha1 = "f", parent = "e", date = 150_005_000),
            Commit(sha1 = "e", parent = "d", date = 150_004_000),
            Commit(sha1 = "d", parent = "c", date = 150_003_000),
            Commit(sha1 = "c", parent = "b", date = 150_002_000),
            Commit(sha1 = "b", parent = "a", date = 150_001_000),
            Commit(sha1 = "a", parent = null, date = 150_000_000)
        )

        val localChanges = LocalChanges(3, 5, 7)
        val git = MockGitRepo(graph, "X", listOf("g" to "main", "X" to "feature/bug_123"), localChanges)
        val versioner = GitVersioner(git)

        versioner.addLocalChangesDetails = false

        SoftAssertions.assertSoftly { softly ->
            softly.assertThat(versioner.versionCode).isEqualTo(7)
            softly.assertThat(versioner.versionName).isEqualTo("7-bug_123+4-SNAPSHOT")
            softly.assertThat(versioner.baseBranchCommitCount).isEqualTo(7)
            softly.assertThat(versioner.featureBranchCommitCount).isEqualTo(4)
            softly.assertThat(versioner.branchName).isEqualTo("feature/bug_123")
            softly.assertThat(versioner.currentSha1).isEqualTo("X")
            softly.assertThat(versioner.baseBranch).isEqualTo("main")
            softly.assertThat(versioner.initialCommit).isEqualTo("a")
            softly.assertThat(versioner.localChanges).isEqualTo(localChanges)
            softly.assertThat(versioner.yearFactor).isEqualTo(1000)
            softly.assertThat(versioner.timeComponent).isEqualTo(0)
            softly.assertThat(versioner.featureBranchOriginCommit).isEqualTo("g")
        }
    }

    @Test
    fun `on feature branch - few commits`() {
        val graph = listOf(
            Commit(sha1 = "X", parent = "j", date = 150_010_000), // <-- feature/bug_123, HEAD
            Commit(sha1 = "j", parent = "i", date = 150_009_000),
            Commit(sha1 = "i", parent = "h", date = 150_008_000),
            Commit(sha1 = "h", parent = "g", date = 150_007_000),
            Commit(sha1 = "g", parent = "f", date = 150_006_000), // <-- main
            Commit(sha1 = "f", parent = "e", date = 150_005_000),
            Commit(sha1 = "e", parent = "d", date = 150_004_000),
            Commit(sha1 = "d", parent = "c", date = 150_003_000),
            Commit(sha1 = "c", parent = "b", date = 150_002_000),
            Commit(sha1 = "b", parent = "a", date = 150_001_000),
            Commit(sha1 = "a", parent = null, date = 150_000_000)
        )

        val git = MockGitRepo(graph, "X", listOf("g" to "main", "X" to "feature/bug_123"))
        val versioner = GitVersioner(git)

        SoftAssertions.assertSoftly { softly ->
            softly.assertThat(versioner.versionCode).isEqualTo(7)
            softly.assertThat(versioner.versionName).isEqualTo("7-bug_123+4")
            softly.assertThat(versioner.baseBranchCommitCount).isEqualTo(7)
            softly.assertThat(versioner.featureBranchCommitCount).isEqualTo(4)
            softly.assertThat(versioner.branchName).isEqualTo("feature/bug_123")
            softly.assertThat(versioner.currentSha1).isEqualTo("X")
            softly.assertThat(versioner.baseBranch).isEqualTo("main")
            softly.assertThat(versioner.initialCommit).isEqualTo("a")
            softly.assertThat(versioner.localChanges).isEqualTo(NO_CHANGES)
            softly.assertThat(versioner.yearFactor).isEqualTo(1000)
            softly.assertThat(versioner.timeComponent).isEqualTo(0)
            softly.assertThat(versioner.featureBranchOriginCommit).isEqualTo("g")
        }
    }

    @Test
    fun `on feature branch - new commits on baseBranch will not increment versioncode`() {
        val graph = listOf(
            // new commits on main way in the future
            Commit(sha1 = "a3", parent = "a2", date = 160_013_000),
            Commit(sha1 = "a2", parent = "a1", date = 160_012_000),
            Commit(sha1 = "a1", parent = "g", date = 160_011_000),

            Commit(sha1 = "X", parent = "j", date = 150_010_000), // <-- feature/bug_123, HEAD
            Commit(sha1 = "j", parent = "i", date = 150_009_000),
            Commit(sha1 = "i", parent = "h", date = 150_008_000),
            Commit(sha1 = "h", parent = "g", date = 150_007_000),
            Commit(sha1 = "g", parent = "f", date = 150_006_000), // <-- base of feature branch
            Commit(sha1 = "f", parent = "e", date = 150_005_000),
            Commit(sha1 = "e", parent = "d", date = 150_004_000),
            Commit(sha1 = "d", parent = "c", date = 150_003_000),
            Commit(sha1 = "c", parent = "b", date = 150_002_000),
            Commit(sha1 = "b", parent = "a", date = 150_001_000),
            Commit(sha1 = "a", parent = null, date = 150_000_000)
        )

        // with new commits on main
        val git = MockGitRepo(graph, "X", listOf("a3" to "main", "X" to "feature/bug_123"))
        val versioner = GitVersioner(git)

        // without new commits on main
        val git2 = MockGitRepo(graph, "X", listOf("g" to "main", "X" to "feature/bug_123"))
        val versioner2 = GitVersioner(git2)

        fun assertOutput(v: GitVersioner) {
            SoftAssertions.assertSoftly { softly ->
                softly.assertThat(v.versionCode).isEqualTo(7)
                softly.assertThat(v.versionName).isEqualTo("7-bug_123+4")
                softly.assertThat(v.baseBranchCommitCount).isEqualTo(7)
                softly.assertThat(v.featureBranchCommitCount).isEqualTo(4)
                softly.assertThat(v.branchName).isEqualTo("feature/bug_123")
                softly.assertThat(v.currentSha1).isEqualTo("X")
                softly.assertThat(v.baseBranch).isEqualTo("main")
                softly.assertThat(v.initialCommit).isEqualTo("a")
                softly.assertThat(v.localChanges).isEqualTo(NO_CHANGES)
                softly.assertThat(v.yearFactor).isEqualTo(1000)
                softly.assertThat(v.timeComponent).isEqualTo(0)
                softly.assertThat(v.featureBranchOriginCommit).isEqualTo("g")
            }
        }

        // output should be the same
        assertOutput(versioner2)
        assertOutput(versioner)
    }

    @Test
    fun `on feature branch - no commits`() {
        val graph = listOf(
            Commit(sha1 = "X", parent = "f", date = 150_006_000), // <-- main, feature/bug_123, HEAD
            Commit(sha1 = "f", parent = "e", date = 150_005_000),
            Commit(sha1 = "e", parent = "d", date = 150_004_000),
            Commit(sha1 = "d", parent = "c", date = 150_003_000),
            Commit(sha1 = "c", parent = "b", date = 150_002_000),
            Commit(sha1 = "b", parent = "a", date = 150_001_000),
            Commit(sha1 = "a", parent = null, date = 150_000_000)
        )

        val git = MockGitRepo(graph, "X", listOf("X" to "feature/bug_123", "X" to "main"))
        val versioner = GitVersioner(git)

        SoftAssertions.assertSoftly { softly ->
            softly.assertThat(versioner.versionCode).isEqualTo(7)
            softly.assertThat(versioner.versionName).isEqualTo("7-bug_123")
            softly.assertThat(versioner.baseBranchCommitCount).isEqualTo(7)
            softly.assertThat(versioner.featureBranchCommitCount).isEqualTo(0)
            softly.assertThat(versioner.branchName).isEqualTo("feature/bug_123")
            softly.assertThat(versioner.currentSha1).isEqualTo("X")
            softly.assertThat(versioner.baseBranch).isEqualTo("main")
            softly.assertThat(versioner.initialCommit).isEqualTo("a")
            softly.assertThat(versioner.localChanges).isEqualTo(NO_CHANGES)
            softly.assertThat(versioner.yearFactor).isEqualTo(1000)
            softly.assertThat(versioner.timeComponent).isEqualTo(0)
            softly.assertThat(versioner.featureBranchOriginCommit).isEqualTo("X")
        }
    }

    @Test
    fun `on feature branch - no commits - local changes (additions only)`() {
        val graph = listOf(
            Commit(sha1 = "X", parent = "f", date = 150_006_000), // <-- main, feature/bug_123, HEAD
            Commit(sha1 = "f", parent = "e", date = 150_005_000),
            Commit(sha1 = "e", parent = "d", date = 150_004_000),
            Commit(sha1 = "d", parent = "c", date = 150_003_000),
            Commit(sha1 = "c", parent = "b", date = 150_002_000),
            Commit(sha1 = "b", parent = "a", date = 150_001_000),
            Commit(sha1 = "a", parent = null, date = 150_000_000)
        )

        val localChanges = LocalChanges(1, 2, 0)
        val git = MockGitRepo(graph, "X", listOf("X" to "feature/bug_123", "X" to "main"), localChanges)
        val versioner = GitVersioner(git)

        SoftAssertions.assertSoftly { softly ->
            softly.assertThat(versioner.versionCode).isEqualTo(7)
            softly.assertThat(versioner.versionName).isEqualTo("7-bug_123-SNAPSHOT(1 +2 -0)")
            softly.assertThat(versioner.baseBranchCommitCount).isEqualTo(7)
            softly.assertThat(versioner.featureBranchCommitCount).isEqualTo(0)
            softly.assertThat(versioner.branchName).isEqualTo("feature/bug_123")
            softly.assertThat(versioner.currentSha1).isEqualTo("X")
            softly.assertThat(versioner.baseBranch).isEqualTo("main")
            softly.assertThat(versioner.initialCommit).isEqualTo("a")
            softly.assertThat(versioner.localChanges).isEqualTo(localChanges)
            softly.assertThat(versioner.yearFactor).isEqualTo(1000)
            softly.assertThat(versioner.timeComponent).isEqualTo(0)
            softly.assertThat(versioner.featureBranchOriginCommit).isEqualTo("X")
        }
    }

    @Test
    fun `on feature branch - no commits - local changes (deletions only)`() {
        val graph = listOf(
            Commit(sha1 = "X", parent = "f", date = 150_006_000), // <-- main, feature/bug_123, HEAD
            Commit(sha1 = "f", parent = "e", date = 150_005_000),
            Commit(sha1 = "e", parent = "d", date = 150_004_000),
            Commit(sha1 = "d", parent = "c", date = 150_003_000),
            Commit(sha1 = "c", parent = "b", date = 150_002_000),
            Commit(sha1 = "b", parent = "a", date = 150_001_000),
            Commit(sha1 = "a", parent = null, date = 150_000_000)
        )

        val localChanges = LocalChanges(1, 0, 2)
        val git = MockGitRepo(graph, "X", listOf("X" to "feature/bug_123", "X" to "main"), localChanges)
        val versioner = GitVersioner(git)

        SoftAssertions.assertSoftly { softly ->
            softly.assertThat(versioner.versionCode).isEqualTo(7)
            softly.assertThat(versioner.versionName).isEqualTo("7-bug_123-SNAPSHOT(1 +0 -2)")
            softly.assertThat(versioner.baseBranchCommitCount).isEqualTo(7)
            softly.assertThat(versioner.featureBranchCommitCount).isEqualTo(0)
            softly.assertThat(versioner.branchName).isEqualTo("feature/bug_123")
            softly.assertThat(versioner.currentSha1).isEqualTo("X")
            softly.assertThat(versioner.baseBranch).isEqualTo("main")
            softly.assertThat(versioner.initialCommit).isEqualTo("a")
            softly.assertThat(versioner.localChanges).isEqualTo(localChanges)
            softly.assertThat(versioner.yearFactor).isEqualTo(1000)
            softly.assertThat(versioner.timeComponent).isEqualTo(0)
            softly.assertThat(versioner.featureBranchOriginCommit).isEqualTo("X")
        }
    }

    @Test
    fun `on feature branch - one commit`() {
        val graph = listOf(
            Commit(sha1 = "X", parent = "g", date = 150_006_000), // <-- feature/bug_123, HEAD
            Commit(sha1 = "g", parent = "f", date = 150_006_000), // <-- main
            Commit(sha1 = "f", parent = "e", date = 150_005_000),
            Commit(sha1 = "e", parent = "d", date = 150_004_000),
            Commit(sha1 = "d", parent = "c", date = 150_003_000),
            Commit(sha1 = "c", parent = "b", date = 150_002_000),
            Commit(sha1 = "b", parent = "a", date = 150_001_000),
            Commit(sha1 = "a", parent = null, date = 150_000_000)
        )

        val git = MockGitRepo(graph, "X", listOf("X" to "feature/bug_123", "g" to "main"))
        val versioner = GitVersioner(git)

        SoftAssertions.assertSoftly { softly ->
            softly.assertThat(versioner.versionCode).isEqualTo(7)
            softly.assertThat(versioner.versionName).isEqualTo("7-bug_123+1")
            softly.assertThat(versioner.baseBranchCommitCount).isEqualTo(7)
            softly.assertThat(versioner.featureBranchCommitCount).isEqualTo(1)
            softly.assertThat(versioner.branchName).isEqualTo("feature/bug_123")
            softly.assertThat(versioner.currentSha1).isEqualTo("X")
            softly.assertThat(versioner.baseBranch).isEqualTo("main")
            softly.assertThat(versioner.initialCommit).isEqualTo("a")
            softly.assertThat(versioner.localChanges).isEqualTo(NO_CHANGES)
            softly.assertThat(versioner.yearFactor).isEqualTo(1000)
            softly.assertThat(versioner.timeComponent).isEqualTo(0)
            softly.assertThat(versioner.featureBranchOriginCommit).isEqualTo("g")
        }
    }

    @Test
    fun `no feature branch name - like a jenkins PR build`() {
        val graph = listOf(
            Commit(sha1 = "abcdefghij", parent = "g", date = 150_006_000), // <-- HEAD
            Commit(sha1 = "g", parent = "f", date = 150_006_000),
            Commit(sha1 = "f", parent = "e", date = 150_005_000),
            Commit(sha1 = "e", parent = "d", date = 150_004_000), // <-- main
            Commit(sha1 = "d", parent = "c", date = 150_003_000),
            Commit(sha1 = "c", parent = "b", date = 150_002_000),
            Commit(sha1 = "b", parent = "a", date = 150_001_000),
            Commit(sha1 = "a", parent = null, date = 150_000_000)
        )

        val git = MockGitRepo(graph, "abcdefghij", listOf("e" to "main"))
        val versioner = GitVersioner(git)

        SoftAssertions.assertSoftly { softly ->
            softly.assertThat(versioner.versionCode).isEqualTo(5)
            softly.assertThat(versioner.versionName).isEqualTo("5-abcdefg+3")
            softly.assertThat(versioner.baseBranchCommitCount).isEqualTo(5)
            softly.assertThat(versioner.featureBranchCommitCount).isEqualTo(3)
            softly.assertThat(versioner.branchName).isNull()
            softly.assertThat(versioner.currentSha1).isEqualTo("abcdefghij")
            softly.assertThat(versioner.currentSha1Short).isEqualTo("abcdefg")
            softly.assertThat(versioner.baseBranch).isEqualTo("main")
            softly.assertThat(versioner.initialCommit).isEqualTo("a")
            softly.assertThat(versioner.localChanges).isEqualTo(NO_CHANGES)
            softly.assertThat(versioner.yearFactor).isEqualTo(1000)
            softly.assertThat(versioner.timeComponent).isEqualTo(0)
            softly.assertThat(versioner.featureBranchOriginCommit).isEqualTo("e")
        }
    }
}

 */
