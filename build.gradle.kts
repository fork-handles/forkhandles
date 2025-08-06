import groovy.namespace.QName
import groovy.util.Node
import org.gradle.api.JavaVersion.VERSION_11
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    jacoco
    `java-library`
    signing

    alias(libs.plugins.coveralls)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.versions)
}

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.kotlin.gradle.plugin)
        classpath("com.github.kt3k.coveralls:com.github.kt3k.coveralls.gradle.plugin:${libs.versions.coveralls.get()}")
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

subprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "java")
    apply(plugin = "org.gradle.jacoco")
    apply(plugin = "com.github.kt3k.coveralls")
    apply(plugin = "java-test-fixtures")
    apply(plugin = "com.vanniktech.maven.publish.base")
    apply(plugin = "kotlin")

    version = project.properties["releaseVersion"] ?: "LOCAL"
    group = "dev.forkhandles"

    jacoco {
        toolVersion = rootProject.libs.versions.jacoco.get()
    }

    tasks {
        withType<KotlinJvmCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(JVM_11)
            }
        }

        java {
            sourceCompatibility = VERSION_11
            targetCompatibility = VERSION_11
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

    val sourcesJar by tasks.registering(Jar::class, fun Jar.() {
        archiveClassifier.set("sources")
        from(project.the<SourceSetContainer>()["main"].allSource)
        dependsOn(tasks.named("classes"))
    })

    val javadocJar by tasks.registering(Jar::class, fun Jar.() {
        archiveClassifier.set("javadoc")
        from(tasks.named<Javadoc>("javadoc").get().destinationDir)
        dependsOn(tasks.named("javadoc"))
    })

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

        val testJar by registering(Jar::class, fun Jar.() {
            archiveClassifier.set("test")
            from(project.the<SourceSetContainer>()["test"].output)
        })

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
        api(rootProject.libs.kotlin.stdlib)
        testApi(platform(rootProject.libs.junit.bom))
        testApi(rootProject.libs.junit.jupiter)
        testApi(rootProject.libs.junit.jupiter.api)
        testApi(rootProject.libs.junit.jupiter.engine)
        testApi(rootProject.libs.junit.platform.launcher)
        testApi(rootProject.libs.hamkrest)
    }

    val enableSigning = project.findProperty("sign") == "true"

    mavenPublishing {
        val javaComponent = components["java"] as AdhocComponentWithVariants

        javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
        javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }

        configure<PublishingExtension> {
            publications {
                create<MavenPublication>("maven") {
                    from(components["java"])
                    artifact(tasks.named("sourcesJar"))
                    artifact(tasks.named("javadocJar"))
                }
            }
            if (enableSigning) {
                apply(plugin = "signing")
                signing {
                    val signingKey: String? by project
                    val signingPassword: String? by project
                    useInMemoryPgpKeys(signingKey, signingPassword)
                    sign(publishing.publications)
                }
            }

            publishToMavenCentral(automaticRelease = true)

            coordinates(
                "dev.forkhandles",
                project.name,
                project.properties["releaseVersion"]?.toString() ?: "LOCAL"
            )

            pom {
                withXml {
                    asNode().appendNode("name", project.name)
                    asNode().appendNode("description", project.description)
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
                    asNode().appendNode("scm").appendNode("url", "git@github.com:fork-handles/forkhandles.git")
                        .parent()
                        .appendNode("connection", "scm:git:git@github.com:fork-handles/forkhandles.git").parent()
                        .appendNode("developerConnection", "scm:git:git@github.com:fork-handles/forkhandles.git")
                    asNode().appendNode("licenses").appendNode("license")
                        .appendNode("name", "Apache License, Version 2.0")
                        .parent().appendNode("url", "http://www.apache.org/licenses/LICENSE-2.0.html")
                    asNode()
                        .childrenCalled("dependencies")
                        .flatMap { it.childrenCalled("dependency") }
                        .flatMap { it.childrenCalled("scope") }
                        .forEach { if (it.text() == "runtime") it.setValue("provided") }
                }
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
    executionData.from(
        subprojects
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
