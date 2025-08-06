description = "ForkHandles Library Testing Helpers (Hamkrest)"

dependencies {
    implementation(project(":result4k"))
    implementation(libs.hamkrest)

    testImplementation(project(path = ":result4k", configuration = "testArtifacts"))
}
