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

package com.hyperdevs.poeditor.gradle.network.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * API declaration of PoEditor endpoints used in the app.
 */
interface PoEditorApi {

    /**
     * Returns a list of languages that the current PoEditor project contains.
     */
    @FormUrlEncoded
    @POST("languages/list")
    fun getProjectLanguages(@Field("api_token") apiToken: String,
                            @Field("id") id: Int): Call<ListLanguagesResponse>

    /**
     * Returns the exportables ready to retrieve from the current PoEditor project.
     */
    @Suppress("LongParameterList")
    @FormUrlEncoded
    @JvmSuppressWildcards
    @POST("projects/export")
    fun getExportFileInfo(@Field("api_token") apiToken: String,
                          @Field("id") id: Int,
                          @Field("language") language: String,
                          @Field("type") type: String,
                          @Field("filters") filters: List<String>? = null,
                          @Field("order") order: String? = null,
                          @Field("tags") tags: List<String>? = null,
                          @Field("options") options: String? = null): Call<ExportResponse>
}
