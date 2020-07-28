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

group = "com.github.bq"

buildscript {
    repositories {
        mavenCentral()
        jcenter()
        google().content {
            includeGroup("com.android")
            includeGroupByRegex("com\\.android\\..*")
            includeGroupByRegex("com\\.google\\..*")
            includeGroupByRegex("androidx\\..*")
        }
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.3.72"))
    }
}

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    groovy
    maven
    id("io.gitlab.arturbosch.detekt").version("1.9.1")
}

repositories {
    mavenCentral()
    jcenter()
    google().content {
        includeGroup("com.android")
        includeGroupByRegex("com\\.android\\..*")
        includeGroupByRegex("com\\.google\\..*")
        includeGroupByRegex("androidx\\..*")
    }
}

dependencies {
    implementation(localGroovy())

    compileOnly("com.android.tools.build:gradle:4.2.0-alpha01")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.72")

    implementation("com.squareup.moshi:moshi:1.9.2")
    implementation("com.squareup.moshi:moshi-kotlin:1.9.2")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    implementation("com.squareup.okhttp3:logging-interceptor:4.7.2")
    implementation("com.squareup.okhttp3:okhttp:4.7.2")

    testImplementation(gradleTestKit())
    testImplementation(kotlin("test"))
    testImplementation("junit:junit:4.13")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.9.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

detekt {
    toolVersion = "1.9.1"
    config = files("${project.rootDir}/config/detekt.yml")
}

tasks {
    test {
        useJUnit()
        maxHeapSize = "1G"
    }

    withType<io.gitlab.arturbosch.detekt.Detekt> {
        // Target version of the generated JVM bytecode. It is used for type resolution.
        this.jvmTarget = "1.8"
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

    val sourcesJar by creating(Jar::class) {
        dependsOn(JavaPlugin.CLASSES_TASK_NAME)
        classifier = "sources"
        from(sourceSets["main"].allSource)
    }

    val javadocJar by creating(Jar::class) {
        val javadoc by tasks
        from(javadoc)
        classifier = "javadoc"
    }

    artifacts {
        add("archives", sourcesJar)
        add("archives", javadocJar)
    }
}