import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

description = "ForkHandles Transactor library"

dependencies {
    implementation(project(":result4k"))
    implementation("com.ubertob.kondor:kondor-core:3.6.1")

    testImplementation("com.ubertob.kondor:kondor-tools:3.6.1")
    
    testImplementation(kotlin("test-junit5"))
}


tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.set(freeCompilerArgs.get() + "-Xinline-classes")
    }
}
