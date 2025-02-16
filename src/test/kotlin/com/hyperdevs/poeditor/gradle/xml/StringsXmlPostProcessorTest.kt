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
@file:Suppress("LongMethod")

package com.hyperdevs.poeditor.gradle.xml

import com.hyperdevs.poeditor.gradle.utils.ALL_REGEX_STRING
import com.hyperdevs.poeditor.gradle.utils.TABLET_REGEX_STRING
import com.hyperdevs.poeditor.gradle.xml.models.StringsXmlDocument
import com.hyperdevs.poeditor.gradle.xml.models.StringsXmlResource
import groovy.test.GroovyTestCase.assertEquals
import org.junit.Test

class StringsXmlPostProcessorTest {

    /**
     * Plugin tests
     */
    @Test
    fun `Formatting resource with percentage symbol in strings works`() {
        // Test % is changed to %%
        val resource = StringsXmlResource.StringElement("test", "Hello my friend. I love you 100%.")

        assertEquals(
            StringsXmlResource.StringElement(resource.name, "Hello my friend. I love you 100%."),
            StringsXmlPostProcessor.formatResource(resource, false, null)
        )
    }

    @Test
    fun `Formatting resource with percentage symbol in text with variables works`() {
        // Test % is not changed if variables are present
        val resource = StringsXmlResource.StringElement("test", "Hello {{friend}}. I love you 100%.")

        assertEquals(
            StringsXmlResource.StringElement(resource.name, "Hello %1\$s. I love you 100%%."),
            StringsXmlPostProcessor.formatResource(resource, false, null)
        )
    }

    @Test
    fun `Formatting resource with line breaks keeps them`() {
        // Test \n is maintained
        val resource = StringsXmlResource.StringElement("test", "Hello my friend\nHow are you?.")

        assertEquals(
            StringsXmlResource.StringElement(resource.name, "Hello my friend\nHow are you?."),
            StringsXmlPostProcessor.formatResource(resource, false, null)
        )
    }

    @Test
    fun `Formatting resource with percentage symbol in text with line breaks and variables works`() {
        // Test % is changed if variables in multiple lines are present
        val resource = StringsXmlResource.StringElement("test", "I love you 100%.\nMy friend {{friend}}.")

        assertEquals(
            StringsXmlResource.StringElement(resource.name, "I love you 100%%.\nMy friend %1\$s."),
            StringsXmlPostProcessor.formatResource(resource, false, null)
        )
    }

    @Test
    fun `Formatting resource with percentage symbol in text with line breaks works`() {
        // Test % is not changed if multiple lines are present
        val resource = StringsXmlResource.StringElement("test", "I love you this much:\n100%.")

        assertEquals(
            StringsXmlResource.StringElement(resource.name, "I love you this much:\n100%."),
            StringsXmlPostProcessor.formatResource(resource, false, null)
        )
    }

    @Test
    fun `Formatting resource with variables in strings works`() {
        // Test variables are converted to Android format
        val resource = StringsXmlResource.StringElement("test", "Hello {{name}}.")

        assertEquals(
            StringsXmlResource.StringElement(resource.name, "Hello %1\$s."),
            StringsXmlPostProcessor.formatResource(resource, false, null)
        )

        val resource2 = StringsXmlResource.StringElement("test", "Hello {{name}}. How are you {{name}}?")

        assertEquals(
            StringsXmlResource.StringElement(resource2.name, "Hello %1\$s. How are you %1\$s?"),
            StringsXmlPostProcessor.formatResource(resource2, false, null)
        )

        val resource3 = StringsXmlResource.StringElement("test", "Hello {1{name}}. This is your score: {2{score}}")

        assertEquals(
            StringsXmlResource.StringElement(resource3.name, "Hello %1\$s. This is your score: %2\$s"),
            StringsXmlPostProcessor.formatResource(resource3, false, null)
        )

        val resource4 = StringsXmlResource.StringElement(
            "test", "Hello {1{name}} {2{name}} {3{name}} {4{name}} {5{name}} " +
                    "{6{name}} {7{name}} {8{name}} {9{name}} {10{name}} {11{name}}"
        )

        assertEquals(
            StringsXmlResource.StringElement(
                resource4.name,
                "Hello %1\$s %2\$s %3\$s %4\$s %5\$s %6\$s %7\$s %8\$s %9\$s %10\$s %11\$s"
            ),
            StringsXmlPostProcessor.formatResource(resource4, false, null)
        )
    }

