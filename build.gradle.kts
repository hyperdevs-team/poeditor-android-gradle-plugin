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
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
    repositories {
        mavenCentral()
        google()
    }

    dependencies {
        classpath(libs.kotlin.gradle)
    }
}

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    groovy
    `maven-publish`
    alias(libs.plugins.detekt)
    alias(libs.plugins.gitVersionGradle)
    alias(libs.plugins.versionsUpdate)
}

apply(plugin = libs.plugins.versionsUpdate.get().pluginId)

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(localGroovy())

    compileOnly(libs.android.buildTools)

    implementation(libs.kotlin.stdlib)
    implementation(libs.bundles.moshi)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.okhttp3)
    implementation(libs.dotenvKotlin)

    testImplementation(gradleTestKit())
    testImplementation(kotlin("test"))
    testImplementation(libs.junit)

    detektPlugins(libs.detekt.formatting)
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.java.sdk.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.java.sdk.get())

    withJavadocJar()
    withSourcesJar()
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config.setFrom(files("${project.rootDir}/config/detekt.yml"))
    autoCorrect = true
}

androidGitVersion {
    codeFormat = "MMNNPP"
    format = "%tag%"
}

tasks {
    test {
        useJUnit()
        maxHeapSize = "1G"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = libs.versions.java.sdk.get()
        }
    }

    val installGitHooks by registering(Copy::class) {
        val baseHooksDir = File(File(rootProject.rootDir, "scripts"), "hooks")
        from(File(baseHooksDir, "pre-commit"))
        into(File(rootProject.rootDir, ".git/hooks"))
        fileMode = 0b000_111_101_101 // 0755 in binary, it doesn't seem to work if I put 755 or 0755
    }

    // Install hooks automatically before building a new compilation
    // Idea from: https://gist.github.com/KenVanHoeylandt/c7a928426bce83ffab400ab1fd99054a
    getByPath("compileKotlin").dependsOn(installGitHooks)
}

group = "com.github.hyperdevs-team"
version = androidGitVersion.name()

publishing {
    publications {
        // Edit the `pluginMaven` publication, which is the name for the default publication task of the `java-gradle-plugin`
        register<MavenPublication>("pluginMaven") {
            groupId = "com.github.hyperdevs-team"
            artifactId = "poeditor-android-gradle-plugin"

            pom {
                name.set("PoEditor Android Gradle Plug-in")
                description.set("Gradle plug-in that enables importing PoEditor localized strings directly to an Android project")
                url.set("https://github.com/hyperdevs-team/poeditor-android-gradle-plugin")
                version = androidGitVersion.name()
                inceptionYear.set("2016")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                organization {
                    name.set("HyperDevs")
                    url.set("https://github.com/hyperdevs-team")
                }

                issueManagement {
                    system.set("GitHub Issues")
                    url.set("https://github.com/hyperdevs-team/poeditor-android-gradle-plugin/issues")
                }

                scm {
                    connection.set("git@github.com:hyperdevs-team/poeditor-android-gradle-plugin.git")
                    url.set("https://github.com/hyperdevs-team/poeditor-android-gradle-plugin.git")
                }

                developers {
                    developer {
                        name.set("Iván Martínez")
                        id.set("imartinez")
                        url.set("https://github.com/imartinez")
                        roles.set(listOf("Initial work"))
                    }

                    developer {
                        name.set("Adrián García")
                        id.set("adriangl")
                        url.set("https://github.com/adriangl")
                        roles.set(listOf("Maintainer"))
                    }
                }
            }
        }
    }
}
// Get only stable versions when running dependencyUpdates
tasks.withType<DependencyUpdatesTask> {
    fun isNonStable(version: String): Boolean {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        return isStable.not()
    }

    rejectVersionIf {
        isNonStable(this.candidate.version) && !isNonStable(this.currentVersion)
    }
}
