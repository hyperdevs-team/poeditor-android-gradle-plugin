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
import retrofit2.http.*

/**
 * API declaration of PoEditor endpoints used in the app.
 */
interface PoEditorApi {

    /**
     * Returns a list of languages that the current PoEditor project contains.
     */
    @FormUrlEncoded
    @POST("/api/")
    fun getProjectLanguages(@Field("api_token") apiToken: String,
                            @Field("action") action: String = "list_languages",
                            @Field("id") id: Int): Call<ListProjectLanguagesResponse>

    /**
     * Returns the exportables ready to retrieve from the current PoEditor project.
     */
    @FormUrlEncoded
    @POST("/api/")
    fun getExportFileInfo(@Field("api_token") apiToken: String,
                          @Field("id") id: Int,
                          @Field("action") action: String = "export",
                          @Field("type") type: String,
                          @Field("language") language: String,
                          @Field("tags") tags: String?): Call<ExportResponse>
}