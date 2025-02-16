/*
 * Copyright 2023 HyperDevs
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

import com.hyperdevs.poeditor.gradle.network.api.OrderType
import java.io.File

/**
 * Object that holds default and convention values for the plugin properties.
 */
@Suppress("MayBeConst", "MayBeConstant")
object DefaultValues {
    internal val ENABLED = true
    internal val MAIN_CONFIG_NAME: ConfigName = "main"
    internal val DEFAULT_LANG = "en"
    internal val FILTERS = emptyList<String>()
    internal val ORDER_TYPE = OrderType.NONE.name.lowercase()
    internal val TAGS = emptyList<String>()
    internal val LANGUAGE_VALUES_OVERRIDE_PATH_MAP = emptyMap<String, String>()
    internal val MINIMUM_TRANSLATION_PERCENTAGE = -1
    internal val RES_FILE_NAME = "strings"
    internal val UNQUOTED = false
    internal val UNESCAPE_HTML_TAGS = true
    internal val INCLUDE_COMMENTS = true

    /**
     * Apply the default convention to a [PoEditorPluginExtension].
     */
    fun applyDefaultConvention(extension: PoEditorPluginExtension, resourceDirectory: File) {
        with(extension) {
            enabled.convention(ENABLED)
            defaultResPath.convention(resourceDirectory.absolutePath)
            defaultLang.convention(DEFAULT_LANG)
            filters.convention(FILTERS)
            order.convention(ORDER_TYPE)
            tags.convention(TAGS)
            languageValuesOverridePathMap.convention(LANGUAGE_VALUES_OVERRIDE_PATH_MAP)
            minimumTranslationPercentage.convention(MINIMUM_TRANSLATION_PERCENTAGE)
            resFileName.convention(RES_FILE_NAME)
            unquoted.convention(UNQUOTED)
            unescapeHtmlTags.convention(UNESCAPE_HTML_TAGS)
            includeComments.convention(INCLUDE_COMMENTS)
        }
    }
}
