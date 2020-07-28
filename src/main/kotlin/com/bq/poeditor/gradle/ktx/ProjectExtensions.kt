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

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register

/**
 * Helper function to register a new task in a [Project].
 */
internal fun Project.registerNewTask(name: String,
                                     description: String? = null,
                                     group: String? = null,
                                     block: Task.() -> Unit = {}) =
    registerNewTask(name, description, group, emptyArray(), block)

/**
 * Helper function to register a new task in a [Project].
 *
 * Allows passing [Task] constructor parameters.
 */
@Suppress("SpreadOperator")
internal inline fun <reified T : Task> Project.registerNewTask(name: String,
                                                               description: String? = null,
                                                               group: String? = null,
                                                               constructorArgs: Array<Any> = emptyArray(),
                                                               noinline block: T.() -> Unit = {}): TaskProvider<T> {
    val config: T.() -> Unit = {
        this.description = description
        this.group = group.takeUnless { description.isNullOrBlank() }
        block()
    }

    return tasks.register<T>(name, *constructorArgs).apply { configure(config) }
}