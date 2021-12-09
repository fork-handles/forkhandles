package dev.forkhandles.time.executors

import java.time.Duration

interface ControllableTaskScheduler : TaskScheduler {
    fun timePasses(duration: Duration)
}
