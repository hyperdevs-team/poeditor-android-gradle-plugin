/*
 * Copyright 2025 HyperDevs
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

package com.hyperdevs.poeditor.gradle.xml.parser

import com.hyperdevs.poeditor.gradle.xml.models.StringsXmlDocument

/**
 * Interface that defines a parser for strings.xml documents.
 */
interface StringsXmlDocumentParser {
    /**
     * Deserializes a strings.xml document from a given XML string.
     *
     * @param xmlString The XML string to deserialize.
     * @return The deserialized strings.xml document.
     */
    fun deserialize(xmlString: String): StringsXmlDocument

    /**
     * Serializes a strings.xml document to an XML string.
     *
     * @param xmlDocument The strings.xml document to serialize.
     * @return The serialized XML string.
     */
    fun serialize(xmlDocument: StringsXmlDocument): String
}
