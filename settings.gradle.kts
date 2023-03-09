plugins {
    id("de.fayard.refreshVersions").version("0.51.0")
}

rootProject.name = "forkhandles"

fun String.includeModule(name: String) {
    val projectName = "$this-$name"
    include(":$projectName")
    project(":$projectName").projectDir = File("$this/${name.replace(':', '/')}")
}

include("forkhandles-bom")

include("bunting4k")
include("fabrikate4k")
include("parser4k")
include("partial4k")
include("result4k")
include("time4k")
include("tuples4k")
include("values4k")
