import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
    id("org.sonarqube") version "2.8"
    jacoco
}

group = "com.github.asyncmc"
version = "0.1.0-SNAPSHOT"

val moduleName = "org.github.asyncmc.protocol.bedrock"

repositories {
    jcenter()
    maven(url = "https://repo.gamemods.com.br/public/")
}

sonarqube {
    properties {
        property("sonar.scm.provider", "git")
        //property("sonar.jacoco.reportPaths", allTestCoverageFile)
        property("sonar.host.url", project.findProperty("asyncmc.sonar.host.url") ?: System.getenv("asyncmc_sonar_host_url"))
        property("sonar.login", project.findProperty("asyncmc.sonar.login.token")?.takeUnless { it.toString().startsWith("<secret") } ?: System.getenv("litecraft_sonar_login_token")?.takeIf { it.isNotBlank() } ?: System.getenv("SONAR_TOKEN"))
        property("sonar.organization", project.findProperty("asyncmc.sonar.organization") ?: System.getenv("asyncmc_sonar_organization"))
        property("sonar.projectKey",project.findProperty("asyncmc.sonar.projectKey") ?: System.getenv("asyncmc_sonar_projectKey"))
        property("sonar.projectName", project.findProperty("asyncmc.sonar.projectName") ?: System.getenv("asyncmc_sonar_projectName"))
        property("sonar.rootModuleName", project.findProperty("asyncmc.sonar.rootModuleName") ?: System.getenv("asyncmc_sonar_rootModuleName"))
        property("sonar.cpd.cross_project", true)
        property("sonar.java.source", "14")
        //property("sonar.java.binaries", "build/libs/xyz-0.0.1-SNAPSHOT.jar")
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.sources", "src/main")
        property("sonar.tests", "src/test")
        //property("sonar.java.test.binaries", "build/classes/java/test")
        property("sonar.coverage.jacoco.xmlReportPaths", "$buildDir/reports/jacoco/codeCoverageReport.xml")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.javaModuleVersion.set(provider { project.version as String })

    // this is needed because we have a separate compile step in this example with the 'module-info.java' is in 'main/java' and the Kotlin code is in 'main/kotlin'
    //options.compilerArgs = listOf("--patch-module", "$moduleName=${sourceSets.main.get().output.asPath}")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "13"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.contracts.ExperimentalContracts"
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

dependencies {
    api(kotlin("stdlib-jdk8", embeddedKotlinVersion))
    api(kotlin("reflect", embeddedKotlinVersion))

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

tasks.named("sonarqube") {
    dependsOn("jacocoTestReport")
}
