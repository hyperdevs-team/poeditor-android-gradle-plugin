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

import com.bq.poeditor.gradle.ktx.toAndroidXmlString
import com.bq.poeditor.gradle.utils.ALL_REGEX_STRING
import com.bq.poeditor.gradle.utils.TABLET_REGEX_STRING
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathFactory

class XmlPostProcessorTest {

    private lateinit var xmlPostProcessor: XmlPostProcessor
    private lateinit var xpathFactory: XPathFactory
    private lateinit var xp: XPath

    @Before
    fun setUp() {
        xmlPostProcessor = XmlPostProcessor()
        xpathFactory = XPathFactory.newInstance()
        xp = xpathFactory.newXPath()
    }

    /**
     * Plugin tests
     */
    @Test
    fun `Postprocessing percentage symbol in strings works`() {
        // Test % is changed to %%
        Assert.assertEquals("Hello my friend. I love you 100%.",
            xmlPostProcessor.formatTranslationString("Hello my friend. I love you 100%."))
    }

    @Test
    fun `Postprocessing percentage symbol in text with variables works`() {
        // Test % is not changed if variables are present
        Assert.assertEquals("Hello %1\$s. I love you 100%%.",
            xmlPostProcessor.formatTranslationString("Hello {{friend}}. I love you 100%."))
    }

    @Test
    fun `Postprocessing line breaks keeps them`() {
        // Test \n is maintained
        Assert.assertEquals("Hello my friend\nHow are you?.",
            xmlPostProcessor.formatTranslationString("Hello my friend\nHow are you?."))
    }

    @Test
    fun `Postprocessing percentage symbol in text with line breaks and variables works`() {
        // Test % is changed if variables in multiple lines are present
        Assert.assertEquals("I love you 100%%.\nMy friend %1\$s.",
            xmlPostProcessor.formatTranslationString("I love you 100%.\nMy friend {{friend}}."))
    }

    @Test
    fun `Postprocessing percentage symbol in text with line breaks works`() {
        // Test % is not changed if multiple lines are present
        Assert.assertEquals("I love you this much:\n100%.",
            xmlPostProcessor.formatTranslationString("I love you this much:\n100%."))
    }

    @Test
    fun `Postprocessing variables works`() {
        // Test variables are converted to Android format
        Assert.assertEquals("Hello %1\$s.",
            xmlPostProcessor.formatTranslationString("Hello {{name}}."))
        Assert.assertEquals("Hello %1\$s. How are you %1\$s?",
            xmlPostProcessor.formatTranslationString("Hello {{name}}. How are you {{name}}?"))
        Assert.assertEquals("Hello %1\$s. This is your score: %2\$s",
            xmlPostProcessor.formatTranslationString("Hello {1{name}}. This is your score: {2{score}}"))
    }

    @Test
    fun `Postprocessing string HTML escapes sequences`() {
        // Test Html tags are fixed
        Assert.assertEquals("Hello <b>%1\$s</b>.",
            xmlPostProcessor.formatTranslationString("Hello &lt;b&gt;{{name}}&lt;/b&gt;."))
    }

    @Test
    fun `Postprocessing complex XML works`() {
        // Test complete Xml
        val inputXmlString = """
                            <resources>
                              <string name="general_link_showAll">
                                "Ver todo {{name}}"
                              </string>
                              <string name="general_button_goTop">
                                "Ir arriba"
                              </string>
                              <string name="general_button_goTop_tablet">
                                "Ir arriba {1{name}} usuario {2{user_name}}"
                              </string>
                              <string name="general_button_goBottom">
                                "Ir${'\n'}abajo"
                              </string>
                            </resources>
                             """

        val expectedResult = """
                            <resources>
                              <string name="general_link_showAll">
                                "Ver todo %1${'$'}s"
                              </string>
                              <string name="general_button_goTop">
                                "Ir arriba"
                              </string>
                              <string name="general_button_goTop_tablet">
                                "Ir arriba %1${'$'}s usuario %2${'$'}s"
                              </string>
                              <string name="general_button_goBottom">
                                "Ir${'\n'}abajo"
                              </string>
                            </resources>
                             """.formatXml()

        Assert.assertEquals(expectedResult, xmlPostProcessor.formatTranslationXml(inputXmlString))
    }

