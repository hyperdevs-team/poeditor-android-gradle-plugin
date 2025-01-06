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
@file:Suppress("LongMethod")

package com.hyperdevs.poeditor.gradle.xml.parser

import com.hyperdevs.poeditor.gradle.xml.*
import com.hyperdevs.poeditor.gradle.xml.ITEM_ELEMENT_NAME
import com.hyperdevs.poeditor.gradle.xml.NAME_ATTRIBUTE_NAME
import com.hyperdevs.poeditor.gradle.xml.PLURALS_ELEMENT_NAME
import com.hyperdevs.poeditor.gradle.xml.QUANTITY_ATTRIBUTE_NAME
import com.hyperdevs.poeditor.gradle.xml.STRING_ARRAY_ELEMENT_NAME
import com.hyperdevs.poeditor.gradle.xml.STRING_ELEMENT_NAME
import com.hyperdevs.poeditor.gradle.xml.TRANSLATABLE_ATTRIBUTE_NAME
import com.hyperdevs.poeditor.gradle.xml.models.StringsXmlDocument
import com.hyperdevs.poeditor.gradle.xml.models.StringsXmlResource
import org.xml.sax.Attributes
import org.xml.sax.ext.LexicalHandler
import org.xml.sax.helpers.DefaultHandler
import java.io.File
import javax.xml.parsers.SAXParserFactory
import kotlin.random.Random

/**
 * SAX implementation of the [StringsXmlDocumentParser].
 */
class SaxStringsXmlDocumentParser : StringsXmlDocumentParser {
    override fun deserialize(xmlString: String): StringsXmlDocument {
        // Replace entities for later restoring
        val entityReplacements = calculateEntityReplacements(xmlString)
        val xmlStringWithReplacements = replaceEntities(xmlString, entityReplacements)

        val tempFile = File.createTempFile("temp", "").apply {
            deleteOnExit()
            writeText(xmlStringWithReplacements)
        }

        // Setup SAXParserFactory
        val factory = SAXParserFactory.newInstance()
        factory.isNamespaceAware = true

        val saxParser = factory.newSAXParser()
        val xmlReader = saxParser.xmlReader

        // Ensure that we use the proper data so LexicalHandler works
        val handler = SaxStringsXmlDocumentHandler()
        xmlReader.contentHandler = handler
        xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler)

