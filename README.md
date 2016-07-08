# Poeditor Android Gradle Plugin
Simple plugin that eases importing PoEditor localized strings to your Android project.

What is PoEditor? [Check it out](https://poeditor.com)

Download
--------

Add [jitpack.io](https://jitpack.io/) to your repositories:
```groovy
allprojects {
    repositories { 
        maven { url "https://jitpack.io" }
    }
}
```

Include the dependency:
```groovy
classpath "com.github.bq:poeditor-android-gradle-plugin:0.2.5"
```
Enjoy!

Configuration
--------
Apply and configure the plugin to your app's module build.gradle file.

```groovy
apply plugin: 'com.bq.poeditor'

poEditorPlugin.api_token = <poeditor_api_token>
poEditorPlugin.project_id = <poeditor_project_id> 
poEditorPlugin.default_lang = "en"
poEditorPlugin.res_dir_path = "${project.rootDir}/app/src/main/res"
```

The complete attribute list:

Attribute                     | Description
------------------------------|-----------------------------------------
```api_token```               | Poeditor API Token.
```project_id```              | Poeditor project ID.
```default_lang```            | The lang to be used to build default ```strings.xml``` (```/values``` folder)
```res_dir_path```            | The path to the project's ```/res``` folder.

If you want to customize another property open a PR or leave a comment!

Usage
--------
Just run the new ```importPoEditorStrings``` task via Android Studio or command line:

```
./gradlew importPoEditorStrings
```

This task will:
- download all strings files (every available lang) from PoEditor given the api token and project id.
- process the incoming strings to fix some PoEditor incompatibilities with Android strings system. 
- create and save strings.xml files to ```/values-<lang>``` (or ```/values``` in case of the default lang)

Handle Tablet specific strings
--------
You can mark some strings as tablet specific strings by adding ```_tablet```suffix to the string key in PoEditor. The plugin will extract tablet strings to its own XML and save it in ```values-<lang>-sw600dp```.

Therefore you could define:

Poeditor Strings 

```welcome_message: Hey friend``` and ```welcome_message_tablet: Hey friend how are you doing today, you look great!```

The plugin will create two strings.xml:

/values/strings.xml
```xml
<string name="welcome_message">Hey friend</string>
```

/values-sw600dp/strings.xml
```xml
<string name="welcome_message">Hey friend how are you doing today, you look great!</string>
```

Handle placeholders
--------
You can add placeholders to your strings. We've defined a placeholder markup to use in PoEditor string definition; it uses  {{value}}: 

PoEditor string:

```welcome_message: Hey {{user_name}} how are you``` 

will become, in strings.xml

```xml
<string name="welcome_message">Hey %1%s how are you</string>
```

If you need more than one placeholder in the same string, you can use ordinals:

PoEditor string:

```welcome_message: Hey {1{user_name}} how are you, today offer is {2{current_offer}}``` 

will become, in strings.xml

```xml
<string name="welcome_message">Hey %1%s how are you, today offer is %1%s</string>
```

This way you could change the order of the placeholders depending on the language:

PoEditor string with spanish translation:

```welcome_message: La oferta del día es {2{current_offer}} para ti, {1{user_name}}``` 

will become, in values-es/strings.xml

```xml
<string name="welcome_message">La oferta del día es %2%s para ti, %1%s</string>
```

To-Do
-------
* Manage language specializations: i.e. values-es-rMX for Mexican.
* Change placeholder system to avoid using ordinals by taking into account the placeholder value instead.

License
-------
This project is licensed under the Apache Software License, Version 2.0.

    Copyright (c) 2016 bq

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
