description = "ForkHandles data-oriented programming library"

dependencies {
    api(libs.kotlin.reflect)
    api(project(":values4k"))
    implementation(libs.jackson.databind)
    testImplementation(libs.jackson.databind)
    testImplementation(libs.strikt.jvm)
    testImplementation(libs.okeydoke)
}
