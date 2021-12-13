package dev.forkhandles.time.executors

import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SimpleSchedulerServiceTest {

    @Test
    fun `can schedule something and shutdown via the adapter`() {
        val executor = Executors.newScheduledThreadPool(3)
        val service = SimpleSchedulerService(executor)

        val counter = CountDownLatch(1)

        try {
            service.submit { counter.countDown() }

            counter.await(5, TimeUnit.SECONDS)
        }
        finally {
            service.shutdown()
        }

        executor.awaitTermination(1, TimeUnit.SECONDS)
    }
}
