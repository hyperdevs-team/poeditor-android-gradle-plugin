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

package com.hyperdevs.poeditor.gradle.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class LocaleUtilsTest {
    @Test
    fun `Creating values modifier from standard lang code works`() {
        assertEquals("es", createValuesModifierFromLangCode("es"))
    }

    @Test
    fun `Creating values modifier from specialized lang code works`() {
        assertEquals("es-rMX", createValuesModifierFromLangCode("es-mx"))
    }

    @Test
    fun `Creating values modifier from Chinese lang codes work`() {
        assertEquals("zh", createValuesModifierFromLangCode("zh-CN"))
        assertEquals("zh-rHK", createValuesModifierFromLangCode("zh-hk"))
        assertEquals("zh-rMO", createValuesModifierFromLangCode("zh-mo"))
        assertEquals("zh-rSG", createValuesModifierFromLangCode("zh-sg"))
        assertEquals("b+zh+Hans", createValuesModifierFromLangCode("zh-Hans"))
        assertEquals("b+zh+Hant", createValuesModifierFromLangCode("zh-Hant"))
    }

    @Test
    fun `Creating values modifier from old lang codes works`() {
        assertEquals("in", createValuesModifierFromLangCode("id"))
        assertEquals("iw", createValuesModifierFromLangCode("he"))
        assertEquals("iw-rIL", createValuesModifierFromLangCode("he-IL"))
        assertEquals("ji", createValuesModifierFromLangCode("yi"))
    }
}
