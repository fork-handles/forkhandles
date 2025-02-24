plugins {

    id("de.fayard.refreshVersions").version("0.60.5")
}

refreshVersions {
    enableBuildSrcLibs()

    rejectVersionIf {
        candidate.stabilityLevel.isLessStableThan(current.stabilityLevel)
    }
}

rootProject.name = "forkhandles"

fun String.includeSubModule(name: String) {
    val projectName = "$this-${name.replace(':', '-')}"
    include(":$projectName")
    project(":$projectName").projectDir = File("$this/${name.replace(':', '/')}")
}

fun String.includeModule(dir: String) {
    include(":$this")
    project(":$this").projectDir = File("$this/$dir")
}

include("forkhandles-bom")

include("bunting4k")
include("data4k")
include("fabrikate4k")
include("fs4k")
include("mock4k")
include("parser4k")
include("partial4k")

"result4k".apply {
    includeModule("core")
    includeSubModule("kotest")
    includeSubModule("hamkrest")
    includeSubModule("strikt")
}

include("ropes4k")
include("state4k")
include("time4k")
include("tuples4k")
include("values4k")
