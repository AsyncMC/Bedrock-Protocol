/*
 *     AsyncMC - A fully async, non blocking, thread safe and open source Minecraft server implementation
 *     Copyright (C) 2020 joserobjr@gamemods.com.br
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
    jacoco
}

java {
    sourceCompatibility = JavaVersion.VERSION_13
    targetCompatibility = JavaVersion.VERSION_13
}

val moduleName = "com.github.asyncmc.protocol.bedrock"

repositories {
    jcenter()
    maven(url = "https://repo.gamemods.com.br/public/")
}

tasks.withType<JavaCompile>().configureEach {
    options.javaModuleVersion.set(provider { project.version as String })

    // this is needed because we have a separate compile step in this example with the 'module-info.java' is in 'main/java' and the Kotlin code is in 'main/kotlin'
    //options.compilerArgs = listOf("--patch-module", "$moduleName=${sourceSets.main.get().output.asPath}")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "13"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn(":compileKotlin")
    inputs.property("moduleName", moduleName)
    doFirst {
        options.compilerArgs = options.compilerArgs + listOf(
            // include Gradle dependencies as modules
            "--module-path", sourceSets["main"].compileClasspath.asPath,
            "--patch-module", "$moduleName=${sourceSets.main.get().output.asPath}"
        )
        sourceSets["main"].compileClasspath = files()
    }
}

sourceSets.main.configure {
    //java.setSrcDirs(listOf("src/main/kotlin"))
}

plugins.withType<JavaPlugin>().configureEach {
    configure<JavaPluginExtension> {
        modularity.inferModulePath.set(true)
    }
}

val ktorVersion = findProperty("ktor.version")

dependencies {
    api(kotlin("stdlib-jdk8", embeddedKotlinVersion))
    api(kotlin("reflect", embeddedKotlinVersion))

    implementation("org.jctools:jctools-core:3.0.0")
    implementation("io.ktor:ktor-network:$ktorVersion")
    implementation("com.github.asyncmc:raknet-protocol:0.1.0-SNAPSHOT")

    testImplementation(kotlin("test-junit5", embeddedKotlinVersion))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0-M1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0-M1")

    testImplementation("org.mockito:mockito-junit-jupiter:3.3.3")
    testImplementation("org.mockito:mockito-inline:3.3.3")
    testImplementation("com.nhaarman:mockito-kotlin:1.6.0")

    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("com.natpryce:hamkrest:1.7.0.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
    testLogging {
        events("PASSED", "FAILED", "SKIPPED", "STANDARD_OUT", "STANDARD_ERROR")
    }
}

jacoco {
    //toolVersion = jacocoVersion
    reportsDir = file("$buildDir/reports/jacoco")
}

tasks {
    named<JacocoReport>("jacocoTestReport") {
        dependsOn("test")
        classDirectories.setFrom(files("${buildDir}/classes"))
        reports {
            xml.isEnabled = true
            html.isEnabled = true
        }
    }
}
