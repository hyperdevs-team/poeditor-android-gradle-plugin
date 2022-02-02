# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

<!--
## [Unreleased]
### Added
- No new features!
### Changed
- No changed features!
### Deprecated
- No deprecated features!
### Removed
- No removed features!
### Fixed
- No fixed issues!
### Security
- No security issues fixed!
-->

## [Unreleased]
### Added
- No new features!
### Changed
- No changed features!
### Deprecated
- No deprecated features!
### Removed
- No removed features!
### Fixed
- No fixed issues!
### Security
- No security issues fixed!

## [3.0.0] - 2022-02-02
### Changed
- BREAKING: Update dependencies to work with Android Gradle Plugin 7.+. This will break compatibility with
projects that use versions lower than 7.0

## [2.4.2] - 2021-10-17
### Changed
- Change detekt rules according to library update to version 1.18.1
### Removed
- Remove `jcenter()` from project

## [2.4.1] - 2021-09-16
### Fixed
- Fix CDATA strings not parsed correctly
- Fix `<` and `>` not getting properly unescaped in CDATA strings

## [2.4.0] - 2021-07-26
### Added
- Add `filters` parameter to `poEditorConfig` block to specify the POEditor filters to use for all languages. _Thanks to [@nokite](https://github.com/nokite) for the contribution!_
<details open><summary>Groovy</summary>

```groovy
poEditor {
    apiToken = "your_api_token"
    projectId = 12345
    defaultLang = "en"
    filters = ["translated", "not_fuzzy"]
}
```
    
</details>

<details><summary>Kotlin</summary>

```kotlin
poEditor {
    apiToken = "your_api_token"
    projectId = 12345
    defaultLang = "en"
    filters = listOf("translated", "not_fuzzy")
}
```
    
</details>

## [2.3.0] - 2021-07-22
### Added
- Add some code style rules to be shared via editorconfig (`insert_final_newline=false`). _Thanks to [@nokite](https://github.com/nokite) for the contribution!_
- Add `minimumTranslationPercentage` parameter to `poEditorConfig` block to specify the minimum accepted percentage of translated strings per language. _Thanks to [@nokite](https://github.com/nokite) for the contribution!_
<details open><summary>Groovy</summary>

```groovy
poEditor {
    apiToken = "your_api_token"
    projectId = 12345
    defaultLang = "en"
    minimumTranslationPercentage = 85
}
```
    
</details>

<details><summary>Kotlin</summary>

```kotlin
poEditor {
    apiToken = "your_api_token"
    projectId = 12345
    defaultLang = "en"
    minimumTranslationPercentage = 85
}
```
    
</details>

### Changed
- The XML attribute `encoding` in the generated string resource files is now lowercase `utf-8`. _Thanks to [@nokite](https://github.com/nokite) for the contribution!_
- Migrate to PoEditor API v2.
- Update some dependencies (Gradle 6.9, Kotlin 1.4.20, Moshi 1.12.0, OkHttp 4.9.1, Junit 4.13.2). _Thanks to [@nokite](https://github.com/nokite) for the contribution!_
- Update deprecated Gradle property `classifier` to `archiveClassifier` _Thanks to [@nokite](https://github.com/nokite) for the contribution!_
- Update `Project.xml` (matching Android Studio 4.2.1) _Thanks to [@nokite](https://github.com/nokite) for the contribution!_

## [2.2.1] - 2021-05-28
### Changed
- Increase connect, read and write timeouts to 30 seconds

## [2.2.0] - 2021-05-25
### Added
- Add `languageValuesOverridePathMap` to override `values` folder for specific languages.
<details open><summary>Groovy</summary>

```groovy
poEditor {
  apiToken = "your_api_token"
  projectId = 12345
  defaultLang = "en"
  languageValuesOverridePathMap = [
          "free" : "${rootDir}/app/src/free/res/values",
          "paid" : "${rootDir}/app/src/paid/res/values"
  ]
}
```

</details>

<details><summary>Kotlin</summary>

```kotlin
poEditor {
  apiToken = "your_api_token"
  projectId = 12345
  defaultLang = "en"
  languageValuesOverridePathMap = mapOf(
          "free" to "${rootDir}/app/src/free/res/values",
          "paid" to "${rootDir}/app/src/paid/res/values"
  )
}
```

</details>

## [2.1.2] - 2021-05-18
### Fixed
- Fix parse date for empty language. _Thanks to [@wojciechkryg](https://github.com/wojciechkryg) and [@rafid059](https://github.com/rafid059) for the contribution!_

## [2.1.1] - 2021-05-06
### Fixed
- Fix undefined tags crashing the plugin

## [2.1.0] - 2021-05-05
### Added
- Add `tags` parameter to `poEditorConfig` block to add PoEditor tags:
<details open><summary>Groovy</summary>

```groovy
poEditor {
  apiToken = "your_api_token"
  projectId = 12345
  defaultLang = "en"
  tags = ["tag1", "tag2"] // Download strings with the specified tags
}
```

</details>

<details><summary>Kotlin</summary>

```kotlin
poEditor {
  apiToken = "your_api_token"
  projectId = 12345
  defaultLang = "en"
  tags = listOf("tag1", "tag2")
}
```
  
</details>

### Changed
- Add support for Android Gradle Plugin version 4.2.0
### Removed
- Remove support for Android Gradle Plugin versions lower than 4.2.0

## [2.0.0] - 2021-04-28
### Changed
- Change repo ownership to [hyperdevs-team](https://github.com/hyperdevs-team). Thanks [bq](https://github.com/bq) for all the work!
- Change package names from `com.bq.*` to `com.hyperdevs.*`

## [1.4.2] - 2020-12-29
### Added
- [Dotenv support](https://github.com/cdimascio/dotenv-kotlin) for the `Main.kt` file for easier local development.
### Fixed
- Fix HTML tags being escaped when parsing.

## [1.4.1] - 2020-12-28
### Fixed
- Fix percent symbols not being properly escaped (again) by processing the XML file line by line.

## [1.4.0] - 2020-12-25
### Added
- Add the `enabled` variable to enable or disable specific configurations.
### Removed
- Remove tasks that are disabled with the `enabled` flag or not configured.
### Fixed
- Fix an issue that didn't save flavor or build type specific strings in their default resources folder.

## [1.3.1] - 2020-12-21
### Fixed
- Fix percent symbols being escaped when variables are present.

## [1.3.0] - 2020-09-28
### Added
- Add support for using the plug-in in library modules.
- Add support for configuring non-standard resource directory path via `defaultResPath`. _Thanks to [@rafid059](https://github.com/rafid059) for the contribution!_
<details open><summary>Groovy</summary>

```groovy
poEditor {
    defaultResPath = "your/res/path"
}

android {
    // If you have the following flavors...
    flavorDimensions 'type'
    productFlavors {
        free { dimension 'type' }
        paid { dimension 'type' }
    }

    poEditorConfig {
        free {
            // Configuration for the free flavor, same syntax as usual
            defaultResPath = "your/free/res/path"
        }
        paid {
            // Configuration for the paid flavor, same syntax as usual
            defaultResPath = "your/paid/res/path"
        }
        debug {
            // Configuration for the debug build type, same syntax as usual
            defaultResPath = "your/debug/res/path"
        }
        release {
            // Configuration for the release build type, same syntax as usual
            defaultResPath = "your/release/res/path"
        }
    }
}
```

</details>

<details><summary>Kotlin</summary>

```kotlin
poEditor {
    defaultResPath = "your/res/path"
}

android {
    // If you have the following flavors...
    flavorDimensions("type")

    productFlavors {
        register("free") { setDimension("type") }
        register("paid") { setDimension("type") }
    }

    poEditorConfig {
        register("free") {
            // Configuration for the free flavor, same syntax as usual
            defaultResPath = "your/free/res/path"
        }
        register("paid") {
            // Configuration for the paid flavor, same syntax as usual
            defaultResPath = "your/paid/res/path"
        }
        register("debug") {
            // Configuration for the debug build type, same syntax as usual
            defaultResPath = "your/debug/res/path"
        }
        register("release") {
            // Configuration for the release build type, same syntax as usual
            defaultResPath = "your/release/res/path"
        }
    }
}
```
</details>

## [1.2.0] - 2020-09-03
### Added
- Add proper support for `plurals`

## [1.1.0] - 2020-08-14
### Added
- Add support for flavor and build type configuration. The sample configuration is as follows:
<details open><summary>Groovy</summary>

```groovy
poEditor {
    // Default config that applies to all flavor/build type configurations. 
    // Also executed when calling 'importPoEditorStrings'
}

android {
    // If you have the following flavors...
    flavorDimensions 'type'
    productFlavors {
        free { dimension 'type' }
        paid { dimension 'type' }
    }

    poEditorConfig {
        free {
            // Configuration for the free flavor, same syntax as usual
        }
        paid {
            // Configuration for the paid flavor, same syntax as usual
        }
        debug {
            // Configuration for the debug build type, same syntax as usual
        }
        release {
            // Configuration for the release build type, same syntax as usual
        }
    }
}
```

</details>

<details><summary>Kotlin</summary>

```kotlin
poEditor {
    // Default config that applies to all flavor/build type configurations. 
    // Also executed when calling 'importPoEditorStrings'
}

android {
    // If you have the following flavors...
    flavorDimensions("type")

    productFlavors {
        register("free") { setDimension("type") }
        register("paid") { setDimension("type") }
    }

    poEditorConfig {
        register("free") {
            // Configuration for the free flavor, same syntax as usual
        }
        register("paid") {
            // Configuration for the paid flavor, same syntax as usual
        }
        register("debug") {
            // Configuration for the debug build type, same syntax as usual
        }
        register("release") {
            // Configuration for the release build type, same syntax as usual
        }
    }
}
```
</details>

### Removed
- The `resDirPath` parameter is no longer needed, since it gets inferred from the flavor or build type configured in the app

## [1.0.0] - 2020-07-21
### Added
- Full project refactor in order to improve general stability and reliability of the plug-in.

### Fixed
- The plug-in does not rely on `curl` commands now. 
  Instead it now uses [OkHttp](https://square.github.io/okhttp/) and [Retrofit](https://square.github.io/retrofit/) libraries.
- Tablet strings folder are not generated if no tablet strings are found.

### Changed
- The plugin name to use is now `poEditor` instead of `poEditorPlugin`.
- The input parameters have been changed from snake case to camel case:
```
api_token -> apiToken
project_id -> projectId
default_lang -> defaultLang
res_dir_path -> resDirPath
```

## [0.2.5] - 2016-07-08
### Fixed
- Fix build process to allow integration via JitPack.

## [0.2.4] - 2016-07-08
### Added
- Initial release.

[Unreleased]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/3.0.0...HEAD
[3.0.0]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/2.4.2...3.0.0
[2.4.2]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/2.4.1...2.4.2
[2.4.1]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/2.4.0...2.4.1
[2.4.0]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/2.3.0...2.4.0
[2.3.0]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/2.2.1...2.3.0
[2.2.1]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/2.2.0...2.2.1
[2.2.0]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/2.1.2...2.2.0
[2.1.2]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/2.1.1...2.1.2
[2.1.1]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/2.1.0...2.1.1
[2.1.0]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/2.0.0...2.1.0
[2.0.0]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/1.4.2...2.0.0
[1.4.2]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/1.4.1...1.4.2
[1.4.1]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/1.4.0...1.4.1
[1.4.0]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/1.3.1...1.4.0
[1.3.1]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/1.3.0...1.3.1
[1.3.0]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/1.2.0...1.3.0
[1.2.0]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/1.1.0...1.2.0
[1.1.0]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/1.0.0...1.1.0
[1.0.0]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/0.2.5...1.0.0
[0.2.5]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/compare/v0.2.4...0.2.5
[0.2.4]: https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/releases/tag/v0.2.4
