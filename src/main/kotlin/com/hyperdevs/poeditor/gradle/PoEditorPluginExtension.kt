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

package com.hyperdevs.poeditor.gradle

import org.gradle.api.Named
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import javax.inject.Inject

/**
 * Extension class that represents the needed params that will
 * be passed to the different tasks of the plugin.
 */
open class PoEditorPluginExtension @Inject constructor(objects: ObjectFactory, private val name: String) : Named {

    @Internal
    override fun getName(): String = name

    /**
     * Whether the configuration is enabled or not.
     */
    @get:Optional
    @get:Input
    val enabled: Property<Boolean> = objects.property(Boolean::class.java)

    /**
     * PoEditor API token.
     *
     * Must be present in order to run the plugin.
     */
    @get:Input
    val apiToken: Property<String> = objects.property(String::class.java)

    /**
     * PoEditor project ID.
     *
     * Must be present in order to run the plugin.
     */
    @get:Input
    val projectId: Property<Int> = objects.property(Int::class.java)

    /**
     * Default language of the project, in ISO-2 format.
     *
     * Defaults to 'en' if not defined.
     */
    @get:Optional
    @get:Input
    val defaultLang: Property<String> = objects.property(String::class.java)

    /**
     * Default resources path for the module where the strings should be put in.
     *
     * Defaults to the module with the `com.android.application` plugin.
     */
    @get:Optional
    @get:Input
    val defaultResPath: Property<String> = objects.property(String::class.java)

    /**
     * File name of the string resource files.
     *
     * Defaults to "strings" if not defined.
     */
    @get:Optional
    @get:Input
    val resFileName: Property<String> = objects.property(String::class.java)

    /**
     * Filters to limit downloaded strings with, from the officially supported list in PoEditor.
     *
     * Defaults to an empty list of filters if not present.
     */
    @get:Optional
    @get:Input
    val filters: ListProperty<String> = objects.listProperty(String::class.java)

    /**
     * Defines the order for the export. If set to "terms" it will be ordered alphabetically by the terms.
     *
     * Defaults to "none" meaning no order will be applied.
     */
    @get:Optional
    @get:Input
    val order: Property<String> = objects.property(String::class.java)

    /**
     * Tags to filter downloaded strings with, previously declared in PoEditor.
     *
     * Defaults to an empty list of tags if not present.
     */
    @get:Optional
    @get:Input
    val tags: ListProperty<String> = objects.listProperty(String::class.java)

    /**
     * Map of languages to override their default values folder.
     *
     * Defaults to an empty map of language overrides map.
     */
    @get:Optional
    @get:Input
    val languageValuesOverridePathMap: MapProperty<String, String> =
        objects.mapProperty(String::class.java, String::class.java)

    /**
     * The minimum accepted percentage of translated strings per language. Languages with fewer translated strings will not be fetched.
     *
     * Defaults to no minimum if not present, meaning that all languages will be fetched.
     */
    @get:Optional
    @get:Input
    val minimumTranslationPercentage: Property<Int> = objects.property(Int::class.java)

    /**
     * Defines if the output strings exported from PoEditor should be unquoted.
     *
     * Defaults to "false" meaning that all texts will be quoted.
     */
    @get:Optional
    @get:Input
    val unquoted: Property<Boolean> = objects.property(Boolean::class.java)

    /**
     * Whether HTML tags in strings should be unescaped or not.
     *
     * Defaults to true.
     */
    @get:Optional
    @get:Input
    val unescapeHtmlTags: Property<Boolean> = objects.property(Boolean::class.java)

    /**
     * Pattern to use to mark strings as translatable=false in the strings file.
     *
     * Defaults to null.
     */
    @get:Optional
    @get:Input
    val untranslatableStringsRegex: Property<String?> = objects.property(String::class.java)

    /**
     * Sets the configuration as enabled or not.
     *
     * NOTE: added for Gradle Groovy DSL compatibility. Check the note on
     * https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_properties for more details.
     *
     * Gradle Kotlin DSL users must use `enabled.set(value)`.
     */
    fun setEnabled(value: Boolean) = enabled.set(value)

    /**
     * Sets the PoEditor API token.
     *
     * NOTE: added for Gradle Groovy DSL compatibility. Check the note on
     * https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_properties for more details.
     *
     * Gradle Kotlin DSL users must use `apiToken.set(value)`.
     */
    fun setApiToken(value: String) = apiToken.set(value)

