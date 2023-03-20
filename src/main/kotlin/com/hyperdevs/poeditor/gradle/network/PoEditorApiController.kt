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

package com.hyperdevs.poeditor.gradle.network

import com.hyperdevs.poeditor.gradle.network.api.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import retrofit2.Response

/**
 * Basic interface used to implement API calls to the PoEditor service.
 */
interface PoEditorApiController {
    /**
     * Retrieves the available languages for a given project.
     */
    fun getProjectLanguages(projectId: Int): List<ProjectLanguage>

    /**
     * Retrieves the translation file URL for a given project, language code, and export type.
     * Also supports a series of options.
     */
    @Suppress("LongParameterList")
    fun getTranslationFileUrl(projectId: Int,
                              code: String,
                              type: ExportType,
                              filters: List<FilterType>?,
                              order: OrderType,
                              tags: List<String>?,
                              unquoted: Boolean): String
}

/**
 * Implementation of [PoEditorApiController] using Retrofit.
 */
class PoEditorApiControllerImpl(private val apiToken: String,
                                private val moshi: Moshi,
                                private val poEditorApi: PoEditorApi) : PoEditorApiController {

    private val optionsAdapter: JsonAdapter<List<Options>> =
        moshi.adapter(Types.newParameterizedType(List::class.java, Options::class.java))

    override fun getProjectLanguages(projectId: Int): List<ProjectLanguage> {
        val response = poEditorApi.getProjectLanguages(
            apiToken = apiToken,
            id = projectId).execute()
        return response.onSuccessful { it.result?.languages ?: emptyList() }
    }

    @Suppress("LongParameterList")
    override fun getTranslationFileUrl(projectId: Int,
                                       code: String,
                                       type: ExportType,
                                       filters: List<FilterType>?,
                                       order: OrderType,
                                       tags: List<String>?,
                                       unquoted: Boolean): String {
        val options = listOf(
            Options(unquoted = if (unquoted) 1 else 0)
        ).let {
            optionsAdapter.toJson(it)
        }

        val response = poEditorApi.getExportFileInfo(
            apiToken = apiToken,
            id = projectId,
            type = type.toString().toLowerCase(),
            filters = filters?.map { it.name.toLowerCase() },
            language = code,
            order = order.name.toLowerCase(),
            tags = tags,
            options = options
        ).execute()

        return response.onSuccessful { it.result.url }
    }

    private inline fun <T : PoEditorResponse, U> Response<T>.onSuccessful(func: (T) -> U): U {
        if (isSuccessful && body()?.response?.code == "200") {
            body()?.let { return func(it) }
        }

        throw IllegalStateException(
            "An error occurred while trying to retrieve data from PoEditor API: \n\n" +
            body().toString())
    }
}
