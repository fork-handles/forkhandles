import groovy.namespace.QName
import groovy.util.Node
import org.gradle.api.JavaVersion.VERSION_1_8
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    kotlin("jvm") version "1.9.23"
    jacoco
    `java-library`
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
    id("com.github.kt3k.coveralls") version "2.12.2"
    id("org.jetbrains.kotlin.plugin.serialization")
    id("io.codearte.nexus-staging")
}

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
        classpath("com.github.kt3k.coveralls:com.github.kt3k.coveralls.gradle.plugin:_")
    }
}

apply(plugin = "io.codearte.nexus-staging")

allprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "org.gradle.jacoco")
    apply(plugin = "com.github.kt3k.coveralls")
    apply(plugin = "java-test-fixtures")
    apply(plugin = "maven-publish")

    version = project.properties["releaseVersion"] ?: "LOCAL"
    group = "dev.forkhandles"

    jacoco {
        toolVersion = "0.8.9"
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }

        java {
            sourceCompatibility = VERSION_1_8
            targetCompatibility = VERSION_1_8
        }

        withType<Test> {
            useJUnitPlatform()
        }

        if (hasCodeCoverage(project)) {
            named<JacocoReport>("jacocoTestReport") {
                reports {
                    html.required.set(true)
                    xml.required.set(true)
                    csv.required.set(false)
                }
            }
        }

        withType<GenerateModuleMetadata> {
            enabled = false
        }
    }
}

subprojects {

    apply(plugin = "java-test-fixtures")

    val sourcesJar by tasks.creating(Jar::class) {
        archiveClassifier.set("sources")
        from(project.the<SourceSetContainer>()["main"].allSource)
        dependsOn(tasks.named("classes"))
    }

    val javadocJar by tasks.creating(Jar::class) {
        archiveClassifier.set("javadoc")
        from(tasks.named<Javadoc>("javadoc").get().destinationDir)
        dependsOn(tasks.named("javadoc"))
    }

    tasks {
        named<Jar>("jar") {
            manifest {
                attributes(
                    mapOf(
                        "Implementation-Title" to project.name,
                        "Implementation-Vendor" to "dev.forkhandles",
                        "Implementation-Version" to project.version
                    )
                )
            }
        }

        val testJar by creating(Jar::class) {
            archiveClassifier.set("test")
            from(project.the<SourceSetContainer>()["test"].output)
        }

        configurations.create("testArtifacts") {
            extendsFrom(configurations["testApi"])
        }

        artifacts {
            add("testArtifacts", testJar)
            archives(sourcesJar)
            archives(javadocJar)
        }
    }

    dependencies {
        api(Kotlin.stdlib)
        testApi(Testing.junit.jupiter.api)
        testApi(Testing.junit.jupiter.engine)
        testApi("com.natpryce:hamkrest:_")
    }

    val enableSigning = project.findProperty("sign") == "true"

    val nexusUsername: String? by project
    val nexusPassword: String? by project

    apply(plugin = "maven-publish") // required to upload to sonatype

    if (enableSigning) { // when added it expects signing keys to be configured
        apply(plugin = "signing")
        signing {
            val signingKey: String? by project
            val signingPassword: String? by project
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(publishing.publications)
        }
    }

    publishing {
        val javaComponent = components["java"] as AdhocComponentWithVariants

        javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
        javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }

        publications {
            repositories {
                maven {
                    name = "SonatypeStaging"
                    url = URI.create("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                    credentials {
                        username = nexusUsername
                        password = nexusPassword
                    }
                }
                maven {
                    name = "SonatypeSnapshot"
                    url = URI.create("https://oss.sonatype.org/content/repositories/snapshots/")
                    credentials {
                        username = nexusUsername
                        password = nexusPassword
                    }
                }
            }


            val archivesBaseName = tasks.jar.get().archiveBaseName.get()
            create<MavenPublication>("mavenJava") {
                artifactId = archivesBaseName
                pom.withXml {
                    asNode().appendNode("name", archivesBaseName)
                    asNode().appendNode("description", description)
                    asNode().appendNode("url", "https://forkhandles.dev")
                    asNode().appendNode("developers")
                        .appendNode("developer").appendNode("name", "Nat Pryce").parent()
                        .appendNode("email", "nat@forkhandles.dev")
                        .parent().parent()
                        .appendNode("developer").appendNode("name", "David Denton").parent()
                        .appendNode("email", "david@forkhandles.dev")
                        .parent().parent()
                        .appendNode("developer").appendNode("name", "Dmitry Kandalov").parent()
                        .appendNode("email", "dmitry@forkhandles.dev")
                        .parent().parent()
                        .appendNode("developer").appendNode("name", "Duncan McGregor").parent()
                        .appendNode("email", "duncan@forkhandles.dev")
                    asNode().appendNode("scm").appendNode("url", "git@github.com:fork-handles/forkhandles.git").parent()
                        .appendNode("connection", "scm:git:git@github.com:fork-handles/forkhandles.git").parent()
                        .appendNode("developerConnection", "scm:git:git@github.com:fork-handles/forkhandles.git")
                    asNode().appendNode("licenses").appendNode("license")
                        .appendNode("name", "Apache License, Version 2.0")
                        .parent().appendNode("url", "http://www.apache.org/licenses/LICENSE-2.0.html")
                }

                from(components["java"])

                // replace all runtime dependencies with provided
                pom.withXml {
                    asNode()
                        .childrenCalled("dependencies")
                        .flatMap { it.childrenCalled("dependency") }
                        .flatMap { it.childrenCalled("scope") }
                        .forEach { if (it.text() == "runtime") it.setValue("provided") }
                }

                artifact(sourcesJar)
                artifact(javadocJar)
            }
        }
    }
}

fun Node.childrenCalled(wanted: String) = children()
    .filterIsInstance<Node>()
    .filter {
        val name = it.name()
        (name is QName) && name.localPart == wanted
    }

tasks.named<KotlinCompile>("compileTestKotlin") {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf("-Xjvm-default=all")
    }
}

fun hasCodeCoverage(project: Project) = project.name != "forkhandles-bom" &&
    !project.name.endsWith("generator")

coveralls {
    sourceDirs = subprojects.map { it.sourceSets.getByName("main").allSource.srcDirs }.flatten().map { it.absolutePath }
    jacocoReportPath = file("${layout.buildDirectory}/reports/jacoco/test/jacocoRootReport.xml")
}

tasks.register<JacocoReport>("jacocoRootReport") {
    dependsOn(subprojects.map { it.tasks.named<Test>("test").get() })

    sourceDirectories.from(subprojects.flatMap { it.the<SourceSetContainer>()["main"].allSource.srcDirs })
    classDirectories.from(subprojects.map { it.the<SourceSetContainer>()["main"].output })
    executionData.from(subprojects
        .filter { it.name != "forkhandles-bom" }
        .map {
            it.tasks.named<JacocoReport>("jacocoTestReport").get().executionData
        }
    )

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
        xml.outputLocation.set(file("${layout.buildDirectory}/reports/jacoco/test/jacocoRootReport.xml"))
    }
}

dependencies {
    subprojects
        .forEach {
            api(project(it.name))
        }
}

sourceSets {
    test {
        kotlin.srcDir("$projectDir/src/test/kotlin")
        kotlin.srcDir("$projectDir/src/docs")
        resources.srcDir("$projectDir/src/docs")
    }
}