    /**
     * Sets the PoEditor API project ID.
     *
     * NOTE: added for Gradle Groovy DSL compatibility. Check the note on
     * https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_properties for more details.
     *
     * Gradle Kotlin DSL users must use `projectId.set(value)`.
     */
    fun setProjectId(value: Int) = projectId.set(value)

    /**
     * Sets the language of the project, in ISO-2 format.
     *
     * NOTE: added for Gradle Groovy DSL compatibility. Check the note on
     * https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_properties for more details.
     *
     * Gradle Kotlin DSL users must use `defaultLang.set(value)`.
     */
    fun setDefaultLang(value: String) = defaultLang.set(value)

    /**
     * Sets the resources directory path for the strings.xml files.
     *
     * NOTE: added for Gradle Groovy DSL compatibility. Check the note on
     * https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_properties for more details.
     *
     * Gradle Kotlin DSL users must use `defaultResPath.set(value)`.
     */
    fun setDefaultResPath(value: String) = defaultResPath.set(value)

    /**
     * Sets the file name for the resource files.
     *
     * NOTE: added for Gradle Groovy DSL compatibility. Check the note on
     * https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_properties for more details.
     *
     * Gradle Kotlin DSL users must use `resFileName.set(value)`.
     */
    fun setResFileName(value: String) = resFileName.set(value)

    /**
     * Sets the filters to limit downloaded strings with, from the officially supported list in PoEditor.
     *
     * NOTE: added for Gradle Groovy DSL compatibility. Check the note on
     * https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_properties for more details.
     *
     * Gradle Kotlin DSL users must use `filters.set(value)`.
     */
    fun setFilters(value: List<String>) = filters.set(value)

    /**
     * Sets the order in which to sort the strings from the officially supported list in PoEditor.
     *
     * NOTE: added for Gradle Groovy DSL compatibility. Check the note on
     * https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_properties for more details.
     *
     * Gradle Kotlin DSL users must use `order.set(value)`.
     */
    fun setOrder(value: String) = order.set(value)

    /**
     * Sets the tags to filter downloaded strings with, previously declared in PoEditor.
     *
     * NOTE: added for Gradle Groovy DSL compatibility. Check the note on
     * https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_properties for more details.
     *
     * Gradle Kotlin DSL users must use `tags.set(value)`.
     */
    fun setTags(value: List<String>) = tags.set(value)

    /**
     * Sets the map of languages to override their default values folder.
     *
     * NOTE: added for Gradle Groovy DSL compatibility. Check the note on
     * https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_properties for more details.
     *
     * Gradle Kotlin DSL users must use `languageValuesOverridePathMap.set(value)`.
     */
    fun setLanguageValuesOverridePathMap(value: Map<String, String>) = languageValuesOverridePathMap.set(value)

    /**
     * Sets the minimum accepted percentage of translated strings per language.
     *
     * NOTE: added for Gradle Groovy DSL compatibility. Check the note on
     * https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_properties for more details.
     *
     * Gradle Kotlin DSL users must use `minimumTranslationPercentage.set(value)`.
     */
    fun setMinimumTranslationPercentage(value: Int) = minimumTranslationPercentage.set(value)

    /**
     * Sets if the exported strings should be unquoted or not.
     *
     * NOTE: added for Gradle Groovy DSL compatibility. Check the note on
     * https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_properties for more details.
     *
     * Gradle Kotlin DSL users must use `unquoted.set(value)`.
     */
    fun setUnquoted(value: Boolean) = unquoted.set(value)

    /**
     * Sets if strings should have HTML tags unescaped.
     *
     * NOTE: added for Gradle Groovy DSL compatibility. Check the note on
     * https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_properties for more details.
     *
     * Gradle Kotlin DSL users must use `unescapeHtmlTags.set(value)`.
     */
    fun setUnescapeHtmlTags(value: Boolean) = unescapeHtmlTags.set(value)

    /**
     * Sets the pattern to use to mark strings as translatable=false in the strings file.
     *
     * NOTE: added for Gradle Groovy DSL compatibility. Check the note on
     * https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_properties for more details.
     *
     * Gradle Kotlin DSL users must use `unescapeHtmlTags.set(value)`.
     */
    fun setuntranslatableStringsRegex(value: String) = untranslatableStringsRegex.set(value)
}
