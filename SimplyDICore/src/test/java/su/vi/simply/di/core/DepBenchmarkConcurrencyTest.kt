package su.vi.simply.di.core

import org.junit.*
import org.junit.Assert.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class DepBenchmarkConcurrencyTest {

    private lateinit var executor: ExecutorService

    @Before
    fun setup() {
        executor = Executors.newFixedThreadPool(2)
    }

    @After
    fun teardown() {
        executor.shutdown()
        executor.awaitTermination(10, TimeUnit.SECONDS)
    }

    @Test
    fun `depBenchmark starts background thread`() {
        val container = SimplyDIContainer(scopeName = "benchmark", isSearchInScope = true)
        container.initialize(scopeName = "benchmark", simplyLogLevel = su.vi.simply.di.core.utils.SimplyLogLevel.FULL)
        container.addDependencyNow("benchmark", String::class) { "benchmark" }

        val latch = CountDownLatch(1)

        executor.submit {
            latch.await()
            container.depBenchmark<String>(kClass = String::class)
            Thread.sleep(5000)
        }

        latch.countDown()
    }

    @Test
    fun `depBenchmark does not interfere with normal operations`() {
        val container = SimplyDIContainer(scopeName = "benchmark-safe", isSearchInScope = true)
        container.initialize("benchmark-safe")
        container.addDependencyNow("benchmark-safe", String::class) { "safe" }

        val latch = CountDownLatch(1)

        executor.submit {
            latch.await()
            container.depBenchmark<String>(kClass = String::class)
            Thread.sleep(5000)
        }

        latch.countDown()

        Thread.sleep(100)

        val result: String = container.getDependency(kClass = String::class, scopeName = "benchmark-safe")
        assertEquals("safe", result)
    }
}
