import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

description = "ForkHandles Value-types library"

dependencies {
    implementation(project(":result4k"))
}

tasks.named<KotlinCompile>("compileTestKotlin") {
    kotlinOptions {
        freeCompilerArgs += listOf("-Xinline-classes")
    }
}
