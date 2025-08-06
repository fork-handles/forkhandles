/*
 * Copyright (C) 2024 James Richardson
 * Licenced under GPL
 */

description = "ForkHandles ropes library"

plugins {
    alias(libs.plugins.kotlinx.benchmark)
    alias(libs.plugins.jmhreport)
}

kotlin {
    explicitApi()
}

dependencies {
    testImplementation(libs.kotlinx.benchmark.runtime)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    testImplementation(libs.strikt.core)
}

tasks.test {
    useJUnitPlatform {
        excludeTags("Performance")
    }
}

// build.gradle.kts
benchmark {
    targets {
        register("test")
    }
    benchmark {
        configurations {
            register("single") {
                include(".*.\\.WriteComplexBenchmark")
            }
        }
    }
}

// currently has to be run by hand after the benchmark,else it picks up previous run :-(
jmhReport {

    fun findMostRecentJmhReportIn(d: File): String? {
        return d.walkBottomUp()
            .filter { it.name == "test.json" }
            .sortedByDescending { it.lastModified() }
            .firstOrNull()
            ?.absolutePath
            ?.also {
                println("Selected JMH Report is $it")
            }
    }

    jmhResultPath = findMostRecentJmhReportIn(project.file("build/reports/benchmarks"))
    jmhReportOutput = project.file("build/reports/benchmarks").absolutePath
}
