description = "ForkHandles Transactor library implementation for JPA"

plugins {
    kotlin("plugin.jpa")
}

dependencies {
    api(project(":tx4k"))
    implementation(project(":tx4k-jdbc"))
    compileOnly(libs.jpa)
    testImplementation(kotlin("test-junit5"))
    testImplementation(libs.hibernate)
    testImplementation(libs.eclipselink)
    testImplementation(project(path = ":tx4k", configuration = "testArtifacts"))
    testImplementation(libs.hsqldb)
    testImplementation(libs.bundles.junit)
    implementation(kotlin("stdlib"))
}

repositories {
    mavenCentral()
}
