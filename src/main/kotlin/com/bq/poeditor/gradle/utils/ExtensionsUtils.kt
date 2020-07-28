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

package com.bq.poeditor.gradle.utils

import com.bq.poeditor.gradle.PoEditorPluginExtension
import com.bq.poeditor.gradle.TAG
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

/**
 * Creates an extension for the given config name (flavor or build type name) by merging data from the base
 * configuration extension extension and the provided configuration extension, if any.
 *
 * Returns null when no extension could be found that matches [configName].
 */
internal fun buildExtensionForConfig(configName: String,
                                     extensionContainer: NamedDomainObjectContainer<PoEditorPluginExtension>,
                                     baseExtension: PoEditorPluginExtension): PoEditorPluginExtension? {
    val configExtension = extensionContainer.findByName(configName)

    if (configExtension == null) {
        logger.debug("$TAG: No config found for config '$configName'")
        return null
    }

    val extensions = listOfNotNull(
        configExtension,
        baseExtension
    ).distinctBy { it.name }

    logger.debug("$TAG: Extensions found for name '$configName': $extensions")

    return mergeExtensions(extensions)
}

internal fun mergeExtensions(extensions: List<PoEditorPluginExtension>): PoEditorPluginExtension {
    requireNotNull(extensions.isNotEmpty()) { "At least one extension must be provided." }
    if (extensions.size == 1) return extensions.single()

    for (i in 1 until extensions.size) {
        val parent = extensions[i]
        val child = extensions[i - 1]

        PoEditorPluginExtension::class.declaredMemberProperties.linkProperties(parent, child)
    }

    return extensions.first()
}

private fun <T> Collection<KProperty1<T, *>>.linkProperties(parent: T, child: T) {
    for (property in this) {
        if (property.name == "name") continue

        val value = property.get(child)
        @Suppress("UNCHECKED_CAST")
        if (value is Property<*>) {
            value.convention(property.get(parent) as Property<Nothing>)
        } else if (value is ListProperty<*>) {
            value.convention(property.get(parent) as ListProperty<Nothing>)
        }
    }
}