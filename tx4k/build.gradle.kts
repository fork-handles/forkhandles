import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

description = "ForkHandles Transactor library"

dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation(libs.bundles.junit)
    testImplementation(libs.bundles.testcontainers)
    testImplementation(libs.bundles.testcontainers.postgres)
    testImplementation(libs.bundles.testcontainers.mariadb)
}


tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.set(freeCompilerArgs.get() + "-Xinline-classes")
    }
}
