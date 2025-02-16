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

package com.hyperdevs.poeditor.gradle.xml

import com.hyperdevs.poeditor.gradle.extensions.getCDataContent
import com.hyperdevs.poeditor.gradle.extensions.isCData
import com.hyperdevs.poeditor.gradle.extensions.replaceCDataContent
import com.hyperdevs.poeditor.gradle.extensions.unescapeHtmlTags
import com.hyperdevs.poeditor.gradle.utils.ALL_REGEX_STRING
import com.hyperdevs.poeditor.gradle.xml.models.StringsXmlDocument
import com.hyperdevs.poeditor.gradle.xml.models.StringsXmlResource

/**
 * Class that handles XML transformation.
 */
object StringsXmlPostProcessor {
    @Suppress("RegExpRedundantEscape")
    private val VARIABLE_REGEX = Regex("""\{(\d*)\{(.*?)\}\}""")

    /**
     * Does a full processing of an Android strings.xml file.
     * The process is the following:
     *
     * - Format variables and texts to conform to Android strings.xml format
     * - Split to multiple XML files depending on regex matching
     */
    fun processTranslationXml(
        translationStringsXmlDocument: StringsXmlDocument,
        fileSplitRegexStringList: List<String>,
        unescapeHtmlTags: Boolean,
        untranslatableStringsRegex: Regex?,
        includeComments: Boolean
    ): Map<String, StringsXmlDocument> {
        val formattedStringsXmlDocument =
            formatTranslationXml(
                translationStringsXmlDocument,
                unescapeHtmlTags,
                untranslatableStringsRegex,
                includeComments
            )

        val splitStringsXmlDocuments = splitTranslationXml(
            formattedStringsXmlDocument,
            fileSplitRegexStringList
        )

        return splitStringsXmlDocuments
    }

    /**
     * Formats a given translations XML string to conform to Android strings.xml format.
     */
    fun formatTranslationXml(
        translationStringsXmlDocument: StringsXmlDocument,
        unescapeHtmlTags: Boolean,
        untranslatableStringsRegex: Regex?,
        includeComments: Boolean
    ): StringsXmlDocument {
        return translationStringsXmlDocument.map { resource ->
            formatResource(resource, unescapeHtmlTags, untranslatableStringsRegex).let {
                // Remove comments if the user specifies that they should not be included
                if (!includeComments) {
                    it.removeComment()
                } else {
                    it
                }
            }
        }
    }

    /**
     * Splits an Android XML file to multiple XML files depending on regex matching.
     */
    fun splitTranslationXml(
        translationStringsXmlDocument: StringsXmlDocument,
        fileSplitRegexStringList: List<String>
    ): Map<String, StringsXmlDocument> {
        return fileSplitRegexStringList
            .map { regex ->
                // Extract strings matched by patterns to a separate strings XML document object
                regex to translationStringsXmlDocument.filter(regex)
            }
            .filter { (_, document) -> document.resources.isNotEmpty() } // Only process regexes with at least one coincidence
            .toMap()
            .mapValues { (regexString, filteredStringsXmlDocument) ->
                val regex = Regex(regexString)

                filteredStringsXmlDocument.mapNotNull { resource ->
                    val newName = regex.find(resource.name)?.groups?.get(1)?.value

                    newName?.let {
                        resource.updateName(it)
                    }
                }
            }
            // Add filtered list with strings that don't match the regexes
            .plus(ALL_REGEX_STRING to fileSplitRegexStringList.fold(translationStringsXmlDocument) { document, regex ->
                document.filterNot(regex)
            })
    }

    /**
     * Formats a given resource to conform to Android strings.xml format.
     */
    fun formatResource(
        resource: StringsXmlResource,
        unescapeHtmlTags: Boolean,
        untranslatableStringsRegex: Regex?
    ): StringsXmlResource {
        var textProcessedResource = resource.updateValues { value -> processResourceValue(value, unescapeHtmlTags) }

        // Add the translatable = false node if the string name matches the untranslatable pattern
        untranslatableStringsRegex?.let {
            if (textProcessedResource.name.matches(untranslatableStringsRegex)) {
                textProcessedResource = textProcessedResource.updateTranslatable(false)
            }
        }

        return textProcessedResource
    }

    private fun processResourceValue(resourceValue: String, unescapeHtmlTags: Boolean): String {
        // First check if we have a CDATA node as the child of the resource. If we have it, we have to
        // preserve the CDATA node but process the text. Else, we handle the node as a usual text node.
        return if (resourceValue.isCData()) {
            resourceValue.replaceCDataContent(formatStringVariables(resourceValue.getCDataContent()!!))
        } else {
            formatStringVariables(resourceValue)
        }.let {
            // Check if we have to unescape HTML tags, and do it if needed
            if (unescapeHtmlTags) it.unescapeHtmlTags() else it
        }
    }

    /**
     * Formats the variables in a given translation string to conform to Android strings.xml format.
     */
    private fun formatStringVariables(translationString: String): String {
        // We need to check for variables to see if we have to escape percent symbols: if we find variables, we have
        // to escape them
        val containsVariables = translationString.contains(VARIABLE_REGEX)

        val placeholderTransform: (MatchResult) -> CharSequence = { matchResult ->
            // TODO if the string has multiple variables but any of them has no order number,
            //  throw an exception

            // If the placeholder contains an ordinal, use it: {2{pages_count}} -> %2$s
            val placeholderVariableOrder = matchResult.groupValues[1]
            if (placeholderVariableOrder.toIntOrNull() != null) {
                "%$placeholderVariableOrder\$s"
            } else { // If not, use "1" as the ordinal: {{pages_count}} -> %1$s
                "%1\$s"
            }
        }

        return translationString
            // Replace % with %% if variables are found
            .let { if (containsVariables) it.replace("%", "%%") else it }
            // Replace placeholders from {{variable}} to %1$s format.
            .replace(VARIABLE_REGEX, placeholderTransform)
    }
}
