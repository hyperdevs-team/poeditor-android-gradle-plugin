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

package com.bq.poeditor.gradle.tasks

import com.bq.poeditor.gradle.utils.POEDITOR_CONFIG_NAME
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * Dummy of the main PoEditor task that will print configuration steps for the missing configuration.
 */
abstract class DummyImportPoEditorStringsTask
@Inject constructor(private val configName: String) : DefaultTask() {

    /**
     * Main task entrypoint.
     */
    @TaskAction
    fun printConfigurationMessage() {
        logger.warn("You haven't set-up '$configName' in your '$POEDITOR_CONFIG_NAME' block.\n" +
                    "Please set it up in order for this to work")
    }
}