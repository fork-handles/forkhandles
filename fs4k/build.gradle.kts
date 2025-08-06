description = "ForkHandles: Library to make manipulate file-systems."

dependencies {
    api(project(":values4k"))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}
