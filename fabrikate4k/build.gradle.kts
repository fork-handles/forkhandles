description = "ForkHandles test utility to instantiate objects with fake data"

apply(plugin = "kotlinx-serialization")

dependencies {
    api("org.jetbrains.kotlin:kotlin-reflect:_")
    testApi(Kotlin.test)
    testApi(KotlinX.serialization.json)
}
