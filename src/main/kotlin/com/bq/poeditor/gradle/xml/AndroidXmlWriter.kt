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

import com.bq.poeditor.gradle.utils.TABLET_REGEX_STRING
import com.bq.poeditor.gradle.utils.TABLET_RES_FOLDER_SUFFIX
import com.bq.poeditor.gradle.utils.logger
import com.bq.poeditor.gradle.utils.createValuesModifierFromLangCode
import org.w3c.dom.Document
import org.w3c.dom.bootstrap.DOMImplementationRegistry
import org.w3c.dom.ls.DOMImplementationLS
import java.io.File
import java.io.StringWriter
import java.lang.IllegalStateException

/**
 * Class that converts XML data into Android XML files.
 */
class AndroidXmlWriter {

    /**
     * Saves a given map of XML files related to a language that the project contains to the
     * project's strings folder.
     */
    fun saveXml(resDirPath: String,
                postProcessedXmlDocumentMap: Map<String, Document>,
                defaultLang: String,
                languageCode: String) {
        val valuesModifier = createValuesModifierFromLangCode(languageCode)
        // TODO investigate if we can infer the res folder path instead of passing it using poEditorPlugin.res_dir_path
        val resDirFile = File(resDirPath)

        val folderToXmlMap = postProcessedXmlDocumentMap.mapKeys { (regexString, _) ->
            // Compose values folder for the given files
            StringBuilder().apply {
                append("values")
                if (valuesModifier != defaultLang) append("-$valuesModifier")
                if (regexString == TABLET_REGEX_STRING) append("-$TABLET_RES_FOLDER_SUFFIX")
            }.toString()
        }

        folderToXmlMap.forEach { (valuesFolderPath, document) ->
            saveXmlToFolder(resDirFile, valuesFolderPath, document)
        }
    }

    private fun saveXmlToFolder(resDirFile: File, valuesFolderPath: String, document: Document) {
        val stringsFolderFile = File(resDirFile, valuesFolderPath)
        if (!stringsFolderFile.exists()) {
            logger.debug("Creating strings folder for new language")
            val folderCreated = stringsFolderFile.mkdirs()
            logger.debug("Folder created?: $folderCreated")
            if (!folderCreated) {
                throw IllegalStateException(
                    "Strings folder could not be created: ${stringsFolderFile.absolutePath}")
            }
        }

        val registry = DOMImplementationRegistry.newInstance()
        val impl = registry.getDOMImplementation("LS") as DOMImplementationLS
        val output = impl.createLSOutput().apply { encoding = "UTF-8" }
        val serializer = impl.createLSSerializer()

        val writer = StringWriter()
        output.characterStream = writer

        serializer.domConfig.setParameter("format-pretty-print",
            java.lang.Boolean.TRUE)
        serializer.domConfig.setParameter("xml-declaration", true)

        serializer.write(document, output)
        File(stringsFolderFile, "strings.xml").writeText(writer.toString())
    }
}