    @Test
    fun `Postprocessing XML with percentages works`() {
        // Test complete Xml
        val inputXmlString = """
                            <resources>
                              <string name="hello_friend">
                                "Hello {{name}}"
                              </string>
                              <string name="hello_friend_love_100">
                                "Hello {{name}}. I love you 100%"
                              </string>
                              <string name="hello_love_100">
                                "Hello. I love you 100%"
                              </string>
                            </resources>
                             """

        val expectedResult = """
                            <resources>
                              <string name="hello_friend">
                                "Hello %1${'$'}s"
                              </string>
                              <string name="hello_friend_love_100">
                                "Hello %1${'$'}s. I love you 100%%"
                              </string>
                              <string name="hello_love_100">
                                "Hello. I love you 100%"
                              </string>
                            </resources>
                             """.formatXml()

        Assert.assertEquals(expectedResult, xmlPostProcessor.formatTranslationXml(inputXmlString))
    }

    @Test
    fun `Postprocessing XML with percentages and line breaks works`() {
        // Test complete Xml
        val inputXmlString = """
                            <resources>
                              <string name="hello_friend">
                                "I love you this much: 
                                100%"
                              </string>
                            </resources>
                             """

        val expectedResult = """
                            <resources>
                              <string name="hello_friend">
                                "I love you this much: 
                                100%"
                              </string>
                            </resources>
                             """.formatXml()

        Assert.assertEquals(expectedResult, xmlPostProcessor.formatTranslationXml(inputXmlString))
    }

    @Test
    fun `Postprocessing XML with percentages and variables with line breaks works`() {
        // Test complete Xml
        val inputXmlString = """
                            <resources>
                              <string name="hello_friend">
                                "Hello {{name}}
                                I love you 100%"
                              </string>
                            </resources>
                             """

        val expectedResult = """
                            <resources>
                              <string name="hello_friend">
                                "Hello %1${'$'}s
                                I love you 100%%"
                              </string>
                            </resources>
                             """.formatXml()

        Assert.assertEquals(expectedResult, xmlPostProcessor.formatTranslationXml(inputXmlString))
    }

    @Test
    fun `Postprocessing XML with plurals works`() {
        // Test complete Xml
        val inputXmlString = """
                            <resources>
                              <string name="general_link_showAll">
                                "Ver todo {{name}}"
                              </string>
                              <string name="general_button_goTop">
                                "Ir arriba"
                              </string>
                              <plurals name="general_quantity">
                                <item quantity="one">"Un elemento seleccionado"</item>
                                <item quantity="other">"{{element_quantity}} elementos seleccionados"</item>
                              </plurals>
                            </resources>
                             """

        val expectedResult = """
                            <resources>
                              <string name="general_link_showAll">
                                "Ver todo %1${'$'}s"
                              </string>
                              <string name="general_button_goTop">
                                "Ir arriba"
                              </string>
                              <plurals name="general_quantity">
                                <item quantity="one">"Un elemento seleccionado"</item>
                                <item quantity="other">"%1${'$'}s elementos seleccionados"</item>
                              </plurals>
                            </resources>
                             """.formatXml()

        Assert.assertEquals(expectedResult, xmlPostProcessor.formatTranslationXml(inputXmlString))
    }

    @Test
    fun `Postprocessing XML with plurals and percentages works`() {
        // Test complete Xml
        val inputXmlString = """
                            <resources>
                              <plurals name="hello_friend">
                                <item quantity="one">"Hello my {{ friend_number }} friend"</item>
                                <item quantity="other">"Hello my {{ friend_number }} friends"</item>
                              </plurals>
                              <plurals name="hello_friend_love_100">
                                <item quantity="one">"Hello my {{ friend_number }} friend. I love you 100%"</item>
                                <item quantity="other">"Hello my {{ friend_number }} friends. I love you 100%"</item>
                              </plurals>
                              <plurals name="hello_love_100">
                                <item quantity="one">"Hello friend. I love you 100%"</item>
                                <item quantity="other">"Hello friends. I love you 100%"</item>
                              </plurals>
                            </resources>
                             """

        val expectedResult = """
                            <resources>
                              <plurals name="hello_friend">
                                <item quantity="one">"Hello my %1${'$'}s friend"</item>
                                <item quantity="other">"Hello my %1${'$'}s friends"</item>
                              </plurals>
                              <plurals name="hello_friend_love_100">
                                <item quantity="one">"Hello my %1${'$'}s friend. I love you 100%%"</item>
                                <item quantity="other">"Hello my %1${'$'}s friends. I love you 100%%"</item>
                              </plurals>
                              <plurals name="hello_love_100">
                                <item quantity="one">"Hello friend. I love you 100%"</item>
                                <item quantity="other">"Hello friends. I love you 100%"</item>
                              </plurals>
                            </resources>
                             """.formatXml()

        Assert.assertEquals(expectedResult, xmlPostProcessor.formatTranslationXml(inputXmlString))
    }

