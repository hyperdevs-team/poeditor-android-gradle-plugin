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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

/**
 * Main plugin class that registers the tasks used by the plugin.
 */
class PoEditorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Add the 'poEditorPlugin' extension object, used to pass parameters to the task
        project.extensions.create<PoEditorPluginExtension>("poEditor")

        // Registers the task
        project.tasks.register("importPoEditorStrings", ImportPoEditorStringsTask::class)
    }
}