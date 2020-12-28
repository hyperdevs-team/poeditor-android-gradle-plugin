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

[Unreleased]: https://github.com/bq/poeditor-android-gradle-plugin/compare/1.4.1...HEAD
[1.4.1]: https://github.com/bq/poeditor-android-gradle-plugin/compare/1.4.0...1.4.1
[1.4.0]: https://github.com/bq/poeditor-android-gradle-plugin/compare/1.3.1...1.4.0
[1.3.1]: https://github.com/bq/poeditor-android-gradle-plugin/compare/1.3.0...1.3.1
[1.3.0]: https://github.com/bq/poeditor-android-gradle-plugin/compare/1.2.0...1.3.0
[1.2.0]: https://github.com/bq/poeditor-android-gradle-plugin/compare/1.1.0...1.2.0
[1.1.0]: https://github.com/bq/poeditor-android-gradle-plugin/compare/1.0.0...1.1.0
[1.0.0]: https://github.com/bq/poeditor-android-gradle-plugin/compare/0.2.5...1.0.0
[0.2.5]: https://github.com/bq/poeditor-android-gradle-plugin/compare/v0.2.4...0.2.5
[0.2.4]: https://github.com/bq/poeditor-android-gradle-plugin/releases/tag/v0.2.4
