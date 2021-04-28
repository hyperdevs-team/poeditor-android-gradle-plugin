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

import io.github.cdimascio.dotenv.Dotenv

/**
 * Only for testing purposes.
 *
 * Declare the variables API_TOKEN, PROJECT_ID, RES_DIR_PATH and DEFAULT_LANGUAGE in /.env
 */
@Suppress("MagicNumber")
fun main() {
    val dotenv: Dotenv = Dotenv.load()

    val apiToken = dotenv.get("API_TOKEN", "")
    val projectId = dotenv.get("PROJECT_ID", "-1").toInt()
    val resDirPath = dotenv.get("RES_DIR_PATH", "")
    val defaultLanguage = dotenv.get("DEFAULT_LANGUAGE", "")

    PoEditorStringsImporter.importPoEditorStrings(apiToken, projectId, defaultLanguage, resDirPath)
}