    @Test
    fun `Postprocessing XML with string HTML symbols works`() {
        // Test complete Xml
        val inputXmlString = """
                            <resources>
                              <string name="hello_friend_bold">
                                "Hello &lt;b&gt;{{name}}&lt;/b&gt;"
                              </string>
                            </resources>
                             """

        val expectedResult = """
                            <resources>
                              <string name="hello_friend_bold">
                                "Hello <b>%1${'$'}s</b>"
                              </string>
                            </resources>
                             """.formatXml()

        Assert.assertEquals(expectedResult, xmlPostProcessor.formatTranslationXml(inputXmlString))
    }

    @Test
    fun `Splitting tablet translation strings works`() {
        // Test complete Xml
        val expectedKey = "general_button_goTop"
        val inputXmlString = """
                            <resources>
                              <string name="$expectedKey">
                                "$expectedKey"
                              </string>
                              <string name="${expectedKey}_tablet">
                                "${expectedKey}_tablet"
                              </string>
                            </resources>
                             """

        val allRegexString = ALL_REGEX_STRING
        val tabletRegexString = TABLET_REGEX_STRING

        val splitTranslationXmlMap = xmlPostProcessor.splitTranslationXml(inputXmlString, listOf(tabletRegexString))

        // Check XML documents and see if the first string node has the proper name and the proper text with XPath
        val xpNamePath = "//resources/string[position()=1]/@name"
        val xpTextPath = "//resources/string[position()=1]/text()"

        Assert.assertEquals(
            expectedKey,
            xp.evaluate(xpNamePath, splitTranslationXmlMap.getValue(allRegexString)).trim())
        Assert.assertEquals(
            expectedKey,
            xp.evaluate(xpNamePath, splitTranslationXmlMap.getValue(tabletRegexString)).trim())

        Assert.assertEquals(
            "\"$expectedKey\"",
            xp.evaluate(xpTextPath, splitTranslationXmlMap.getValue(allRegexString)).trim())
        Assert.assertEquals(
            "\"${expectedKey}_tablet\"",
            xp.evaluate(xpTextPath, splitTranslationXmlMap.getValue(tabletRegexString)).trim())

    }

    @Test
    fun `Splitting tablet translation strings with plurals works`() {
        // Test complete Xml
        val expectedKey = "general_quantity"
        val inputXmlString = """
                            <resources>
                              <plurals name="$expectedKey">
                                <item quantity="one">"{{element_quantity}} elemento seleccionado"</item>
                                <item quantity="other">"{{element_quantity}} elementos seleccionados"</item>
                              </plurals>
                              <plurals name="${expectedKey}_tablet">
                                <item quantity="one">"{{element_quantity}} elemento seleccionado en tablet"</item>
                                <item quantity="other">"{{element_quantity}} elementos seleccionados en tablet"</item>
                              </plurals>
                            </resources>
                             """

        val allRegexString = ALL_REGEX_STRING
        val tabletRegexString = TABLET_REGEX_STRING

        val splitTranslationXmlMap = xmlPostProcessor.splitTranslationXml(inputXmlString, listOf(tabletRegexString))

        // Check XML documents and see if the first string node has the proper name with XPath
        val xpNamePath = "//resources/plurals[position()=1]/@name"

        Assert.assertEquals(
            expectedKey,
            xp.evaluate(xpNamePath, splitTranslationXmlMap.getValue(allRegexString)).trim())
        Assert.assertEquals(
            expectedKey,
            xp.evaluate(xpNamePath, splitTranslationXmlMap.getValue(tabletRegexString)).trim())
    }

    private fun String.formatXml(): String =
        DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(this.byteInputStream(Charsets.UTF_8))
            .toAndroidXmlString()
}