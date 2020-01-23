package com.masmovil.gradle

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Task that:
 * 1. downloads all strings files (every available lang) from PoEditor given a api_token and project_id.
 * 2. extract "tablet" strings to another own XML (strings with the suffix "_tablet")
 * 3. creates and saves two strings.xml files to values-<lang> and values-<lang>-sw600dp (tablet specific strings)
 *
 * Created by imartinez on 11/1/16.
 */
class ImportPoEditorStringsTask extends DefaultTask {

    @TaskAction
    def importPoEditorStrings() {

        def String POEDITOR_API_URL = 'https://poeditor.com/api/'

        // Check if needed extension and parameters are set
        def apiToken = ""
        def projectId = ""
        def defaultLang = ""
        def resDirPath = ""

        try {
            apiToken = project.extensions.poEditorPlugin.api_token
            projectId = project.extensions.poEditorPlugin.project_id
            defaultLang = project.extensions.poEditorPlugin.default_lang
            resDirPath = project.extensions.poEditorPlugin.res_dir_path

            if (apiToken.length() == 0)
                throw new Exception('Invalid params: api_token is ""');
            if (projectId.length() == 0)
                throw new Exception('Invalid params: project_id is ""');
            if (defaultLang.length() == 0)
                throw new Exception('Invalid params: default_lang is ""');
            if (resDirPath.length() == 0)
                throw new Exception('Invalid params: res_dir_path is ""');

        } catch (Exception e) {
            throw new IllegalStateException(
                    "You shoud define in your build.gradle: \n\n" +
                    "poEditorPlugin.api_token = <your_api_token>\n" +
                    "poEditorPlugin.project_id = <your_project_id>\n" +
                    "poEditorPlugin.default_lang = <your_default_lang> \n" +
                    "poEditorPlugin.res_dir_path = <your_res_dir_path> \n\n "
                    + e.getMessage()
            )
            return
        }

        // Retrieve available languages from PoEditor
        def jsonSlurper = new JsonSlurper()
        def langs = ['curl', '-X', 'POST', '-d', "api_token=${apiToken}", '-d', 'action=list_languages', '-d', "id=${projectId}", POEDITOR_API_URL].execute()
        def langsJson = jsonSlurper.parseText(langs.text)

        // Check if the response was 200
        if (langsJson.response.code != "200") {
            throw new IllegalStateException(
                    "An error occurred while trying to export from PoEditor API: \n\n" +
                            langsJson.toString()
            )
            return
        }

        // Iterate over every available language
        langsJson.list.code.each {
            // Retrieve translation file URL for the given language
            println "Retrieving translation file URL for language code: ${it}"
            // TODO curl may not be installed in the host SO. Add a safe check and, if curl is not available, stop the process and print an error message
            def translationFileInfo = ['curl', '-X', 'POST', '-d', "api_token=${apiToken}", '-d', 'action=export', '-d', "id=${projectId}", '-d', 'type=android_strings', '-d', "language=${it}", 'https://poeditor.com/api/'].execute()
            def translationFileInfoJson = jsonSlurper.parseText(translationFileInfo.text)
            def translationFileUrl = translationFileInfoJson.item
            // Download translation File in "Android Strings" XML format
            println "Downloading file from Url: ${translationFileUrl}"
            def translationFile = ['curl', '-X', 'GET', translationFileUrl].execute()

            // Post process the downloaded XML:
            def translationFileText = postProcessIncomingXMLString(translationFile.text)

            // Extract tablet strings to a separate strings XML
            def translationFileRecords = new XmlParser().parseText(translationFileText)
            def tabletNodes = translationFileRecords.children().findAll {
                it.@name.endsWith('_tablet')
            }
            String tabletXmlString = """
                    <resources>
                     <!-- Tablet strings -->
                    </resources>"""
            def tabletRecords = new XmlParser().parseText(tabletXmlString)
            tabletNodes.each {
                translationFileRecords.remove(it)
                it.@name = it.@name.replace("_tablet", "")
                tabletRecords.append(it)
            }

            // Build final strings XMLs ready to be written to files
            StringWriter sw = new StringWriter()
            XmlNodePrinter np = new XmlNodePrinter(new PrintWriter(sw))
            np.print(translationFileRecords)
            def curatedStringsXmlText = sw.toString()
            StringWriter tabletSw = new StringWriter()
            XmlNodePrinter tabletNp = new XmlNodePrinter(new PrintWriter(tabletSw))
            tabletNp.print(tabletRecords)
            def curatedTabletStringsXmlText = tabletSw.toString()

            // If language folders doesn't exist, create it (both for smartphones and tablets)
            // TODO investigate if we can infer the res folder path instead of passing it using poEditorPlugin.res_dir_path

            def valuesModifier = createValuesModifierFromLangCode(it)
            def valuesFolder = valuesModifier != defaultLang ? "values-${valuesModifier}" : "values"
            File stringsFolder = new File("${resDirPath}/${valuesFolder}")
            if (!stringsFolder.exists()) {
                println 'Creating strings folder for new language'
                def folderCreated = stringsFolder.mkdir()
                println "Folder created: ${folderCreated}"
            }

            // TODO delete existing strings.xml files

            // Write downloaded and post-processed XML to files
            println "Writing strings.xml file"
            new File(stringsFolder, 'strings.xml').withWriter { w ->
                w << curatedStringsXmlText
            }
            println "Writing tablet strings.xml file"
            new File(tabletStringsFolder, 'strings.xml').withWriter { w ->
                w << curatedTabletStringsXmlText
            }
        }
    }

    /**
     * Creates values file modifier taking into account specializations (i.e values-es-rMX for Mexican)
     * @param langCode
     * @return proper values file modifier (i.e. es-rMX)
     */
    String createValuesModifierFromLangCode(String langCode) {
        if (!langCode.contains("-")) {
            return langCode
        } else {
            String[] langParts = langCode.split("-")
            return langParts[0] + "-" + "r" + langParts[1].toUpperCase()
        }
    }

    String postProcessIncomingXMLString(String incomingXMLString) {
        // Post process the downloaded XML
        return incomingXMLString
                // Replace % with %%
                .replace("%", "%%")
                // Replace &lt; with < and &gt; with >
                .replace("&lt;", "<").replace("&gt;", ">")
                // Replace placeholders from {{bookTitle}} to %1$s format.
                // First of all, manage each strings separately
                .split('</string>').collect { s ->
                    // Second, replace each placeholder taking into account ther order set as part of the placeholder
                    def placeHolderInStringCounter = 1
                    s.replaceAll("\\{\\d?\\{(.*?)\\}\\}") { it ->
                        // If the placeholder contains an ordinal, use it: {2{pages_count}} -> %2%s
                        def match = it[0].toString()
                        if (Character.isDigit(match.charAt(1))) {
                            '%' + match.charAt(1) + '$s'
                        } else { // If not, use '1' as the ordinal: {{pages_count}} -> %1%s
                            '%1$s'
                        }
                    }
                }.join('</string>')
    }

}
