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

package com.bq.poeditor.gradle.network.api

import java.util.Date

/**
 * Basic PoEditor response. Contains common fields for all responses in the API.
 */
open class PoEditorResponse(open val response: ResponseStatus)

/**
 * PoEditor response to "list languages" call.
 */
data class ListProjectLanguagesResponse(override val response: ResponseStatus,
                                        val list: List<ProjectLanguage>) : PoEditorResponse(response)

/**
 * PoEditor response to "export languages" call.
 */
data class ExportResponse(override val response: ResponseStatus,
                          val item: String) : PoEditorResponse(response)

/**
 * Basic response data.
 */
data class ResponseStatus(val status: String,
                          val code: String,
                          val message: String)

/**
 * Information about a language in PoEditor.
 */
data class ProjectLanguage(val name: String,
                           val code: String,
                           val percentage: Double,
                           val updated: Date)