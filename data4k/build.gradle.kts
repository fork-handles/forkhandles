description = "ForkHandles data-oriented programming library"

dependencies {
    api("org.jetbrains.kotlin:kotlin-reflect:_")
    api(project(":values4k"))
    compileOnly("com.fasterxml.jackson.core:jackson-databind:_")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:_")
    testImplementation("io.strikt:strikt-jvm:_")
    testImplementation("com.oneeyedmen:okeydoke:2.0.3")
}
