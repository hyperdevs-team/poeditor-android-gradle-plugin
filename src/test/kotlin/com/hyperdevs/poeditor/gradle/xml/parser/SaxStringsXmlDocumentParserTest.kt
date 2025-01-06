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
import com.hyperdevs.poeditor.gradle.xml.models.StringsXmlResource
import groovy.test.GroovyTestCase.assertEquals
import org.junit.Before
import org.junit.Test

class SaxStringsXmlDocumentParserTest {
    private lateinit var parser: StringsXmlDocumentParser

    @Before
    fun setUp() {
        parser = SaxStringsXmlDocumentParser()
    }

    @Test
    fun `Deserializing complex XML works`() {
        // Test complete Xml
        val inputXmlString = """
                            <resources>
                              <string name="general_link_showAll">"Ver todo {{name}}"</string>
                              <string name="general_button_goTop">"Ir arriba"</string>
                              <string name="general_button_goTop_tablet">"Ir arriba {1{name}} usuario {2{user_name}}"</string>
                              <string name="general_button_goBottom">"Ir${'\n'}abajo"</string>
                            </resources>
                             """.trimIndent()

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement(
                    name = "general_link_showAll",
                    value = "\"Ver todo {{name}}\""
                ),
                StringsXmlResource.StringElement(
                    name = "general_button_goTop",
                    value = "\"Ir arriba\""
                ),
                StringsXmlResource.StringElement(
                    name = "general_button_goTop_tablet",
                    value = "\"Ir arriba {1{name}} usuario {2{user_name}}\""
                ),
                StringsXmlResource.StringElement(
                    name = "general_button_goBottom",
                    value = "\"Ir${'\n'}abajo\""
                )
            )
        )

        assertEquals(
            expectedResult,
            parser.deserialize(inputXmlString)
        )
    }

    @Test
    fun `Deserializing XML with line breaks works`() {
        // Test complete Xml
        val inputXmlString = """
                            <resources>
                              <string name="hello_friend">"I love you this much:
                            100%"</string>
                            </resources>
                             """.trimIndent()

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement(
                    name = "hello_friend",
                    value = """
                            "I love you this much:
                            100%"
                            """.trimIndent()
                )
            )
        )

        assertEquals(
            expectedResult,
            parser.deserialize(inputXmlString)
        )
    }

    @Test
    fun `Deserializing XML with variables with line breaks works`() {
        // Test complete Xml
        val inputXmlString = """
                            <resources>
                              <string name="hello_friend">"Hello {{name}}
                            I love you 100%"</string>
                            </resources>
                             """.trimIndent()

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement(
                    name = "hello_friend",
                    value = """
                            "Hello {{name}}
                            I love you 100%"
                            """.trimIndent()
                )
            )
        )

        assertEquals(
            expectedResult,
            parser.deserialize(inputXmlString)
        )
    }

    @Test
    fun `Deserializing XML with plurals works`() {
        // Test complete Xml
        val inputXmlString = """
                            <resources>
                              <string name="general_link_showAll">"Ver todo {{name}}"</string>
                              <string name="general_button_goTop">"Ir arriba"</string>
                              <plurals name="general_quantity">
                                <item quantity="one">"Un elemento seleccionado"</item>
                                <item quantity="other">"{{element_quantity}} elementos seleccionados"</item>
                              </plurals>
                            </resources>
                             """.trimIndent()

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement(
                    name = "general_link_showAll",
                    value = "\"Ver todo {{name}}\""
                ),
                StringsXmlResource.StringElement(
                    name = "general_button_goTop",
                    value = "\"Ir arriba\""
                ),
                StringsXmlResource.PluralsElement(
                    name = "general_quantity",
                    items = listOf(
                        StringsXmlResource.PluralsElement.Item(
                            quantity = StringsXmlResource.PluralsElement.Quantity.ONE,
                            value = "\"Un elemento seleccionado\""
                        ),
                        StringsXmlResource.PluralsElement.Item(
                            quantity = StringsXmlResource.PluralsElement.Quantity.OTHER,
                            value = "\"{{element_quantity}} elementos seleccionados\""
                        )
                    )
                )
            )
        )

        assertEquals(
            expectedResult,
            parser.deserialize(inputXmlString)
        )
    }

    @Test
    fun `Deserializing XML with string HTML symbols works`() {
        // Test complete Xml
        val inputXmlString = """
                            <resources>
                              <string name="hello_friend_bold">"Hello &lt;b&gt;{{name}}&lt;/b&gt;"</string>
                            </resources>
                             """.trimIndent()

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement(
                    name = "hello_friend_bold",
                    value = "\"Hello &lt;b&gt;{{name}}&lt;/b&gt;\""
                )
            )
        )

        assertEquals(
            expectedResult,
            parser.deserialize(inputXmlString)
        )
    }

    @Test
    fun `Deserializing XML with string HTML symbols works 2`() {
        // Test complete Xml
        val inputXmlString = """
                            <resources>
                              <string name="hello_friend_bold">"&amp;lt;b&amp;gt;Hello world&amp;lt;/b&amp;gt;"</string>
                            </resources>
                             """.trimIndent()

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement(
                    name = "hello_friend_bold",
                    value = "\"&amp;lt;b&amp;gt;Hello world&amp;lt;/b&amp;gt;\""
                )
            )
        )

        assertEquals(
            expectedResult,
            parser.deserialize(inputXmlString)
        )
    }

    @Test
    fun `Deserializing XML with CDATA works`() {
        // Test complete Xml
        val inputXmlString = """
                            <resources>
                              <string name="cdata"><![CDATA[Some text<a href="{{link}}">Link</a> text text]]></string>
                            </resources>
                             """.trimIndent()

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement(
                    name = "cdata",
                    value = "<![CDATA[Some text<a href=\"{{link}}\">Link</a> text text]]>"
                )
            )
        )

        assertEquals(
            expectedResult,
            parser.deserialize(inputXmlString)
        )
    }

    @Test
    fun `Deserializing XML with multiline CDATA works`() {
        // Test complete Xml
        val inputXmlString = """
                            <resources>
                              <string name="cdata"><![CDATA[
                              <br />
                              <p><a href="mailto:{{email}}">ABC Email</a></p>
                            ]]></string>
                            </resources>
                             """.trimIndent()

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement(
                    name = "cdata",
                    value = """
                            <![CDATA[
                              <br />
                              <p><a href="mailto:{{email}}">ABC Email</a></p>
                            ]]>
                            """.trimIndent()
                )
            )
        )

        assertEquals(
            expectedResult,
            parser.deserialize(inputXmlString)
        )
    }
}
