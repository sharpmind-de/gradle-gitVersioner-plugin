# GitVersioner for Gradle

An independently maintained continuation of [Pascal Welsch's original GitVersioner plugin](https://github.com/passsy/gradle-gitVersioner-plugin), now maintained by [SharpMind](https://github.com/sharpmind-de).

The upstream project was archived in November 2024. This standalone project preserves its history and continues development under the SharpMind plugin ID `de.sharpmind.gitversioner`.

## Overview

GitVersioner simplifies versioning for Gradle projects, inspired by SVN's revision numbering. In Git, branching complicates linear revision tracking, but GitVersioner restores simplicity while embracing Git's flexibility.

## Features

- Automatically generates version codes and names based on:
  - The number of commits in the base branch.
  - The number of commits in the current feature branch.
  - Uncommitted changes.
- Supports feature branches with unique identifiers and snapshot details for uncommitted changes.
- Customizable versioning properties and formatter.
- Generates a version file `build/gitversion/gitversion.properties` with version and Git details.

## Tasks

- **`generateGitVersionName`**: Generates a properties file with versioning details.
- **`gGVN`**: Alias for `generateGitVersionName`.

## Usage

```shell
% git status
On branch main
nothing to commit, working tree clean

./gradlew generateGitVersionName

# Output
git versionName: 3

% git checkout -b dev
Switched to a new branch 'dev'

# After two commits on dev

./gradlew generateGitVersionName

# Output
git versionName: 3-dev+2
```

#### Explanation

- **Base branch** (`main`) has 3 commits.
- **Feature branch** (`dev`) has 2 additional commits.
- The default formatter adds the sanitized branch name and feature-branch commit count.
- Uncommitted changes add a suffix such as `-SNAPSHOT(1 +1 -0)`.

The generated version `3-dev+2` follows the default version formatter.

### Configuring the Version Formatter

The default formatter generates versions based on specific rules, but you can configure it using the `formatter` property as shown below.

## Installation and Configuration

### Basic Plugin Setup

**build.gradle**
```groovy
plugins {
    id 'de.sharpmind.gitversioner' version '0.7.0'
}
```

**build.gradle.kts**
```kotlin
plugins {
    id("de.sharpmind.gitversioner") version "0.7.0"
}
```

Version `0.7.0` is published on the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/de.sharpmind.gitversioner/0.7.0).

### Compatibility

The current project build is verified with the following toolchain:

| Component | Version |
| --- | --- |
| GitVersioner | `0.7.0` |
| Gradle Wrapper | `9.3.1` |
| Kotlin | `2.3.10` |
| Java toolchain | `17` |

These versions describe the maintained build baseline. Compatibility with other Gradle or Java versions should be verified before production use.

### Full Configuration

**build.gradle**
```groovy
import de.sharpmind.gitversioner.GitVersioner

plugins {
    id 'de.sharpmind.gitversioner' version '0.7.0'
}

configure(GitVersioner) {
    baseBranch = 'main' // Default base branch
    yearFactor = 0

    // Default formatter properties
    addSnapshot = false // Adds a "-SNAPSHOT" suffix
    addLocalChangesDetails = false

    formatter = { v ->
        def sb = new StringBuilder()
        sb.append(v.versionCode)

        def branchCommitCount = v.featureBranchCommitCount
        def localChangesCount = v.localChanges.filesChanged

        if (v.branchName != v.baseBranch) {
            sb.append(".").append(v.branchName).append("-").append(branchCommitCount)
        }

        if (localChangesCount > 0) {
            sb.append(".").append(localChangesCount)
        }

        sb.toString()
    }

    project.version = this.versionName
    project.ext.set("branchName", this.branchName)
}
```

**build.gradle.kts**
```kotlin
import de.sharpmind.gitversioner.GitVersioner

plugins {
    id("de.sharpmind.gitversioner") version "0.7.0"
}

configure<GitVersioner> {
    baseBranch = "main" // Default base branch
    yearFactor = 0

    // Default formatter properties
    addSnapshot = false // Adds a "-SNAPSHOT" suffix
    addLocalChangesDetails = false

    formatter = { v ->
        val sb = StringBuilder()
        sb.append(v.versionCode)

        val branchCommitCount = v.featureBranchCommitCount
        val localChangesCount = v.localChanges.filesChanged

        if (v.branchName?.let { v.baseBranch.compareTo(it) } != 0) {
            sb.append(".").append(v.branchName).append("-").append(branchCommitCount)
        }

        if (localChangesCount > 0) {
            sb.append(".").append(localChangesCount)
        }

        sb.toString()
    }

    project.version = this.versionName
    project.ext.set("branchName", this.branchName)
}
```

## Generated Properties File

Run the following command to generate the version properties file:

```shell
./gradlew generateGitVersionName
```

**Output File**: `build/gitversion/gitversion.properties`

**Sample Content:**

```properties
# gitVersioner plugin - extracted data from Git repository
# Thu Dec 26 14:20:43 CET 2024
baseBranch=main
baseBranchCommitCount=1
branchName=dev
currentSha1=91e13e9258625f3bd0399f2083d164f52e78147c
featureBranchCommitCount=358
localChanges=1 +0 -3
timeComponent=0
versionCode=1
versionName=1.dev-358.1
yearFactor=0
```

The version.properties file is automatically available in the classpath and can be accessed using the following code:

Sample kotlin object to access the version properties file and offer methods to access the properties:

```kotlin
package de.sharpmind.shared.service

import org.slf4j.LoggerFactory
import java.util.*

object GitInfoService {
  private val gitProperties = Properties()
  private const val GIT_PROPERTIES_FILE_NAME = "version.properties"
  private val logger = LoggerFactory.getLogger(GitInfoService::class.java)

  init {
    // read git info file if it exists
    val resourceURL = this::class.java.classLoader.getResource(GIT_PROPERTIES_FILE_NAME)

    if (resourceURL != null) {
      gitProperties.load(resourceURL.openStream())
      logger.debug("Loaded git properties from $GIT_PROPERTIES_FILE_NAME")
      logger.debug("git properties: {}", gitProperties)
    } else {
      logger.warn("Could not load git properties. File $GIT_PROPERTIES_FILE_NAME not found")
    }
  }

  fun get(propKey: String): String? = gitProperties.getProperty(propKey)

  fun getPropertyNames(): Set<String> = gitProperties.stringPropertyNames()

  // convenience methods
  fun getVersion(): String? = get("versionName")
}
```

That helps to access the version properties in your application code.

## Maintenance and Support

SharpMind maintains this continuation independently from the archived upstream project. Bug reports, compatibility findings, and focused pull requests are welcome in [GitHub Issues](https://github.com/sharpmind-de/gradle-gitVersioner-plugin/issues).

When reporting a problem, include the GitVersioner, Gradle, Java, and Kotlin versions, along with a minimal reproduction when possible.

## Releasing

For maintainers:

1. Update `version` in `gradle.properties` and the documented plugin version in this README.
2. Run `./gradlew clean check`.
3. Commit the release change and create the corresponding Git tag.
4. Publish with `./gradlew publishPlugins` using configured Gradle Plugin Portal credentials.
5. Push the tag and create the GitHub release notes.

## License

```
Copyright 2022-2026 SharpMind

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Acknowledgements

This project derives from [Pascal Welsch's GitVersioner](https://github.com/passsy/gradle-gitVersioner-plugin) and retains the original Git history and attribution.

The original repository was archived by its owner on November 19, 2024, and is read-only. This SharpMind repository is the independently maintained continuation.

### Original License

```
Copyright 2017 Pascal Welsch

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
