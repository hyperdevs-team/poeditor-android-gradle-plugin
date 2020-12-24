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

package com.bq.poeditor.gradle.utils

import com.bq.poeditor.gradle.PoEditorPluginExtension
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertThrows
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class MergeExtensionsTest {
    private val project = ProjectBuilder.builder().build()

    @Test
    fun `Merging empty list of extensions fails`() {
        val exts = emptyList<PoEditorPluginExtension>().mapToExtensionMergeHolder(project)

        assertThrows(IllegalArgumentException::class.java) { mergeExtensions(exts) }
    }

    @Test
    fun `Merging single extension returns self`() {
        val p0 = Extension().apply { apiToken.set("test") }
        val exts = listOf(p0).mapToExtensionMergeHolder(project)

        val merged = mergeExtensions(exts)

        assertSame(p0, merged)
    }

    @Test
    fun `Merging multiple extensions returns superset`() {
        val testApiToken = "test"
        val testProjectId = 1234

        val p0 = Extension().apply { apiToken.set(testApiToken) }
        val p1 = Extension().apply { projectId.set(testProjectId) }
        val exts = listOf(p0, p1).mapToExtensionMergeHolder(project)

        val merged = mergeExtensions(exts)

        assertEquals(testApiToken, merged.apiToken.get())
        assertEquals(testProjectId, merged.projectId.get())
    }

    @Test
    fun `Merging multiple extensions with overlapping properties returns prioritized superset`() {
        val testApiToken = "test"
        val testProjectId0 = 1234
        val testProjectId1 = 2345
        val testDefaultLang = "es"

        val p0 = Extension().apply {
            apiToken.set(testApiToken)
            projectId.set(testProjectId0)
        }
        val p1 = Extension().apply {
            projectId.set(testProjectId1)
            defaultLang.set(testDefaultLang)
        }
        val exts = listOf(p0, p1).mapToExtensionMergeHolder(project)

        val merged = mergeExtensions(exts)

        assertEquals(testApiToken, merged.apiToken.get())
        assertEquals(testProjectId0, merged.projectId.get())
        assertEquals(testDefaultLang, merged.defaultLang.get())
    }

    @Test
    fun `Updating parent extension propagates changes`() {
        val testApiToken = "test"
        val testProjectId = 1234
        val testDefaultLang = "es"

        val p0 = Extension().apply { apiToken.set(testApiToken) }
        val p1 = Extension().apply { projectId.set(testProjectId) }
        val p2 = Extension().apply { defaultLang.set(testDefaultLang) }
        val exts = listOf(p0, p1, p2).mapToExtensionMergeHolder(project)

        val merged = mergeExtensions(exts)

        val modifiedDefaultLang = "en"
        p2.defaultLang.set(modifiedDefaultLang)

        assertEquals(p2.defaultLang.get(), merged.defaultLang.get())
    }

    @Test
    fun `Updating parent extension doesn't overwrite existing properties`() {
        val testApiToken0 = "test0"
        val testApiToken1 = "test1"

        val p0 = Extension().apply { apiToken.set(testApiToken0) }
        val p1 = Extension().apply { apiToken.set(testApiToken1) }
        val exts = listOf(p0, p1).mapToExtensionMergeHolder(project)

        mergeExtensions(exts)

        val modifiedTestApiToken1 = "test1_mod"
        p1.apiToken.set(modifiedTestApiToken1)

        assertEquals(modifiedTestApiToken1, p1.apiToken.get())
        assertEquals(testApiToken0, p0.apiToken.get())
    }

    private inner class Extension : PoEditorPluginExtension(project.objects, "test")
}
