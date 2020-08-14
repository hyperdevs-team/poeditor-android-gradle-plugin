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

import org.gradle.api.Named
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

/**
 * Extension class that represents the needed params that will
 * be passed to the different tasks of the plugin.
 */
open class PoEditorPluginExtension
@JvmOverloads constructor(objects: ObjectFactory, private val name: String = "default") : Named {
    @Internal
    override fun getName(): String = name

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
}