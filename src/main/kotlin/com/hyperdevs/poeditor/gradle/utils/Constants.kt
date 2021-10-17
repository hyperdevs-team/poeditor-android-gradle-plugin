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

import org.gradle.api.logging.Logging

const val ALL_REGEX_STRING = """^(.+)$"""

const val TABLET_REGEX_STRING = """^(.+)_tablet$"""
const val TABLET_RES_FOLDER_SUFFIX = "sw600dp"

const val DEFAULT_PLUGIN_NAME = "poEditor"
const val POEDITOR_CONFIG_NAME = "poEditorConfig"

const val PLUGIN_GROUP = "Translations"

val logger = Logging.getLogger(org.gradle.api.logging.Logger::class.java)!!
