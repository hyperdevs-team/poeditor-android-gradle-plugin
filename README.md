# PoEditor Android Gradle Plug-in
[![Release](https://jitpack.io/v/hyperdevs-team/poeditor-android-gradle-plugin.svg)](https://jitpack.io/#hyperdevs-team/poeditor-android-gradle-plugin)

Simple plug-in that eases importing [PoEditor](https://poeditor.com) localized strings to your Android project.

## Purpose
This plug-in super-charges your Android project by providing tasks to download your localized strings from the PoEditor service into you Android project.
It also provides a built-in syntax to handle placeholders to enhance the already awesome Android support from PoEditor.

## Minimum requirements
* Gradle 8.2 or above
* Android Gradle Plug-in 8.0 or above

## Setting Up
In your main `build.gradle`, add [jitpack.io](https://jitpack.io/) repository in the `buildscript` block and include the plug-in as a dependency:

<details open><summary>Groovy</summary>

```groovy
buildscript {
    repositories { 
        maven { url "https://jitpack.io" }
    }
    dependencies {
        classpath "com.github.hyperdevs-team:poeditor-android-gradle-plugin:<latest_version>"
    }
}
```

</details>

<details><summary>Kotlin (using classpath)</summary>

```kotlin
buildscript {
    repositories { 
        maven { url = java.net.URI("https://jitpack.io") }
    }
    dependencies {
        classpath("com.github.hyperdevs-team:poeditor-android-gradle-plugin:<latest_version>")
    }
}
```

</details>

<details><summary>Kotlin (using the plugins block)</summary>

Top-level `settings.gradle.kts`
```kotlin
pluginManagement {
    repositories {
        maven { url = java.net.URI("https://jitpack.io") }
    }
    resolutionStrategy {
        eachPlugin {
            // Add custom plugin ID for the PoEditor plugin.
            // This is required because the plugin is not published in the Gradle plugin portal.
            if (requested.id.id == "com.hyperdevs.poeditor") {
                useModule("com.github.hyperdevs-team:poeditor-android-gradle-plugin:${requested.version}")
            }
        }
    }
}
```

Top-level `build.gradle.kts`
```kotlin
plugins {
    id("com.hyperdevs.poeditor") version "<latest_version>" apply false
}
```

</details>

## How to use
Apply and configure the plug-in in your app's `build.gradle` file:
<details open><summary>Groovy</summary>

```groovy
apply plugin: "com.android.application"
apply plugin: "com.hyperdevs.poeditor"

poEditor {
    apiToken = "your_api_token"
    projectId = 12345
    defaultLang = "en"
}
```

</details>

<details><summary>Kotlin</summary>

```kotlin
plugins {
    id("com.android.application")
    id("com.hyperdevs.poeditor")
}

poEditor {
    apiToken.set("your_api_token")
    projectId.set(12345)
    defaultLang.set("en")
}
```

</details>

The complete attribute list is the following:

Attribute                              | Description
---------------------------------------|-----------------------------------------
```apiToken```                         | PoEditor API Token.
```projectId```                        | PoEditor project ID.
```defaultLang```                      | (Optional) The lang to be used to build default ```strings.xml``` (```/values``` folder). Defaults to English (`en`).
```defaultResPath```                   | (Since 1.3.0) (Optional) Path where the plug-in should dump strings. Defaults to the module's default (or build variant) `res` path.
```enabled```                          | (Since 1.4.0) (Optional) Enables the generation of the block's related task. Defaults to `true`.
```tags```                             | (Since 2.1.0) (Optional) List of PoEditor tags to download. Defaults to empty list.
```languageValuesOverridePathMap```    | (Since 2.2.0) (Optional) Map of `language_code:path` entries that you want to override the default language values folder with. Defaults to empty map.
```minimumTranslationPercentage```     | (Since 2.3.0) (Optional) The minimum accepted percentage of translated strings per language. Languages with fewer translated strings will not be fetched. Defaults to no minimum, allowing all languages to be fetched.
```filters```                          | (Since 2.4.0) (Optional) List of PoEditor filters to use during download. Defaults to empty list. Accepted values are defined by the POEditor API.
```resFileName```                      | (Since 3.1.0) (Optional) Sets the file name for the imported string resource XML files. Defaults to `strings`.
```order```                            | (Since 3.1.0) (Optional) Defines how to order the export. Accepted values are defined by the POEditor API.
```unquoted```                         | (Since 3.2.0) (Optional) Defines if the strings should be unquoted, overriding default PoEditor configuration. Defaults to `false`.
```unescapeHtmlTags```                 | (Since 3.4.0) (Optional) Whether or not to unescape HTML entitites from strings. Defaults to true.
```untranslatableStringsRegex```       | (Since 4.2.0) (Optional) Pattern to use to mark strings as translatable=false in the strings file. Defaults to null.

After the configuration is done, just run the new ```importPoEditorStrings``` task via Android Studio or command line:

```
./gradlew importPoEditorStrings
```

This task will:
* Download all strings files (every available lang) from PoEditor given the api token and project id.
* Process the incoming strings to fix some PoEditor incompatibilities with Android strings system. 
* Create and save strings.xml (or whatever file name you desire by using the `resFileName` attribute) files to ```/values-<lang>``` (or ```/values``` in case of the default lang). It supports
region specific languages by creating the proper folders (i.e. ```/values-es-rMX```).

## Enhanced syntax
The plug-in enhances your PoEditor experience by adding useful features over your project by adding some custom syntax for certain tasks.

### Variables
The plug-in does not parse string placeholders, instead it uses variables with a specific markup to use in PoEditor's string definition: it uses a double braces syntax to declare them.
This allows more clarity for translators that use the platform, since it allows them to know what the placeholders really mean and better reuse them in translations.

For example, the PoEditor string:

```
welcome_message: Hey {{user_name}} how are you
``` 

will become, in `strings.xml`

```xml
<string name="welcome_message">Hey %1$s how are you</string>
```

If you need more than one variable in the same string, you can also use ordinals. The string:

```
welcome_message: Hey {1{user_name}} how are you, today offer is {2{current_offer}}
``` 

will become, `in strings.xml`

```xml
<string name="welcome_message">Hey %1$s how are you, today offer is %2$s</string>
```

This way you can change the order of the placeholders depending on the language:

The same string, with the following Spanish translation:

```
welcome_message: La oferta del día es {2{current_offer}} para ti, {1{user_name}}
``` 

will become, in `values-es/strings.xml`

```xml
<string name="welcome_message">La oferta del día es %2$s para ti, %1$s</string>
```

### Tablet specific strings
You can mark some strings as tablet specific strings by adding ```_tablet```suffix to the string key in PoEditor. 
The plug-in will extract tablet strings to its own XML and save it in ```values-<lang>-sw600dp```.

If you define the following string in PoEditor:
```welcome_message: Hey friend``` and ```welcome_message_tablet: Hey friend how are you doing today, you look great!```

The plug-in will create two `strings.xml` files:

`/values/strings.xml`
```xml
<string name="welcome_message">Hey friend</string>
```

`/values-sw600dp/strings.xml`
```xml
<string name="welcome_message">Hey friend how are you doing today, you look great!</string>
```

## Handling multiple flavors and build types
Sometimes we might want to import different strings for a given flavor (for example, in white label apps, we could have
different string definitions depending on the brand where they're used). The plugin supports this kind of apps by providing
specific configurations via the `poEditorConfig` block.

Let's see an example configuration:

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
            // Configuration for the free flavor, same syntax as the standard 'poEditor' block
        }
        paid {
            // Configuration for the paid flavor, same syntax as the standard 'poEditor' block
        }
        debug {
            // Configuration for the debug build type, same syntax as the standard 'poEditor' block
        }
        release {
            // Configuration for the release build type, same syntax as the standard 'poEditor' block
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
            // Configuration for the free flavor, same syntax as the standard 'poEditor' block
        }
        register("paid") {
            // Configuration for the paid flavor, same syntax as the standard 'poEditor' block
        }
        register("debug") {
            // Configuration for the debug build type, same syntax as the standard 'poEditor' block
        }
        register("release") {
            // Configuration for the release build type, same syntax as the standard 'poEditor' block
        }
    }
}
```

</details>

Each flavor (`free` and `paid`) and build type (`debug` and `release`) will have its own task to import strings for said
configuration: `importFreePoEditorStrings`, `importPaidPoEditorStrings`, `importDebugPoEditorStrings` and
`importReleasePoEditorStrings`.

Now the `importPoEditorStrings` task will import the main strings configured in the `poEditor` block and also the
strings for each defined flavor or build type.`

## Handling library modules
> Requires version 1.3.0 of the plug-in

You can also apply the plug-in to library modules. Here's an example:
Apply and configure the plug-in in your library's `build.gradle` file:
<details open><summary>Groovy</summary>

```groovy
apply plugin: "com.android.library"
apply plugin: "com.hyperdevs.poeditor"

poEditor {
    apiToken = "your_api_token"
    projectId = 12345
    defaultLang = "en"
}
```

</details>

<details><summary>Kotlin</summary>

```kotlin
plugins {
    id "com.android.library"
    id "com.hyperdevs.poeditor"
}

poEditor {
    apiToken = "your_api_token"
    projectId = 12345
    defaultLang = "en"
}
```

</details>

You can also apply flavor and build type-specific configurations as you would do when setting them up with application modules.
The plug-in will generate the proper tasks needed to import the strings under your module: `:<your_module>:import<your_flavor_or_build_type_if_any>PoEditorStrings`

## Disabling task generation for specific configurations
> Requires version 1.4.0 of the plug-in

There may be some cases where you only want certain configurations to have a related task.
One of these examples may be to only have tasks for the configured flavors or build types, but you don't want to have the
main `poEditor` block to download any strings. For these cases you have the `enabled` variable that you can set to false
when you want to disable a configuration.

Keep in mind that, if you disable the main `poEditor` block, you'll need to enable each specific configuration separately
since they inherit the main block configuration. Let's see how this works:

<details open><summary>Groovy</summary>

```groovy
poEditor {
    // Default config that applies to all flavor/build type configurations. 
    // Also executed when calling 'importPoEditorStrings'
    enabled = false // This'll disable task generation for every configuration.
    apiToken = "your_common_api_token"
}

android {
    flavorDimensions 'type'
    productFlavors {
        free { dimension 'type' }
        paid { dimension 'type' }
    }

    poEditorConfig {
        free {
            // Specific configuration for the free flavor
            enabled = true // Explicitly enabled since the main block disables task generation
            projectId = 12345
        }
        paid {
            // Specific configuration for the paid flavor
            enabled = true // Explicitly enabled since the main block disables task generation
            projectId = 54321
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
    enabled = false // This'll disable task generation for every configuration.
    apiToken = "your_common_api_token"
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
            // Specific configuration for the free flavor
            enabled = true // Explicitly enabled since the main block disables task generation
            projectId = 12345
        }
        register("paid") {
            // Specific configuration for the paid flavor
            enabled = true // Explicitly enabled since the main block disables task generation
            projectId = 54321
        }
    }
}
```

</details>

## Handling tags
> Requires version 2.1.0 of the plug-in

You can also select the tags that you want strings to be downloaded from PoEditor, based on the tags that you defined in
your PoEditor project.

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

## Overriding default values folder for specific languages
> Requires version 2.2.0 of the plug-in

Sometimes you may want to override the default `values` path where the plug-in stores the downloaded strings.
For example, you may have a project where you have custom languages suited to your app flavors in PoEditor
(let's say `free` and `paid`). The plug-in would create two folders in your app's `res` folder 
(`values-free` and `values-paid`) by default; this would not be ideal if you want the string values to their respective
flavors.

You can add the parameter `languageValuesOverridePathMap` to your `poEditor` or `poEditorConfig` block to change the 
path of the `values` folder where the strings file will be stored for a given language code:

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

## Tweaking minimum translation percentages
> Requires version 2.3.0 of the plug-in

The plug-in also allows setting a minimum percentage of translated strings to download languages. This is set-up with the `minimumTranslationPercentage` parameter in your `poEditor` or `poEditorConfig` blocks:

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


## Handling filters
> Requires version 2.4.0 of the plug-in

The plug-in also allows setting filters for narrowing down the type of terms to be downloaded. 
Supported filters are defined by the POEditor API and currently include:  `translated`, `untranslated`, `fuzzy`, `not_fuzzy`, `automatic`, `not_automatic`, `proofread`, `not_proofread`. 
At the moment it's not possible to set different filters per language.  
This is set-up with the optional `filters` parameter in your `poEditor` or `poEditorConfig` blocks:

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

## Toggling HTML unescaping
> Requires version 3.4.0 of the plug-in

You can enable or disable HTML tags unescaping with the `unescapeHtmlTags` flag.

<details open><summary>Groovy</summary>

```groovy
poEditor {
    apiToken = "your_api_token"
    projectId = 12345
    defaultLang = "en"
    unescapeHtmlTags = false
}
```

</details>

<details><summary>Kotlin</summary>

```kotlin
poEditor {
    apiToken = "your_api_token"
    projectId = 12345
    defaultLang = "en"
    unescapeHtmlTags = false
}
```

</details>

## Creating extra PoEditor tasks
> Requires version 4.1.0 of the plug-in

You can create extra PoEditor tasks to import strings for other projects, for example. You can do so by adding this to
your `build.gradle(.kts)` file:

<details open><summary>Kotlin</summary>

```kotlin
tasks.register("importCustomPoEditorStrings", ImportPoEditorStringsTask::class.java) {
    description = "Imports custom strings from POEditor."
    group = "strings"

    apiToken = "another_token_from_a_different_project"
    projectId = 12345
    resFileName = "strings_custom"
}
```

</details>

## Mark strings as untranslatable
> Requires version 4.2.0 of the plug-in

You can use the `untranslatableStringsRegex` property to define a regex to mark matching PoEditor string keys as 
untranslatable.
These strings will be marked as `translatable="false"` in the final strings file.

<details open><summary>Groovy</summary>

```groovy
poEditor {
    apiToken = "your_api_token"
    projectId = 12345
    defaultLang = "en"
    untranslatableStringsRegex = "(.*)"
}
```

</details>

<details><summary>Kotlin</summary>

```kotlin
poEditor {
    apiToken = "your_api_token"
    projectId = 12345
    defaultLang = "en"
    untranslatableStringsRegex = "(.*)"
}
```

</details>

Keep in mind that the regex must match the whole string name and not just a part, as it relies on 
[`CharSequence.matches(Regex)`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/matches.html) from the 
Kotlin API.

## iOS alternative
If you want a similar solution for your iOS projects, check this out: [poeditor-parser-swift](https://github.com/hyperdevs-team/poeditor-parser-swift)

## Authors & contributors
* **[Iván Martínez](https://github.com/imartinez)** - *Initial work*
* **[Adrián García](https://github.com/adriangl)** - *Maintainer*

Thanks to all the people who contributed to making the plug-in better!

<a href="https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=hyperdevs-team/poeditor-android-gradle-plugin" />
</a>

## Acknowledgements
The work in this repository up to April 28th, 2021 was done by [bq](https://github.com/bq).
Thanks for all the work!!

## License
This project is licensed under the Apache Software License, Version 2.0.
```
   Copyright 2021 HyperDevs
   
   Copyright 2016 BQ

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
