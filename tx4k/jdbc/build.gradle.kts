description = "ForkHandles Transactor library implementation for JDBC"


dependencies {
    api(project(":tx4k"))
    testImplementation(kotlin("test-junit5"))
    testImplementation(project(path = ":tx4k", configuration = "testArtifacts"))
    testImplementation(libs.bundles.junit)
    testImplementation(libs.bundles.testcontainers)
    testImplementation(libs.bundles.testcontainers.postgres)
    testImplementation(libs.bundles.testcontainers.mariadb)
    testImplementation("org.hsqldb:hsqldb:2.7.2")
}
