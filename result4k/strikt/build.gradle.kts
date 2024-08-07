description = "ForkHandles Library Testing Helpers (Strikt)"

dependencies {
    implementation(project(":result4k"))
    implementation("io.strikt:strikt-core:_")

    testImplementation(project(path= ":result4k", configuration= "testArtifacts"))
}
