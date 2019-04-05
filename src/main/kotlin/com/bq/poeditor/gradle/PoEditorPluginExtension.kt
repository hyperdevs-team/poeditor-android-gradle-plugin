/*
 * Copyright 2020 BQ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bq.poeditor.gradle

/**
 * Extension class that represents the needed params that will
 * be passed to the different tasks of the plugin.
 */
open class PoEditorPluginExtension {
    // PoEditor API TOKEN
    var apiToken = ""
    // PoEditor PROJECT ID
    var projectId = ""
    // Default (and fallback) language code: i.e. "es"
    var defaultLang = "en"
    // Path to res/ directory: i.e. "${project.rootDir}/app/src/main/res"
    var resDirPath = ""
}