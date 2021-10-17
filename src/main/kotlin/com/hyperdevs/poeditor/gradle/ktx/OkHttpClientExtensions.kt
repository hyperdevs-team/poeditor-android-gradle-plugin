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

package com.hyperdevs.poeditor.gradle.ktx

import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Downloads a file from network and outputs it as a String.
 *
 * Returns null if the request fails.
 */
fun OkHttpClient.downloadUrlToString(fileUrl: String): String {
    val translationFileRequest = Request.Builder()
            .url(fileUrl)
            .build()
    return newCall(translationFileRequest).execute().body!!.string()
}
