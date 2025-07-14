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

package com.hyperdevs.poeditor.gradle.extensions

private val cdataRegex = Regex("^<!\\[CDATA\\[(.*)]]>$", RegexOption.DOT_MATCHES_ALL)

/**
 * Unescapes HTML tags from string.
 */
fun String.unescapeHtmlTags() = this
    .replace("&lt;", "<")
    .replace("&gt;", ">")

/**
 * Returns if the given string is a CDATA string.
 */
fun String.isCData() = cdataRegex.matches(this.trim())

/**
 * Returns the CDATA content from a given CDATA string.
 *
 * Returns null if the string is not a CDATA string.
 */
fun String.getCDataContent() = cdataRegex.matchEntire(this.trim())?.groups?.get(1)?.value

/**
 * Replaces the CDATA contents of a given string.
 *
 * Returns the same string if it's not a CDATA string.
 */
fun String.replaceCDataContent(newContent: String): String {
    return if (this.isCData()) {
        "<![CDATA[$newContent]]>"
    } else {
        this
    }
}
