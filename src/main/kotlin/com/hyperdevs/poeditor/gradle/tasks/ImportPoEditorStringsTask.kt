/*
 * Copyright 2021 HyperDevs
 *
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

package com.hyperdevs.poeditor.gradle.tasks

import com.hyperdevs.poeditor.gradle.PoEditorPluginExtension
import com.hyperdevs.poeditor.gradle.PoEditorStringsImporter
import com.hyperdevs.poeditor.gradle.utils.DEFAULT_PLUGIN_NAME
import com.hyperdevs.poeditor.gradle.utils.POEDITOR_CONFIG_NAME
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * Task that:
 * 1. Downloads all strings files (every available lang) from PoEditor given a api_token and project_id.
 * 2. Extracts "tablet" strings to another XML (strings with the suffix "_tablet")
 * 3. Creates and saves two strings.xml files to values-<lang> and values-<lang>-sw600dp (tablet specific strings)
 */
abstract class ImportPoEditorStringsTask
@Inject constructor(private val extension: PoEditorPluginExtension) : DefaultTask() {

    /**
     * Main task entrypoint.
     */
    @TaskAction
    @Suppress("ThrowsCount")
    fun importPoEditorStrings() {
        val apiToken: String
        val projectId: Int
        val defaultLang: String
        val defaultResPath: String
        val tags: List<String>
        val languageOverridePathMap: Map<String, String>
        val minimumTranslationPercentage: Int?

        try {
            apiToken = extension.apiToken.get()
            projectId = extension.projectId.get()
            defaultLang = extension.defaultLang.get()
            defaultResPath = extension.defaultResPath.get()
            tags = extension.tags.get()
            languageOverridePathMap = extension.languageValuesOverridePathMap.get()
            minimumTranslationPercentage = extension.minimumTranslationPercentage.orNull
        } catch (e: Exception) {
            logger.error("Import configuration failed", e)

            throw IllegalArgumentException(
                "You don't have the config '${extension.name}' properly set-up in your '$POEDITOR_CONFIG_NAME' block " +
                "or you don't have your main '$DEFAULT_PLUGIN_NAME' config properly set-up.\n" +
                "Please review the input parameters of both blocks and try again.")
        }

        PoEditorStringsImporter.importPoEditorStrings(
            apiToken,
            projectId,
            defaultLang,
            defaultResPath,
            tags,
            languageOverridePathMap,
            minimumTranslationPercentage)
    }
}