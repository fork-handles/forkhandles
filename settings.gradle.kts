plugins {
    id("de.fayard.refreshVersions").version("0.51.0")
}

rootProject.name = "forkhandles"

fun String.includeAsSubModule(name: String) {
    val projectName = "$this-${name.replace(':', '-')}"
    include(":$projectName")
    project(":$projectName").projectDir = File("$this/${name.replace(':', '/')}")
}

fun String.includeAsModule(dir: String) {
    include(":$this")
    project(":$this").projectDir = File("$this/$dir")
}

include("forkhandles-bom")

include("bunting4k")
include("fabrikate4k")
include("parser4k")
include("partial4k")
"result4k".apply {
    includeAsModule("core")
    includeAsSubModule("kotest")
}
include("time4k")
include("tuples4k")
include("values4k")
