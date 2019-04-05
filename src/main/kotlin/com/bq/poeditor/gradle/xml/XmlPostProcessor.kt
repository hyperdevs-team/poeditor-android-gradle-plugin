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

package com.bq.poeditor.gradle.xml

import com.bq.poeditor.gradle.utils.ALL_REGEX_STRING
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Class that handles XML transformation.
 */
@Suppress("StringLiteralDuplication")
class XmlPostProcessor {
    companion object {
        private val DEFAULT_ENCODING = Charsets.UTF_8
        private val TEXT_VARIABLE_REGEX = Regex("""\{\d?\{(.*?)\}\}""")
    }

    /**
     * Does a full post-processing of an Android strings.xml file.
     * The process is the following:
     *
     * - Format variables and texts to conform to Android strings.xml format
     * - Split to multiple XML files depending on regex matching
     */
    fun postProcessTranslationXml(translationXmlString: String,
                                  fileSplitRegexStringList: List<String>): Map<String, Document> =
        splitTranslationXml(formatTranslationXml(translationXmlString), fileSplitRegexStringList)

    /**
     * Format variables and texts to conform to Android strings.xml format.
     */
    fun formatTranslationXml(translationXmlString: String): String {
        return translationXmlString
            // Replace % with %%
            .replace("%", "%%")
            // Replace &lt; with < and &gt; with >
            .replace("&lt;", "<").replace("&gt;", ">")
            // Replace placeholders from {{variable}} to %1$s format.
            // First of all, manage each string separately
            .split("</string>")
            .joinToString("</string>") { s ->
                // Second, replace each placeholder taking into account their order set as part of the placeholder
                val transform: (MatchResult) -> CharSequence = { matchResult ->
                    // TODO: if the string has multiple variables but any of them has no order number,
                    //  throw an exception
                    // If the placeholder contains an ordinal, use it: {2{pages_count}} -> %2$s
                    val match = matchResult.groupValues[0]
                    if (Character.isDigit(match[1])) {
                        "%${match[1]}\$s"
                    } else { // If not, use "1" as the ordinal: {{pages_count}} -> %1%s
                        "%1\$s"
                    }
                }
                s.replace(TEXT_VARIABLE_REGEX, transform)
            }
    }

    /**
     * Splits an Android XML file to multiple XML files depending on regex matching.
     */
    fun splitTranslationXml(translationXmlString: String,
                            fileSplitRegexStringList: List<String>): Map<String, Document> {
        val translationFileRecords = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(translationXmlString.byteInputStream(DEFAULT_ENCODING))

        return fileSplitRegexStringList
            .map { regex ->
                // Extract strings matched by patterns to a separate strings XML
                regex to extractMatchingNodes(translationFileRecords.childNodes, regex)
            }
            .filter { (_, nodes) -> nodes.isNotEmpty() } // Only process suffixes with at least one coincidence
            .toMap()
            .mapValues { (regexString, nodes) ->
                val regex = Regex(regexString)
                val xmlString = "<resources></resources>"
                val xmlRecords = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(xmlString.byteInputStream(DEFAULT_ENCODING))
                nodes.forEach { node ->
                    node.parentNode.removeChild(node)
                    val copiedNode = (node.cloneNode(true) as Element).apply {
                        val name = getAttribute("name")
                        val nameWithoutRegex = regex.find(name)?.groups?.get(1)?.value ?: ""
                        setAttribute("name", nameWithoutRegex)
                    }
                    xmlRecords.adoptNode(copiedNode)
                    xmlRecords.firstChild.appendChild(copiedNode)
                }

                // Remove empty nodes from resulting XMLs
                sanitizeNodes(xmlRecords)
                xmlRecords
            }
            .plus(ALL_REGEX_STRING to translationFileRecords)
    }

    private fun extractMatchingNodes(nodeList: NodeList, regexString: String): List<Node> {
        val matchedNodes = mutableListOf<Node>()
        val regex = Regex(regexString)

        for (i in 0 until nodeList.length) {
            if (nodeList.item(i).nodeType == Node.ELEMENT_NODE) {
                val nodeElement = nodeList.item(i) as Element
                if (nodeElement.tagName == "resources") {
                    matchedNodes.addAll(extractMatchingNodes(nodeElement.childNodes, regexString))
                } else if (nodeElement.tagName == "string" && nodeElement.getAttribute("name").matches(regex)) {
                    matchedNodes.add(nodeElement)
                }
            }
        }
        return matchedNodes
    }

    /**
     * Removes empty nodes for better serialization
     *
     * Extracted from https://stackoverflow.com/a/31421664/9288365
     */
    private fun sanitizeNodes(node: Node) {
        var child: Node? = node.firstChild
        while (child != null) {
            val sibling = child.nextSibling
            if (child.nodeType == Node.TEXT_NODE) {
                if (child.textContent.trim { it <= ' ' }.isEmpty()) {
                    node.removeChild(child)
                }
            } else {
                sanitizeNodes(child)
            }
            child = sibling
        }
    }
}