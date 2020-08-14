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

package com.bq.poeditor.gradle

import com.bq.poeditor.gradle.tasks.ImportPoEditorStringsTask
import org.gradle.api.Project
import org.gradle.api.internal.tasks.DefaultTaskContainer
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class ImportPoEditorStringsTaskTest {
    private val project: Project
        get() = ProjectBuilder.builder().build()

    private val task: ImportPoEditorStringsTask
        get() {
            return project.tasks.create("importPoEditorStrings", ImportPoEditorStringsTask::class.java)
        }

    /**
     * Extension tests
     */

    /**
    @Test(expected = DefaultTaskContainer.TaskCreationException::class)
    fun testExecutedWithoutNeededExtensionThrowsException() {
        // No extension is set

        // Test this throws
        task.importPoEditorStrings()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testExecutedWithEmptyExtensionThrowsException() {
        // Set empty extension
        val emptyExtension = PoEditorPluginExtension()
        project.extensions.add("poEditorPlugin", emptyExtension)

        // Test this throws IllegalArgumentException
        task.importPoEditorStrings()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testExecutedWithInvalidApiCredentialsThrowsException() {
        // Set empty extension
        val invalidApiCredentialsExtension = PoEditorPluginExtension()
        invalidApiCredentialsExtension.projectId = "invalid_project_id"
        invalidApiCredentialsExtension.apiToken = "invalid_api_token"
        invalidApiCredentialsExtension.defaultLang = "fake_lang"
        invalidApiCredentialsExtension.resDirPath = "fake_path"

        project.extensions.add("poEditorPlugin", invalidApiCredentialsExtension)

        // Test this throws IllegalArgumentException
        task.importPoEditorStrings()
    }

     */
}