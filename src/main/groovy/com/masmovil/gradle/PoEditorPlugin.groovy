package com.masmovil.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * PoEditor gradle plugin.
 *
 * Created by imartinez on 11/1/16.
 */
class PoEditorPlugin implements Plugin<Project> {

    void apply(Project project) {

        // Add the 'poEditorPlugin' extension object, used to pass parameters to the task
        project.extensions.create("poEditorPlugin", PoEditorPluginExtension)

        // Registers the task
        project.task('importPoEditorStrings', type: ImportPoEditorStringsTask)
    }

}