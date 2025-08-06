description = "ForkHandles Library Testing Helpers (Kotest)"

dependencies {
    implementation(project(":result4k"))
    implementation(libs.kotest.assertions.core)

    testImplementation(project(path= ":result4k", configuration= "testArtifacts"))
}
