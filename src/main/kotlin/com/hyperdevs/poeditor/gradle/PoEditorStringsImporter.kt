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

import com.hyperdevs.poeditor.gradle.adapters.PoEditorDateJsonAdapter
import com.hyperdevs.poeditor.gradle.extensions.downloadUrlToString
import com.hyperdevs.poeditor.gradle.network.PoEditorApiControllerImpl
import com.hyperdevs.poeditor.gradle.network.api.ExportType
import com.hyperdevs.poeditor.gradle.network.api.FilterType
import com.hyperdevs.poeditor.gradle.network.api.OrderType
import com.hyperdevs.poeditor.gradle.network.api.PoEditorApi
import com.hyperdevs.poeditor.gradle.network.api.ProjectLanguage
import com.hyperdevs.poeditor.gradle.utils.TABLET_REGEX_STRING
import com.hyperdevs.poeditor.gradle.utils.logger
import com.hyperdevs.poeditor.gradle.xml.StringsXmlPostProcessor
import com.hyperdevs.poeditor.gradle.xml.StringsXmlWriter
import com.hyperdevs.poeditor.gradle.xml.parser.SaxStringsXmlDocumentParser
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Main class that does the XML download, parsing and saving from PoEditor files.
 */
object PoEditorStringsImporter {
    private const val POEDITOR_API_URL = "https://api.poeditor.com/v2/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(Date::class.java, PoEditorDateJsonAdapter())
        .build()

    private const val CONNECT_TIMEOUT_SECONDS = 30L
    private const val READ_TIMEOUT_SECONDS = 30L
    private const val WRITE_TIMEOUT_SECONDS = 30L

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    logger.debug(message)
                }
            })
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        )
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(POEDITOR_API_URL.toHttpUrl())
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val poEditorApi: PoEditorApi = retrofit.create(PoEditorApi::class.java)

    /**
     * Imports PoEditor strings.
     */
    @Suppress("LongParameterList", "LongMethod")
    fun importPoEditorStrings(
        apiToken: String,
        projectId: Int,
        defaultLang: String,
        resDirPath: String,
        filters: List<FilterType>,
        order: OrderType,
        tags: List<String>,
        languageValuesOverridePathMap: Map<String, String>,
        minimumTranslationPercentage: Int,
        resFileName: String,
        unquoted: Boolean,
        unescapeHtmlTags: Boolean,
        untranslatableStringsRegex: String?
    ) {
        try {
            val poEditorApiController = PoEditorApiControllerImpl(apiToken, moshi, poEditorApi)

            // Retrieve available languages from PoEditor
            logger.lifecycle("Retrieving project languages...")
            val projectLanguages = poEditorApiController.getProjectLanguages(projectId)
            val skippedLanguages = projectLanguages.filter { it.percentage < minimumTranslationPercentage }.toSet()

            // Iterate over every available language
            logger.lifecycle("Available languages: ${projectLanguages.joinAndFormat { it.code }}")

            if (minimumTranslationPercentage >= 0) {
                val skippedLanguagesMessage = if (skippedLanguages.isEmpty()) {
                    "All languages are translated above the minimum of $minimumTranslationPercentage%"
                } else {
                    val formattedLanguages = skippedLanguages.joinAndFormat { "${it.code} (${it.percentage}%)" }
                    "Skipping languages translated under $minimumTranslationPercentage%: $formattedLanguages"
                }

                logger.lifecycle(skippedLanguagesMessage)
            }

            if (filters.isNotEmpty()) {
                logger.lifecycle("Using the following filters for all languages: $filters")
            }

            val stringsXmlDocumentParser = SaxStringsXmlDocumentParser()

            projectLanguages.minus(skippedLanguages).forEach { languageData ->
                val languageCode = languageData.code

                // Retrieve translation file URL for the given language and for the "android_strings" type,
                // acknowledging passed tags if present
                logger.lifecycle("Retrieving translation file URL for language code: $languageCode")
                val translationFileUrl = poEditorApiController.getTranslationFileUrl(
                    projectId = projectId,
                    code = languageCode,
                    type = ExportType.ANDROID_STRINGS,
                    filters = filters,
                    order = order,
                    tags = tags,
                    unquoted = unquoted
                )

                // Download translation File to in-memory string
                logger.lifecycle("Downloading file from URL: $translationFileUrl")
                val translationFile = okHttpClient.downloadUrlToString(translationFileUrl)

                // Extract final files from downloaded translation XML
                val originalStringsXmlDocument = stringsXmlDocumentParser.deserialize(translationFile)

                val postProcessedStringsXmlDocumentMap = StringsXmlPostProcessor.processTranslationXml(
                    originalStringsXmlDocument,
                    listOf(TABLET_REGEX_STRING),
                    unescapeHtmlTags,
                    untranslatableStringsRegex?.toRegex()
                )

                StringsXmlWriter.saveXml(
                    resDirPath,
                    resFileName,
                    postProcessedStringsXmlDocumentMap.mapValues { (_, document) ->
                        stringsXmlDocumentParser.serialize(document)
                    },
                    defaultLang,
                    languageCode,
                    languageValuesOverridePathMap
                )
            }
        } catch (e: Exception) {
            logger.error(
                "An error happened when retrieving strings from project. " +
                "Please review the plug-in's input parameters and try again"
            )
            throw e
        }
    }

    private fun Collection<ProjectLanguage>.joinAndFormat(transform: ((ProjectLanguage) -> CharSequence)) =
        joinToString(separator = ", ", prefix = "[", postfix = "]", transform = transform)
}
