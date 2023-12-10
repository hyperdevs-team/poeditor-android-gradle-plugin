@file:Suppress("NoUnusedImports", "UnusedImports") // Needed because detekt removes the "assign" import
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

import com.hyperdevs.poeditor.gradle.DefaultValues
import com.hyperdevs.poeditor.gradle.PoEditorPluginExtension
import com.hyperdevs.poeditor.gradle.PoEditorStringsImporter
import com.hyperdevs.poeditor.gradle.network.api.FilterType
import com.hyperdevs.poeditor.gradle.network.api.OrderType
import com.hyperdevs.poeditor.gradle.utils.DEFAULT_PLUGIN_NAME
import com.hyperdevs.poeditor.gradle.utils.POEDITOR_CONFIG_NAME
import com.hyperdevs.poeditor.gradle.utils.getResourceDirectory
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import javax.inject.Inject

/**
 * Task that:
 * 1. Downloads all strings files (every available lang) from PoEditor given an api_token and project_id.
 * 2. Extracts "tablet" strings to another XML (strings with the suffix "_tablet")
 * 3. Creates and saves two strings.xml files to values-<lang> and values-<lang>-sw600dp (tablet specific strings)
 */
abstract class ImportPoEditorStringsTask @Inject constructor() : DefaultTask() {
    /**
     * PoEditor API token.
     *
     * Must be present in order to run the plugin.
     */
    @get:Input
    abstract val apiToken: Property<String>

    /**
     * PoEditor project ID.
     *
     * Must be present in order to run the plugin.
     */
    @get:Input
    abstract val projectId: Property<Int>

    /**
     * Default language of the project, in ISO-2 format.
     *
     * Defaults to 'en' if not defined.
     */
    @get:Optional
    @get:Input
    abstract val defaultLang: Property<String>

    /**
     * Default resources path for the module where the strings should be put in.
     *
     * Defaults to the module with the `com.android.application` plugin.
     */
    @get:Optional
    @get:Input
    abstract val defaultResPath: Property<String>

    /**
     * File name of the string resource files.
     *
     * Defaults to "strings" if not defined.
     */
    @get:Optional
    @get:Input
    abstract val resFileName: Property<String>

    /**
     * Filters to limit downloaded strings with, from the officially supported list in PoEditor.
     *
     * Defaults to an empty list of filters if not present.
     */
    @get:Optional
    @get:Input
    abstract val filters: ListProperty<String>

    /**
     * Defines the order for the export. If set to "terms" it will be ordered alphabetically by the terms.
     *
     * Defaults to "none" meaning no order will be applied.
     */
    @get:Optional
    @get:Input
    abstract val order: Property<String>

    /**
     * Tags to filter downloaded strings with, previously declared in PoEditor.
     *
     * Defaults to an empty list of tags if not present.
     */
    @get:Optional
    @get:Input
    abstract val tags: ListProperty<String>

    /**
     * Map of languages to override their default values folder.
     *
     * Defaults to an empty map of language overrides map.
     */
    @get:Optional
    @get:Input
    abstract val languageValuesOverridePathMap: MapProperty<String, String>

    /**
     * The minimum accepted percentage of translated strings per language. Languages with fewer translated strings will not be fetched.
     *
     * Defaults to no minimum if not present, meaning that all languages will be fetched.
     */
    @get:Optional
    @get:Input
    abstract val minimumTranslationPercentage: Property<Int>

    /**
     * Defines if the output strings exported from PoEditor should be unquoted.
     *
     * Defaults to "false" meaning that all texts will be quoted.
     */
    @get:Optional
    @get:Input
    abstract val unquoted: Property<Boolean>

    /**
     * Whether HTML tags in strings should be unescaped or not.
     *
     * Defaults to true.
     */
    @get:Optional
    @get:Input
    abstract val unescapeHtmlTags: Property<Boolean>

    /**
     * Main task entrypoint.
     */
    @TaskAction
    @Suppress("ThrowsCount")
    fun importPoEditorStrings() {
        // Ensure that mandatory parameters are defined
        val apiToken: String
        val projectId: Int

        try {
            apiToken = this.apiToken.get()
            projectId = this.projectId.get()
        } catch (e: Exception) {
            logger.error("Import configuration failed", e)

            throw IllegalArgumentException(
                "You don't have the config properly set-up in your '$POEDITOR_CONFIG_NAME' block " +
                "or you don't have your main '$DEFAULT_PLUGIN_NAME' config properly set-up.\n" +
                "Please review the input parameters of both blocks and try again.")
        }

        PoEditorStringsImporter.importPoEditorStrings(
            apiToken,
            projectId,
            defaultLang.getOrElse(DefaultValues.DEFAULT_LANG),
            defaultResPath.getOrElse(getResourceDirectory(project, DefaultValues.MAIN_CONFIG_NAME).absolutePath),
            filters.getOrElse(DefaultValues.FILTERS).map { FilterType.from(it) },
            OrderType.from(order.getOrElse(DefaultValues.ORDER_TYPE.lowercase())),
            tags.getOrElse(DefaultValues.TAGS),
            languageValuesOverridePathMap.getOrElse(DefaultValues.LANGUAGE_VALUES_OVERRIDE_PATH_MAP),
            minimumTranslationPercentage.getOrElse(DefaultValues.MINIMUM_TRANSLATION_PERCENTAGE),
            resFileName.getOrElse(DefaultValues.RES_FILE_NAME),
            unquoted.getOrElse(DefaultValues.UNQUOTED),
            unescapeHtmlTags.getOrElse(DefaultValues.UNESCAPE_HTML_TAGS)
        )
    }

    internal fun configureTask(extension: PoEditorPluginExtension) {
        this.apiToken = extension.apiToken
        this.projectId = extension.projectId
        this.defaultLang = extension.defaultLang
        this.defaultResPath = extension.defaultResPath
        this.resFileName = extension.resFileName
        this.filters = extension.filters
        this.order = extension.order
        this.tags = extension.tags
        this.languageValuesOverridePathMap = extension.languageValuesOverridePathMap
        this.minimumTranslationPercentage = extension.minimumTranslationPercentage
        this.unquoted = extension.unquoted
        this.unescapeHtmlTags = extension.unescapeHtmlTags
    }
}
