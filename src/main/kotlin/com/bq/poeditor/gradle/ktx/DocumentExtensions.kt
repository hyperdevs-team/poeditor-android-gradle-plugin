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

package com.bq.poeditor.gradle.ktx

import org.w3c.dom.Document
import org.w3c.dom.bootstrap.DOMImplementationRegistry
import org.w3c.dom.ls.DOMImplementationLS
import java.io.StringWriter
import java.lang.Boolean
import javax.xml.parsers.DocumentBuilderFactory

private val DEFAULT_ENCODING = Charsets.UTF_8

/**
 * Converts an XML string to a proper [Document].
 */
fun String.toDocument(): Document =
    DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .parse(this.byteInputStream(DEFAULT_ENCODING))

/**
 * Convers a [Document] into a formatted [String].
 */
fun Document.dumpToString(): String {
    val registry = DOMImplementationRegistry.newInstance()
    val impl = registry.getDOMImplementation("LS") as DOMImplementationLS
    val output = impl.createLSOutput().apply { encoding = "UTF-8" }
    val serializer = impl.createLSSerializer()

    val writer = StringWriter()
    output.characterStream = writer

    serializer.domConfig.setParameter("format-pretty-print",
        Boolean.TRUE)
    serializer.domConfig.setParameter("xml-declaration", true)

    serializer.write(this, output)

    return writer.toString()
}