    @Test
    fun `Formatting resource with variables in plurals works`() {
        // Test variables are converted to Android format
        val resource = StringsXmlResource.PluralsElement(
            "test",
            listOf(
                StringsXmlResource.PluralsElement.Item(
                    StringsXmlResource.PluralsElement.Quantity.ZERO,
                    "Zero {{name}}s."
                ),
                StringsXmlResource.PluralsElement.Item(StringsXmlResource.PluralsElement.Quantity.ONE, "One {{name}}."),
                StringsXmlResource.PluralsElement.Item(
                    StringsXmlResource.PluralsElement.Quantity.TWO,
                    "Two {{name}}s."
                ),
                StringsXmlResource.PluralsElement.Item(
                    StringsXmlResource.PluralsElement.Quantity.FEW,
                    "Few {{name}}s."
                ),
                StringsXmlResource.PluralsElement.Item(
                    StringsXmlResource.PluralsElement.Quantity.MANY,
                    "Many {{name}}s."
                ),
                StringsXmlResource.PluralsElement.Item(
                    StringsXmlResource.PluralsElement.Quantity.OTHER,
                    "Other {{name}}s."
                )
            )
        )

        assertEquals(
            StringsXmlResource.PluralsElement(
                resource.name,
                listOf(
                    StringsXmlResource.PluralsElement.Item(
                        StringsXmlResource.PluralsElement.Quantity.ZERO,
                        "Zero %1\$ss."
                    ),
                    StringsXmlResource.PluralsElement.Item(
                        StringsXmlResource.PluralsElement.Quantity.ONE,
                        "One %1\$s."
                    ),
                    StringsXmlResource.PluralsElement.Item(
                        StringsXmlResource.PluralsElement.Quantity.TWO,
                        "Two %1\$ss."
                    ),
                    StringsXmlResource.PluralsElement.Item(
                        StringsXmlResource.PluralsElement.Quantity.FEW,
                        "Few %1\$ss."
                    ),
                    StringsXmlResource.PluralsElement.Item(
                        StringsXmlResource.PluralsElement.Quantity.MANY,
                        "Many %1\$ss."
                    ),
                    StringsXmlResource.PluralsElement.Item(
                        StringsXmlResource.PluralsElement.Quantity.OTHER,
                        "Other %1\$ss."
                    )
                )
            ),
            StringsXmlPostProcessor.formatResource(resource, false, null)
        )
    }

