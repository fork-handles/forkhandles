description = "ForkHandles Library Testing Helpers (Hamkrest)"

dependencies {
    implementation(project(":result4k"))
    implementation("com.natpryce:hamkrest:_")

    testImplementation(project(path = ":result4k", configuration = "testArtifacts"))
}
