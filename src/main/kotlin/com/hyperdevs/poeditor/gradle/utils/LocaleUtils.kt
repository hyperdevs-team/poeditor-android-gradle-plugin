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
    val langParts = langCode.split("-")
    val language = langParts[0]
    val region = langParts.getOrNull(1)?.toLowerCase()

    return when (language) {
        "zh" -> {
            // Chinese support
            handleChineseVariants(language, region).apply {
                logger.lifecycle("INFO: Converted $langCode to $this")
            }
        }

        "he", "yi", "id" -> {
            handleOldLocaleVariants(language, region).apply {
                logger.lifecycle("INFO: Converted $langCode to $this")
            }
        }

        else -> {
            "$language${region?.let { "-r${it.toUpperCase()}" } ?: ""}"
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
private fun handleChineseVariants(language: String, region: String?) = when (region) {
    "cn" -> {
        language
    }

    "hans", "hant" -> {
        "b+$language${region.let { "+${it.toLowerCase().capitalize()}" }}"
    }

    else -> {
        "$language${region?.let { "-r${it.toUpperCase()}" } ?: ""}"
    }
}

/**
 * Handle locales that need translation to ISO-639 old locales, so Android devices can pick them up properly.
 *
 * According to [java.util.Locale] constructor docs in Android documentation:
 * ISO 639 is not a stable standard; some of the language codes it defines (specifically "iw", "ji", and "in")
 * have changed. This constructor accepts both the old codes ("iw", "ji", and "in") and the new codes
 * ("he", "yi", and "id"), but all other API on Locale will return only the OLD codes.
 *
 * We keep this in mind to convert new locale codes received from PoEditor to the old codes handled by Android.
 *
 * NOTE: this will most likely change if Android ever supports Java 17, where new codes are used for locales. There may
 * be a way to user the "-Djava.locale.useOldISOCodes=true" system property, though.
 *
 * References:
 * - Locale documentation: https://developer.android.com/reference/java/util/Locale.html#Locale(java.lang.String)
 * - List of supported languages up to Android 5.1: https://stackoverflow.com/a/7989085/9288365
 * - List of supported languages from Android 7 to Android 9: https://stackoverflow.com/a/52329560/9288365
 * - Android Issue Tracker issue regarding Indonesian locale: https://issuetracker.google.com/issues/36911507
 * - Android Issue Tracker issue regarding Hebrew locale: https://issuetracker.google.com/issues/36908826
 * - Java 17 locale changes: https://bugs.openjdk.org/browse/JDK-8267069
 */
private fun handleOldLocaleVariants(language: String, region: String?): String {
    val oldLanguageCode = when (language) {
        "he" -> "iw"
        "yi" -> "ji"
        "id" -> "in"
        else -> language
    }

    return "$oldLanguageCode${region?.let { "-r${it.toUpperCase()}" } ?: ""}"
}
