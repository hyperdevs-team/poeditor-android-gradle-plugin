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

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.bq.poeditor.gradle.ktx.registerNewTask
import com.bq.poeditor.gradle.tasks.DummyImportPoEditorStringsTask
import com.bq.poeditor.gradle.tasks.ImportPoEditorStringsTask
import com.bq.poeditor.gradle.utils.*
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.*

const val TAG = "[PoEditorPlugin]"
typealias ConfigName = String

/**
 * Main plugin class that registers the tasks used by the plugin.
 */
class PoEditorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val mainResourceDirectory = getResourceDirectory(project, "main")

        // Add the 'poEditorPlugin' extension object in the project,
        // used to pass parameters to the main PoEditor task
        val mainPoEditorExtension: PoEditorPluginExtension = project.extensions
            .create<PoEditorPluginExtension>(DEFAULT_PLUGIN_NAME).apply {
                defaultLang.convention("en")
                defaultResPath.convention(mainResourceDirectory.asFile.absolutePath)
            }

        // Create the main PoEditor task, and add it to the project
        project.registerNewTask<ImportPoEditorStringsTask>(
            getPoEditorTaskName(),
            getMainPoEditorDescription(),
            PLUGIN_GROUP,
            arrayOf(mainPoEditorExtension, mainResourceDirectory))

        // Add flavor and build-type configurations if the project has the "com.android.application" plugin
        project.plugins.withType<AppPlugin> {
            applyInternalApp(project, mainPoEditorExtension)
        }

        // Add flavor and build-type configurations if the project has the "com.android.library" plugin
        project.plugins.withType<LibraryPlugin> {
            applyInternalLib(project, mainPoEditorExtension)
        }

        project.afterEvaluate {
            val projectHasApplicationPlugin = project.plugins.findPlugin(AppPlugin::class.java) != null
            val projectHasLibraryPlugin = project.plugins.findPlugin(LibraryPlugin::class.java) != null

            val projectHasAnyAndroidPlugin = projectHasApplicationPlugin || projectHasLibraryPlugin

            if (!projectHasAnyAndroidPlugin) {
                throw IllegalStateException(
                    "The Android Application Gradle plug-in or the Android Library Gradle plug-in was not applied. " +
                    "PoEditor plug-in cannot be configured.")
            }
        }
    }

    private fun applyInternalApp(project: Project,
                                 mainExtension: PoEditorPluginExtension) {
        // Add android.poEditorConfig extension container so we can set-up flavors and build types
        // configurations with Android app modules.
        val configsExtensionContainer = project.container<PoEditorPluginExtension>()
        val androidExtension = project.the<BaseAppModuleExtension>()
        (androidExtension as ExtensionAware).extensions.add(POEDITOR_CONFIG_NAME, configsExtensionContainer)

        val configPoEditorTaskProvidersMap: MutableMap<ConfigName, TaskProvider<*>> = mutableMapOf()

        // Add tasks for every build variant
        androidExtension.onVariants {
            val configs = getConfigs(this.productFlavors.map { it.second }, this.buildType)

            generatePoEditorTasks(configs,
                project,
                configsExtensionContainer,
                mainExtension,
                configPoEditorTaskProvidersMap)
        }

        project.afterEvaluate {
            val allPossibleConfigNames: Set<String> by lazy {
                androidExtension.applicationVariants.flatMapTo(mutableSetOf()) {
                    listOf(it.buildType.name) + it.productFlavors.map { it.name }
                }
            }

            verifyAndLinkTasks(configsExtensionContainer,
                allPossibleConfigNames,
                configPoEditorTaskProvidersMap,
                project)
        }
    }

    private fun applyInternalLib(project: Project,
                                 mainExtension: PoEditorPluginExtension) {
        // Add android.poEditorConfig extension container so we can set-up flavors and build types
        // configurations with Android library modules.
        val configsExtensionContainer = project.container<PoEditorPluginExtension>()
        val androidExtension = project.the<LibraryExtension>()
        (androidExtension as ExtensionAware).extensions.add(POEDITOR_CONFIG_NAME, configsExtensionContainer)

        val configPoEditorTaskProvidersMap: MutableMap<ConfigName, TaskProvider<*>> = mutableMapOf()

        // Add tasks for every build variant
        androidExtension.onVariants {
            val configs = getConfigs(this.productFlavors.map { it.second }, this.buildType)

            generatePoEditorTasks(configs,
                project,
                configsExtensionContainer,
                mainExtension,
                configPoEditorTaskProvidersMap)
        }

        project.afterEvaluate {
            val allPossibleConfigNames: Set<String> by lazy {
                androidExtension.libraryVariants.flatMapTo(mutableSetOf()) {
                    listOf(it.buildType.name) + it.productFlavors.map { it.name }
                }
            }

            verifyAndLinkTasks(configsExtensionContainer,
                allPossibleConfigNames,
                configPoEditorTaskProvidersMap,
                project)
        }
    }

    private fun getConfigs(productFlavors: List<String>,
                           buildType: String?): Set<String> = (productFlavors + buildType).filterNotNull().toSet()

    private fun generatePoEditorTasks(configs: Set<String>,
                                      project: Project,
                                      configsExtensionContainer: NamedDomainObjectContainer<PoEditorPluginExtension>,
                                      mainExtension: PoEditorPluginExtension,
                                      configPoEditorTaskProvidersMap: MutableMap<ConfigName, TaskProvider<*>>) {
        configs.forEach { configName ->
            val configTaskName = getPoEditorTaskName(configName)

            // Only create the task if no other task is registered with the same name (would mean it's already
            // created.
            project.tasks.findByName(configTaskName) ?: run {
                val configResourceDirectory = getResourceDirectory(project, configName)
                val configExtension = buildExtensionForConfig(
                    configName,
                    configsExtensionContainer,
                    mainExtension)

                val newConfigPoEditorTask = if (configExtension != null) {
                    project.registerNewTask<ImportPoEditorStringsTask>(
                        getPoEditorTaskName(configName),
                        getMainPoEditorDescription(),
                        PLUGIN_GROUP,
                        arrayOf(configExtension, configResourceDirectory))
                } else {
                    project.registerNewTask<DummyImportPoEditorStringsTask>(
                        getPoEditorTaskName(configName),
                        getMainPoEditorDescription(),
                        PLUGIN_GROUP,
                        arrayOf(configName))
                }

                configPoEditorTaskProvidersMap.put(configName, newConfigPoEditorTask)
            }
        }
    }

    private fun verifyAndLinkTasks(configsExtensionContainer: NamedDomainObjectContainer<PoEditorPluginExtension>,
                                   allPossibleConfigNames: Set<String>,
                                   configPoEditorTaskProvidersMap: MutableMap<ConfigName, TaskProvider<*>>,
                                   project: Project) {
        for (configName in configsExtensionContainer.names) {
            if (configName !in allPossibleConfigNames) {
                logger.warn("$TAG: " +
                            "'$POEDITOR_CONFIG_NAME' object '$configName' " +
                            "does not match a flavor or build type.")
            }
        }

        logger.debug("$TAG: Configurations to add: $configPoEditorTaskProvidersMap")

        // Link all other tasks to main task so they also get executed when executing the main one
        configPoEditorTaskProvidersMap.values.forEach { task ->
            project.tasks.named(getPoEditorTaskName()) { dependsOn(task) }
        }
    }

    private fun getPoEditorTaskName(configName: ConfigName = "") = "import${configName.capitalize()}PoEditorStrings"

    private fun getResourceDirectory(project: Project, configName: ConfigName) =
        project.layout.projectDirectory.dir("src/$configName/res")

    private fun getMainPoEditorDescription(): String = """
        Imports all PoEditor strings for all flavors and build types configurations.
    """.trimIndent()

    private fun getPoEditorDescriptionForConfig(configName: ConfigName): String = """
        Imports all PoEditor strings for configuration $configName.
    """.trimIndent()
}