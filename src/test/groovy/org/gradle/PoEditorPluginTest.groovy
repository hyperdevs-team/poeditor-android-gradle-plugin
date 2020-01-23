package org.gradle

import com.masmovil.gradle.ImportPoEditorStringsTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * PoEditorPluginTest test.
 *
 * Created by imartinez on 11/1/16.
 */
class PoEditorPluginTest {
    @Test
    public void poeditorPluginAddsImportPoEditorStringsTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'com.masmovil.poeditor'

        assertTrue(project.tasks.importPoEditorStrings instanceof ImportPoEditorStringsTask)
    }
}
