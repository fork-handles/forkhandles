plugins {
    id("de.fayard.refreshVersions").version("0.51.0")
}

rootProject.name = "forkhandles"

fun String.includeSubModule(name: String) {
    val projectName = "$this-${name.replace(':', '-')}"
    include(":$projectName")
    project(":$projectName").projectDir = File("$this/${name.replace(':', '/')}")
}

fun includeModule(name: String) {
    name.apply {
        include(this)
        includeSubModule("testing:kotest")
    }
}

include("forkhandles-bom")

include("bunting4k")
include("fabrikate4k")
include("parser4k")
include("partial4k")
includeModule("result4k")
include("time4k")
include("tuples4k")
include("values4k")
