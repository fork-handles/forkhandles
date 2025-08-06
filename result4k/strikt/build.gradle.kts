description = "ForkHandles Library Testing Helpers (Strikt)"

dependencies {
    implementation(project(":result4k"))
    implementation(libs.strikt.core)

    testImplementation(project(path= ":result4k", configuration= "testArtifacts"))
}
