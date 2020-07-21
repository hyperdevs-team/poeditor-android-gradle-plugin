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

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.findByType

/**
 * Task that:
 * 1. downloads all strings files (every available lang) from PoEditor given a api_token and project_id.
 * 2. extracts "tablet" strings to another XML (strings with the suffix "_tablet")
 * 3. creates and saves two strings.xml files to values-<lang> and values-<lang>-sw600dp (tablet specific strings)
 */
open class ImportPoEditorStringsTask : DefaultTask() {

    private val extension: PoEditorPluginExtension by lazy {
        project.extensions.findByType(PoEditorPluginExtension::class) as PoEditorPluginExtension
    }

    /**
     * Main task entrypoint.
     */
    @TaskAction
    @Suppress("ThrowsCount")
    fun importPoEditorStrings() {
        // Check if needed extension and parameters are set
        val apiToken: String
        val projectId: Int
        val defaultLang: String
        val resDirPath: String

        try {
            apiToken = requireNotNull(extension.apiToken) {
                throw IllegalArgumentException("Missing parameter: apiToken") }
            projectId = try {
                requireNotNull(extension.projectId) {
                    throw IllegalArgumentException("Missing parameter: projectId") }.toInt()
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Parameter projectId is not valid")
            }
            defaultLang = requireNotNull(extension.defaultLang) {
                throw IllegalArgumentException("Missing parameter: defaultLang") }
            resDirPath = requireNotNull(extension.resDirPath) {
                throw IllegalArgumentException("Missing parameter: resDirPath") }
        } catch (e: Exception) {
            throw IllegalArgumentException(
                """
                    You need to define the following variables in your build.gradle: 
                        poEditor {
                            apiToken = <your_api_token>
                            projectId = <your_project_id>
                            defaultLang = <default_project_lang>
                            resDirPath = <your_project_resources_dir_path>
                        }
                        
                    Cause of error: ${e.message}
                    """.trimIndent()
            )
        }

        PoEditorStringsImporter.importPoEditorStrings(apiToken, projectId, defaultLang, resDirPath)
    }
}