description = "ForkHandles Library Testing Helpers (Kotest)"

dependencies {
    implementation(project(":result4k"))
    implementation("io.kotest:kotest-assertions-core-jvm:_")

    testImplementation(project(path= ":result4k", configuration= "testArtifacts"))
}
