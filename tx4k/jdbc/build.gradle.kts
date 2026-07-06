description = "ForkHandles Transactor library"

dependencies {
    api(project(":tx4k"))
    testImplementation(kotlin("test-junit5"))
    testImplementation(project(path = ":tx4k", configuration = "testArtifacts"))
    testImplementation(libs.bundles.junit)
    testImplementation(libs.bundles.testcontainers)
    testImplementation(libs.bundles.testcontainers.postgres)
    testImplementation(libs.bundles.testcontainers.mariadb)
}