        // Read the content
        xmlReader.parse(tempFile.toURI().toString())
        return restoreEntities(handler.parsedResources, entityReplacements)
    }

    @Suppress("NestedBlockDepth", "CyclomaticComplexMethod")
    override fun serialize(xmlDocument: StringsXmlDocument): String {
        val indent = "    "
        return StringBuilder().apply {
            appendLine("""<?xml version="1.0" encoding="utf-8"?>""")
            appendLine("""<resources>""")
            for (element in xmlDocument.resources) {
                element.comments.forEach { comment ->
                    append(indent.repeat(1))
                        .appendLine("<!-- $comment -->")
                }

                when (element) {
                    is StringsXmlResource.StringElement -> {
                        append(indent.repeat(1))
                            .append("""<$STRING_ELEMENT_NAME $NAME_ATTRIBUTE_NAME="${element.name}"""")
                            .apply {
                                if (!element.translatable) {
                                    append(""" $TRANSLATABLE_ATTRIBUTE_NAME="false"""")
                                }
                            }
                            .apply {
                                if (element.value.isNotEmpty()) {
                                    appendLine(""">${element.value}</$STRING_ELEMENT_NAME>""")
                                } else {
                                    appendLine("""/>""")
                                }
                            }
                    }

                    is StringsXmlResource.PluralsElement -> {
                        append(indent.repeat(1))
                            .appendLine("""<$PLURALS_ELEMENT_NAME $NAME_ATTRIBUTE_NAME="${element.name}">""")
                        for (item in element.items) {
                            append(indent.repeat(2))
                                .append("""<$ITEM_ELEMENT_NAME $QUANTITY_ATTRIBUTE_NAME="${item.quantity.value}"""")
                                .apply {
                                    if (!item.translatable) {
                                        append(""" $TRANSLATABLE_ATTRIBUTE_NAME="false"""")
                                    }
                                }
                                .apply {
                                    if (item.value.isNotEmpty()) {
                                        appendLine(""">${item.value}</$ITEM_ELEMENT_NAME>""")
                                    } else {
                                        appendLine("""/>""")
                                    }
                                }
                        }
                        append(indent.repeat(1))
                            .appendLine("""</$PLURALS_ELEMENT_NAME>""")
                    }

                    is StringsXmlResource.StringArrayElement -> {
                        append(indent.repeat(1))
                            .appendLine("""<$STRING_ARRAY_ELEMENT_NAME $NAME_ATTRIBUTE_NAME="${element.name}">""")
                        for (item in element.items) {
                            append(indent.repeat(2))
                                .append("""<$ITEM_ELEMENT_NAME""")
                                .apply {
                                    if (!item.translatable) {
                                        append(""" $TRANSLATABLE_ATTRIBUTE_NAME="false"""")
                                    }
                                }
                                .apply {
                                    if (item.value.isNotEmpty()) {
                                        appendLine(""">${item.value}</$ITEM_ELEMENT_NAME>""")
                                    } else {
                                        appendLine("""/>""")
                                    }
                                }
                        }
                        append(indent.repeat(1))
                            .appendLine("""</$STRING_ARRAY_ELEMENT_NAME>""")
                    }
                }
            }
            appendLine("""</resources>""")
        }.toString()
    }
}

private val XML_ENTITY_REGEX = Regex("""&(.*?);""")

/**
 * This function will return a map that contains entity names as keys, and
 * random integer strings as values. The values are guaranteed not to have
 * appeared in the original xml.
 *
 * @param xmlString The xml to generate the replacements for
 * @return a map of entity names to unique random strings
 *
 * Extracted from: https://stackoverflow.com/a/7720323/9288365
 */
private fun calculateEntityReplacements(xmlString: String): Map<String, String> {
    val replacementMap: MutableMap<String, String> = HashMap()
    XML_ENTITY_REGEX.findAll(xmlString).forEach { result ->
        val entityName = result.groupValues[1]

        val replacement: String
        if (!replacementMap.containsKey(entityName)) {
            replacement = "[${Random.nextInt()}]"
            replacementMap[entityName] = replacement
        }
    }

    return replacementMap
}

/**
 * This function takes the Map generated by the calculateEntityReplacements
 * function, and uses those values to replace any entities in the XML string
 * with their unique random integer replacements. The end results is an XML
 * string that contains no entities, but contains identifiable strings that
 * can be used to replace those entities at a later point.
 *
 * @param replacements
 * The Map generated by the calculateEntityReplacements function
 * @param xmlStrings
 * The XML string to modify
 * @return The modified XML
 *
 * Extracted from: https://stackoverflow.com/a/7720323/9288365
 */
private fun replaceEntities(xmlStrings: String, replacements: Map<String, String>): String {
    var retValue = xmlStrings
    for (entity in replacements.keys) retValue = retValue.replace(
        ("\\&$entity;").toRegex(),
        replacements[entity]!!
    )
    return retValue
}

/**
 * This function takes a parsed Document, along with the Map generated by
 * the calculateEntityReplacements function, and restores all the entities.
 *
 * Extracted from: https://stackoverflow.com/a/7720323/9288365
 */
private fun restoreEntities(xmlDocument: StringsXmlDocument, replacements: Map<String, String>): StringsXmlDocument {
    return StringsXmlDocument(
        xmlDocument.resources.map { element ->
            when (element) {
                is StringsXmlResource.StringElement -> {
                    element.copy(
                        value = replacements.entries.fold(element.value) { string, (entity, replacement) ->
                            string.replace(
                                replacement,
                                "&$entity;"
                            )
                        }
                    )
                }

                is StringsXmlResource.PluralsElement -> {
                    element.copy(
                        items = element.items.map { item ->
                            item.copy(
                                value = replacements.entries.fold(item.value) { string, (entity, replacement) ->
                                    string.replace(
                                        replacement,
                                        "&$entity;"
                                    )
                                }
                            )
                        }
                    )
                }

                is StringsXmlResource.StringArrayElement -> {
                    element.copy(
                        items = element.items.map { item ->
                            item.copy(
                                value = replacements.entries.fold(item.value) { string, (entity, replacement) ->
                                    string.replace(
                                        replacement,
                                        "&$entity;"
                                    )
                                }
                            )
                        }
                    )
                }
            }
        }
    )
}

private class SaxStringsXmlDocumentHandler : DefaultHandler(), LexicalHandler {
    private val resources = mutableListOf<StringsXmlResource>()

    private var currentElement: String? = null
    private var currentName: String? = null
    private var currentValue: StringBuilder? = null
    private var currentTranslatable: Boolean? = null
    private var currentComments = mutableListOf<String>()
    private var currentPluralItems = mutableListOf<StringsXmlResource.PluralsElement.Item>()
    private var currentStringArrayItems = mutableListOf<StringsXmlResource.StringArrayElement.Item>()
    private var isInCData = false

    val parsedResources: StringsXmlDocument
        get() = StringsXmlDocument(resources)

    override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
        when (qName) {
            STRING_ELEMENT_NAME -> {
                currentElement = STRING_ELEMENT_NAME
                currentName = attributes!!.getValue(NAME_ATTRIBUTE_NAME)
                currentTranslatable = (attributes.getValue(TRANSLATABLE_ATTRIBUTE_NAME) ?: "true").toBoolean()
                currentValue = StringBuilder()
            }

            PLURALS_ELEMENT_NAME -> {
                currentElement = PLURALS_ELEMENT_NAME
                currentName = attributes!!.getValue(NAME_ATTRIBUTE_NAME)
                currentPluralItems = mutableListOf()
            }

            STRING_ARRAY_ELEMENT_NAME -> {
                currentElement = STRING_ARRAY_ELEMENT_NAME
                currentName = attributes!!.getValue(NAME_ATTRIBUTE_NAME)
                currentStringArrayItems = mutableListOf()
            }

            ITEM_ELEMENT_NAME -> {
                currentTranslatable = (attributes!!.getValue(TRANSLATABLE_ATTRIBUTE_NAME) ?: "true").toBoolean()
                when (currentElement) {
                    PLURALS_ELEMENT_NAME -> {
                        val quantity = attributes.getValue(QUANTITY_ATTRIBUTE_NAME)
                        currentValue = StringBuilder()

                        if (quantity != null) {
                            currentPluralItems.add(
                                StringsXmlResource.PluralsElement.Item(
                                    StringsXmlResource.PluralsElement.Quantity.from(quantity),
                                    "",
                                    true
                                )
                            )
                        }
                    }

                    STRING_ARRAY_ELEMENT_NAME -> {
                        currentValue = StringBuilder()
                    }
                }
            }

            else -> {
                // Add element as is to value, including attributes
                val attributesString = (0 until (attributes?.length ?: 0)).fold(listOf(qName)) { list, index ->
                    list.plus("${attributes!!.getLocalName(index)}=\"${attributes.getValue(index)}\"")
                }.joinToString(" ")

                currentValue?.append("<$attributesString>")
            }
        }
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
        when (qName) {
            STRING_ELEMENT_NAME -> {
                resources.add(
                    StringsXmlResource.StringElement(
                        name = currentName!!,
                        value = currentValue!!.toString(),
                        comments = currentComments.toList(),
                        translatable = currentTranslatable!!
                    )
                )
                currentComments.clear()
                currentElement = null
                currentTranslatable = null
            }

            PLURALS_ELEMENT_NAME -> {
                resources.add(
                    StringsXmlResource.PluralsElement(
                        name = currentName!!,
                        items = currentPluralItems.toList(),
                        comments = currentComments.toList()
                    )
                )
                currentComments.clear()
                currentElement = null
            }

            STRING_ARRAY_ELEMENT_NAME -> {
                resources.add(
                    StringsXmlResource.StringArrayElement(
                        name = currentName!!,
                        items = currentStringArrayItems.toList(),
                        comments = currentComments.toList()
                    )
                )
                currentComments.clear()
                currentElement = null
            }

            ITEM_ELEMENT_NAME -> {
                when (currentElement) {
                    PLURALS_ELEMENT_NAME -> {
                        val lastIndex = currentPluralItems.lastIndex
                        if (lastIndex >= 0) {
                            currentPluralItems[lastIndex] =
                                currentPluralItems[lastIndex].copy(
                                    value = currentValue.toString(),
                                    translatable = currentTranslatable!!
                                )
                        }
                    }

                    STRING_ARRAY_ELEMENT_NAME -> {
                        currentStringArrayItems.add(
                            StringsXmlResource.StringArrayElement.Item(
                                currentValue.toString(),
                                translatable = currentTranslatable!!
                            )
                        )
                    }
                }
                currentValue = null
                currentTranslatable = null
            }

            else -> {
                // Close the tag in current value
                currentValue?.append("</$qName>")
            }
        }
    }

    override fun characters(ch: CharArray?, start: Int, length: Int) {
        val slice = ch?.sliceArray(start until start + length)
        currentValue?.appendRange(slice!!, 0, length)
    }

    override fun startDTD(name: String?, publicId: String?, systemId: String?) {
    }

    override fun endDTD() {
    }

    override fun startEntity(name: String?) {
    }

    override fun endEntity(name: String?) {
    }

    override fun startCDATA() {
        // Keep CDATA as is
        isInCData = true
        currentValue?.append("<![CDATA[")
    }

    override fun endCDATA() {
        // Keep CDATA as is
        isInCData = false
        currentValue?.append("]]>")
    }

    override fun comment(ch: CharArray?, start: Int, length: Int) {
        // Save comments for later usage
        val comment = ch?.concatToString(start, start + length)?.trim()
        if (comment != null) {
            currentComments.add(comment)
        }
    }
}
