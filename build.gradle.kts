import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.31"
    jacoco
}

java {
    sourceCompatibility = JavaVersion.VERSION_15
    targetCompatibility = JavaVersion.VERSION_15
}

kotlin {
    explicitApi()
}

val moduleName = "com.github.asyncmc.protocol.bedrock"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://repo.gamemods.com.br/public/")
}

tasks.withType<JavaCompile>().configureEach {
    options.javaModuleVersion.set(provider { project.version as String })

    // this is needed because we have a separate compile step in this example with the 'module-info.java' is in 'main/java' and the Kotlin code is in 'main/kotlin'
    //options.compilerArgs = listOf("--patch-module", "$moduleName=${sourceSets.main.get().output.asPath}")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "15"
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
    api(kotlin("stdlib-jdk8"))
    api(kotlin("reflect"))

    implementation("com.github.asyncmc:raknet-interface:0.1.0-SNAPSHOT")
    implementation("com.github.asyncmc:module-api:0.1.0-SNAPSHOT")

    implementation("org.jctools:jctools-core:3.0.0")
    implementation("io.ktor:ktor-network:$ktorVersion")

    testImplementation(kotlin("test-junit5"))

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

sourceSets {
    main {
        java {
            outputDir = buildDir.resolve("classes/kotlin/main")
        }
    }
    test {
        java {
            outputDir = buildDir.resolve("classes/kotlin/test")
        }
    }
}

jacoco {
    //toolVersion = jacocoVersion
    reportsDirectory.set(file("$buildDir/reports/jacoco"))
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
