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

import com.hyperdevs.poeditor.gradle.utils.TABLET_REGEX_STRING
import com.hyperdevs.poeditor.gradle.utils.TABLET_RES_FOLDER_SUFFIX
import com.hyperdevs.poeditor.gradle.utils.createValuesModifierFromLangCode
import com.hyperdevs.poeditor.gradle.utils.logger
import java.io.File

/**
 * Object that converts XML data into Android XML files.
 */
object StringsXmlWriter {

    /**
     * Saves a given map of XML files related to a language that the project contains to the
     * project's strings folder.
     */
    @Suppress("LongParameterList")
    fun saveXml(
        resDirPath: String,
        resFileName: String,
        stringsXmlDocumentMap: Map<String, String>,
        defaultLang: String,
        languageCode: String,
        languageValuesOverridePathMap: Map<String, String>?
    ) {
        // First check if we have passed a default "values" folder for the given language
        var baseValuesDir: File? = languageValuesOverridePathMap?.get(languageCode)?.let { File(it) }

        // If we haven't passed a default base values directory, compose the base values folder
        if (baseValuesDir == null) {
            var valuesFolderName = "values"

            val valuesModifier = createValuesModifierFromLangCode(languageCode)
            if (valuesModifier != defaultLang) valuesFolderName = "$valuesFolderName-$valuesModifier"

            baseValuesDir = File(File(resDirPath), valuesFolderName)
        }

        val folderToXmlMap = stringsXmlDocumentMap.mapKeys { (regexString, _) ->
            // Compose values folder file for the given modifiers
            if (regexString == TABLET_REGEX_STRING) {
                File("${baseValuesDir.absolutePath}-$TABLET_RES_FOLDER_SUFFIX")
            } else {
                baseValuesDir
            }
        }

        folderToXmlMap.forEach { (valuesFolderFile, xmlString) ->
            saveXmlToFolder(valuesFolderFile, xmlString, resFileName)
        }
    }

    private fun saveXmlToFolder(
        stringsFolderFile: File,
        xmlString: String,
        resFileName: String
    ) {
        if (!stringsFolderFile.exists()) {
            logger.debug("Creating strings folder for new language")
            val folderCreated = stringsFolderFile.mkdirs()
            logger.debug("Folder created?: $folderCreated")
            check(folderCreated) { "Strings folder could not be created: ${stringsFolderFile.absolutePath}" }
        }

        logger.lifecycle("Saving strings to ${stringsFolderFile.absolutePath}")
        File(stringsFolderFile, "$resFileName.xml").writeText(xmlString)
    }
}
