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

package com.hyperdevs.poeditor.gradle.utils

import com.hyperdevs.poeditor.gradle.PoEditorPluginExtension
import com.hyperdevs.poeditor.gradle.TAG
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.newInstance
import java.util.*
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

/**
 * Creates an extension for the given config name (flavor or build type name) by merging data from the base
 * configuration extension and the provided configuration extension.
 */
internal fun buildExtensionForConfig(project: Project,
                                     configExtension: PoEditorPluginExtension,
                                     baseExtension: PoEditorPluginExtension): PoEditorPluginExtension {
    val extensions = listOfNotNull(configExtension, baseExtension)
        .distinctBy { it.name }
        .mapToExtensionMergeHolder(project)

    logger.debug("$TAG: Extensions found: $extensions")

    return mergeExtensions(extensions)
}

internal fun mergeExtensions(extensions: List<ExtensionMergeHolder>): PoEditorPluginExtension {
    require(extensions.isNotEmpty()) { "At least one extension must be provided." }

    if (extensions.size == 1) return extensions.single().original

    val extensionsWithInitializationRoot = extensions + extensions.last()

    for (i in 1 until extensionsWithInitializationRoot.size) {
        val parentCopy = extensionsWithInitializationRoot[i].uninitializedCopy
        val (child, childCopy) = extensionsWithInitializationRoot[i - 1]

        PoEditorPluginExtension::class.declaredMemberProperties.linkProperties(parentCopy, child, childCopy)
    }

    return extensions.first().uninitializedCopy
}

private fun <T> Collection<KProperty1<T, *>>.linkProperties(parent: T, child: T, childCopy: T) {
    for (property in this) {
        if (property.name == "name") continue

        val value = property.get(childCopy)
        @Suppress("UNCHECKED_CAST")
        if (value is Property<*>) {
            val originalProperty = property.get(child) as Property<Nothing>
            val parentFallback = property.get(parent) as Property<Nothing>
            if (value !== parentFallback) {
                value.set(originalProperty.orElse(parentFallback))
            } else {
                value.set(originalProperty)
            }
        } else if (value is ListProperty<*>) {
            val originalProperty = property.get(child) as ListProperty<Nothing>
            val parentFallback = property.get(parent) as ListProperty<Nothing>
            if (value !== parentFallback) {
                value.set(originalProperty.map {
                    it.takeUnless { it.isEmpty() }.sneakyNull()
                }.orElse(parentFallback))
            } else {
                value.set(originalProperty)
            }
        }
    }
}

internal data class ExtensionMergeHolder(val original: PoEditorPluginExtension,
                                         val uninitializedCopy: PoEditorPluginExtension)

internal fun List<PoEditorPluginExtension>.mapToExtensionMergeHolder(project: Project): List<ExtensionMergeHolder> {
    return this.map {
        ExtensionMergeHolder(
            original = it,
            // This is an injected instance of PoEditorPluginExtension, so you need to provide proper arguments that
            // match its constructor
            uninitializedCopy = project.objects.newInstance(project.objects, UUID.randomUUID().toString()))
    }
}

// Extracted from gradle-play-publisher: https://github.com/Triple-T/gradle-play-publisher/blob/bffc26cb41efc79babdb3ac7dbefcb1d9816f928/play/plugin/src/main/kotlin/com/github/triplet/gradle/play/internal/Extensions.kt#L125
// TODO: remove after https://github.com/gradle/gradle/issues/12388
@Suppress("UNCHECKED_CAST")
private fun <T> T?.sneakyNull() = this as T