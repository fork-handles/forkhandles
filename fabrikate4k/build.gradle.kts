description = "ForkHandles test utility to instantiate objects with fake data"

apply(plugin = "kotlinx-serialization")

dependencies {
    api(libs.kotlin.reflect)
    testApi(libs.kotlin.test)
    testApi(libs.kotlinx.serialization.json)
}
