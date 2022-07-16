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

/**
 * Creates values file modifier taking into account specializations (i.e values-es-rMX for Mexican).
 * @param langCode
 * @return proper values file modifier (i.e. es-rMX)
 */
fun createValuesModifierFromLangCode(langCode: String): String {
    return if (!langCode.contains("-")) {
        langCode
    } else {
        val langParts = langCode.split("-")
        val language = langParts[0]
        val region = langParts[1].toLowerCase()

        return when (language) {
            "zh" -> {
                // Chinese support
                handleChineseVariants(language, region)
            }
            else -> {
                "$language-r${region.toUpperCase()}"
            }
        }
    }
}

/**
 * Handle Chinese variants supported by PoEditor.
 *
 * PoEditor handles the following Chinese variants:
 * - Chinese
 * - Chinese (HK)
 * - Chinese (MO)
 * - Chinese (SG)
 * - Chinese (simplified)
 * - Chinese (traditional)
 *
 * They will be handled the following way:
 * - Chinese will be set as values-zh.
 * - Chinese (simplified) will be set as values-b+zh+Hans.
 * - Chinese (traditional) will be set as values-b+zh+Hant.
 * - Regional Chinese variants will be set as values-zh-r<REGION>
 */
private fun handleChineseVariants(language: String, region: String) = when (region) {
    "cn" -> {
        language
    }
    "hans", "hant" -> {
        "b+$language+${region.toLowerCase().capitalize()}"
    }
    else -> {
        "$language-r${region.toUpperCase()}"
    }
}