    @Test
    fun `Postprocessing complex XML works`() {
        // Test complete Xml
        val inputStringsXmlDocument = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("general_link_showAll", "Ver todo {{name}}"),
                StringsXmlResource.StringElement("general_button_goTop", "Ir arriba"),
                StringsXmlResource.StringElement(
                    "general_button_goTop_tablet",
                    "Ir arriba {1{name}} usuario {2{user_name}}"
                ),
                StringsXmlResource.StringElement("general_button_goBottom", "Ir${'\n'}abajo")
            )
        )

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("general_link_showAll", "Ver todo %1\$s"),
                StringsXmlResource.StringElement("general_button_goTop", "Ir arriba"),
                StringsXmlResource.StringElement("general_button_goTop_tablet", "Ir arriba %1\$s usuario %2\$s"),
                StringsXmlResource.StringElement("general_button_goBottom", "Ir${'\n'}abajo")
            )
        )

        assertEquals(expectedResult, StringsXmlPostProcessor.formatTranslationXml(inputStringsXmlDocument, true, null, true))
    }

    @Test
    fun `Postprocessing XML with percentages works`() {
        // Test complete Xml
        val inputStringsXmlDocument = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("hello_friend", "Hello {{name}}"),
                StringsXmlResource.StringElement("hello_friend_love_100", "Hello {{name}}. I love you 100%"),
                StringsXmlResource.StringElement("hello_love_100", "Hello. I love you 100%")
            )
        )

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("hello_friend", "Hello %1\$s"),
                StringsXmlResource.StringElement("hello_friend_love_100", "Hello %1\$s. I love you 100%%"),
                StringsXmlResource.StringElement("hello_love_100", "Hello. I love you 100%")
            )
        )

        assertEquals(expectedResult, StringsXmlPostProcessor.formatTranslationXml(inputStringsXmlDocument, true, null, true))
    }

    @Test
    fun `Postprocessing XML with percentages and line breaks works`() {
        // Test complete Xml
        val inputStringsXmlDocument = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("hello_friend", "I love you this much:\n100%")
            )
        )

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("hello_friend", "I love you this much:\n100%")
            )
        )

        assertEquals(expectedResult, StringsXmlPostProcessor.formatTranslationXml(inputStringsXmlDocument, true, null, true))
    }

    @Test
    fun `Postprocessing XML with percentages and variables with line breaks works`() {
        // Test complete Xml
        val inputStringsXmlDocument = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("hello_friend", "Hello {{name}}\nI love you 100%")
            )
        )

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("hello_friend", "Hello %1\$s\nI love you 100%%")
            )
        )

        assertEquals(expectedResult, StringsXmlPostProcessor.formatTranslationXml(inputStringsXmlDocument, true, null, true))
    }

    @Test
    fun `Postprocessing XML with plurals works`() {
        // Test complete Xml
        val inputStringsXmlDocument = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("general_link_showAll", "Ver todo {{name}}"),
                StringsXmlResource.StringElement("general_button_goTop", "Ir arriba"),
                StringsXmlResource.PluralsElement(
                    "general_quantity",
                    listOf(
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.ONE,
                            "Un elemento seleccionado"
                        ),
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.OTHER,
                            "{{element_quantity}} elementos seleccionados"
                        )
                    )
                )
            )
        )

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("general_link_showAll", "Ver todo %1\$s"),
                StringsXmlResource.StringElement("general_button_goTop", "Ir arriba"),
                StringsXmlResource.PluralsElement(
                    "general_quantity",
                    listOf(
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.ONE,
                            "Un elemento seleccionado"
                        ),
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.OTHER,
                            "%1\$s elementos seleccionados"
                        )
                    )
                )
            )
        )

        assertEquals(expectedResult, StringsXmlPostProcessor.formatTranslationXml(inputStringsXmlDocument, true, null, true))
    }

    @Test
    fun `Postprocessing XML with plurals and percentages works`() {
        // Test complete Xml
        val inputStringsXmlDocument = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.PluralsElement(
                    "hello_friend",
                    listOf(
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.ONE,
                            "Hello my {{ friend_number }} friend"
                        ),
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.OTHER,
                            "Hello my {{ friend_number }} friends"
                        )
                    )
                ),
                StringsXmlResource.PluralsElement(
                    "hello_friend_love_100",
                    listOf(
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.ONE,
                            "Hello my {{ friend_number }} friend. I love you 100%"
                        ),
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.OTHER,
                            "Hello my {{ friend_number }} friends. I love you 100%"
                        )
                    )
                ),
                StringsXmlResource.PluralsElement(
                    "hello_love_100",
                    listOf(
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.ONE,
                            "Hello friend. I love you 100%"
                        ),
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.OTHER,
                            "Hello friends. I love you 100%"
                        )
                    )
                )
            )
        )

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.PluralsElement(
                    "hello_friend",
                    listOf(
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.ONE,
                            "Hello my %1\$s friend"
                        ),
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.OTHER,
                            "Hello my %1\$s friends"
                        )
                    )
                ),
                StringsXmlResource.PluralsElement(
                    "hello_friend_love_100",
                    listOf(
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.ONE,
                            "Hello my %1\$s friend. I love you 100%%"
                        ),
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.OTHER,
                            "Hello my %1\$s friends. I love you 100%%"
                        )
                    )
                ),
                StringsXmlResource.PluralsElement(
                    "hello_love_100",
                    listOf(
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.ONE,
                            "Hello friend. I love you 100%"
                        ),
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.OTHER,
                            "Hello friends. I love you 100%"
                        )
                    )
                )
            )
        )

        assertEquals(expectedResult, StringsXmlPostProcessor.formatTranslationXml(inputStringsXmlDocument, true, null, true))
    }

    @Test
    fun `Postprocessing XML with string HTML symbols works`() {
        // Test complete Xml
        val inputStringsXmlDocument = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("hello_friend_bold", "Hello &lt;b&gt;{{name}}&lt;/b&gt;")
            )
        )

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("hello_friend_bold", "Hello <b>%1${'$'}s</b>")
            )
        )

        assertEquals(expectedResult, StringsXmlPostProcessor.formatTranslationXml(inputStringsXmlDocument, true, null, true))
    }

    @Test
    fun `Postprocessing XML with string HTML symbols and unescape set to false works`() {
        // Test complete Xml
        val inputStringsXmlDocument = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("hello_friend_bold", "Hello &lt;b&gt;{{name}}&lt;/b&gt;")
            )
        )

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("hello_friend_bold", "Hello &lt;b&gt;%1${'$'}s&lt;/b&gt;")
            )
        )

        assertEquals(expectedResult, StringsXmlPostProcessor.formatTranslationXml(inputStringsXmlDocument, false, null, true))
    }

    @Test
    fun `Postprocessing XML with string HTML symbols and unescape set to false works 2`() {
        // Test complete Xml
        val inputStringsXmlDocument = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("hello_friend_bold", "&amp;lt;b&amp;gt;Hello world&amp;lt;/b&amp;gt;")
            )
        )

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("hello_friend_bold", "&amp;lt;b&amp;gt;Hello world&amp;lt;/b&amp;gt;")
            )
        )

        assertEquals(expectedResult, StringsXmlPostProcessor.formatTranslationXml(inputStringsXmlDocument, false, null, true))
    }

    @Test
    fun `Postprocessing XML with CDATA works`() {
        // Test complete Xml
        val inputStringsXmlDocument = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("cdata", "Some text<a href=\"{{link}}\">Link</a> text text")
            )
        )

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("cdata", "Some text<a href=\"%1${'$'}s\">Link</a> text text")
            )
        )

        assertEquals(expectedResult, StringsXmlPostProcessor.formatTranslationXml(inputStringsXmlDocument, true, null, true))
    }

    @Test
    fun `Postprocessing XML with multiline CDATA works`() {
        // Test complete Xml
        val inputStringsXmlDocument = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement(
                    "cdata",
                    """
                    <br />
                    <p><a href="mailto:{{email}}">ABC Email</a></p>
                    """.trimIndent()
                )
            )
        )

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement(
                    "cdata",
                    """
                    <br />
                    <p><a href="mailto:%1${'$'}s">ABC Email</a></p>
                    """.trimIndent()
                )
            )
        )

        assertEquals(expectedResult, StringsXmlPostProcessor.formatTranslationXml(inputStringsXmlDocument, true, null, true))
    }

    @Test
    fun `Splitting tablet translation strings works`() {
        // Test complete Xml
        val expectedKey = "general_button_goTop"

        val inputStringsXmlDocument = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement(expectedKey, "\"$expectedKey\""),
                StringsXmlResource.StringElement("${expectedKey}_tablet", "\"${expectedKey}_tablet\"")
            )
        )

        val allRegexString = ALL_REGEX_STRING
        val tabletRegexString = TABLET_REGEX_STRING

        val splitTranslationXmlMap =
            StringsXmlPostProcessor.splitTranslationXml(inputStringsXmlDocument, listOf(tabletRegexString))

        assertEquals(
            expectedKey,
            splitTranslationXmlMap[allRegexString]!!.resources.first().name
        )
        assertEquals(
            expectedKey,
            splitTranslationXmlMap[tabletRegexString]!!.resources.first().name
        )

        assertEquals(
            "\"$expectedKey\"",
            (splitTranslationXmlMap[allRegexString]!!.resources.first() as StringsXmlResource.StringElement).value
        )
        assertEquals(
            "\"${expectedKey}_tablet\"",
            (splitTranslationXmlMap[tabletRegexString]!!.resources.first() as StringsXmlResource.StringElement).value
        )
    }

    @Test
    fun `Splitting tablet translation strings with plurals works`() {
        // Test complete Xml
        val expectedKey = "general_quantity"
        val inputStringsXmlDocument = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.PluralsElement(
                    expectedKey,
                    listOf(
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.ONE,
                            "{{element_quantity}} elemento seleccionado"
                        ),
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.OTHER,
                            "{{element_quantity}} elementos seleccionados"
                        )
                    )
                ),
                StringsXmlResource.PluralsElement(
                    "${expectedKey}_tablet",
                    listOf(
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.ONE,
                            "{{element_quantity}} elemento seleccionado en tablet"
                        ),
                        StringsXmlResource.PluralsElement.Item(
                            StringsXmlResource.PluralsElement.Quantity.OTHER,
                            "{{element_quantity}} elementos seleccionados en tablet"
                        )
                    )
                )
            )
        )

        val allRegexString = ALL_REGEX_STRING
        val tabletRegexString = TABLET_REGEX_STRING

        val splitTranslationXmlMap =
            StringsXmlPostProcessor.splitTranslationXml(inputStringsXmlDocument, listOf(tabletRegexString))

        assertEquals(
            expectedKey,
            splitTranslationXmlMap[allRegexString]!!.resources.first().name
        )
        assertEquals(
            expectedKey,
            splitTranslationXmlMap[tabletRegexString]!!.resources.first().name
        )
    }

    @Test
    fun `Postprocessing with untranslated pattern works`() {
        // Test complete Xml
        val inputStringsXmlDocument = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("untranslatable_string", "\"1234\""),
                StringsXmlResource.StringElement("translatable_string", "\"¡Hola!\"")
            )
        )

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("untranslatable_string", "\"1234\"", translatable = false),
                StringsXmlResource.StringElement("translatable_string", "\"¡Hola!\"")
            )
        )

        assertEquals(
            expectedResult,
            StringsXmlPostProcessor.formatTranslationXml(
                inputStringsXmlDocument,
                true,
                Regex("""^untranslatable(.+)$"""),
                true
            )
        )
    }

    @Test
    fun `Postprocessing with including comments works`() {
        // Test complete Xml
        val inputStringsXmlDocument = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("general_link_showAll", "Ver todo {{name}}", comments = listOf("Comment 1")),
                StringsXmlResource.StringElement("general_button_goTop", "Ir arriba", comments = listOf("Comment 2")),
                StringsXmlResource.StringElement("general_button_goBottom", "Ir${'\n'}abajo", comments = listOf("Comment 3"))
            )
        )

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("general_link_showAll", "Ver todo %1\$s", comments = listOf("Comment 1")),
                StringsXmlResource.StringElement("general_button_goTop", "Ir arriba", comments = listOf("Comment 2")),
                StringsXmlResource.StringElement("general_button_goBottom", "Ir${'\n'}abajo", comments = listOf("Comment 3"))
            )
        )

        assertEquals(expectedResult, StringsXmlPostProcessor.formatTranslationXml(inputStringsXmlDocument, true, null, true))
    }

    @Test
    fun `Postprocessing without including comments works`() {
        // Test complete Xml
        val inputStringsXmlDocument = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("general_link_showAll", "Ver todo {{name}}", comments = listOf("Comment 1")),
                StringsXmlResource.StringElement("general_button_goTop", "Ir arriba", comments = listOf("Comment 2")),
                StringsXmlResource.StringElement("general_button_goBottom", "Ir${'\n'}abajo", comments = listOf("Comment 3"))
            )
        )

        val expectedResult = StringsXmlDocument(
            resources = listOf(
                StringsXmlResource.StringElement("general_link_showAll", "Ver todo %1\$s"),
                StringsXmlResource.StringElement("general_button_goTop", "Ir arriba"),
                StringsXmlResource.StringElement("general_button_goBottom", "Ir${'\n'}abajo")
            )
        )

        assertEquals(expectedResult, StringsXmlPostProcessor.formatTranslationXml(inputStringsXmlDocument, true, null, false))
    }
}
