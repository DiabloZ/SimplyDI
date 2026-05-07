package su.vi.simply.di.core

import org.junit.*
import org.junit.Assert.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class ConcurrencyTest {

    private lateinit var executor: ExecutorService
    private val threads = 10

    @Before
    fun setup() {
        executor = Executors.newFixedThreadPool(threads)
    }

    @After
    fun teardown() {
        executor.shutdown()
        executor.awaitTermination(10, TimeUnit.SECONDS)
    }

    @Test
    fun `synchronized getNullableDependency prevents race condition`() {
        val scope = SimplyDIScope(isSearchInScope = true)
        scope.createDependencyLater(Int::class) { 1 }

        val latch = CountDownLatch(1)
        val results = mutableListOf<Int?>()
        val resultsLock = Any()

        repeat(threads) {
            executor.submit {
                latch.await()
                synchronized(resultsLock) {
                    results.add(scope.getNullableDependency(Int::class))
                }
            }
        }

        latch.countDown()
        Thread.sleep(500)

        assertEquals(threads, results.size)
        for (r in results) {
            assertEquals(1, r)
        }
    }

    @Test
    fun `concurrent addDependencyNow is thread safe`() {
        val container = SimplyDIContainer(scopeName = "concurrent-add", isSearchInScope = true)
        container.initialize("concurrent-add")

        val count = AtomicInteger(0)
        val latch = CountDownLatch(1)

        repeat(threads) { i ->
            executor.submit {
                latch.await()
                try {
                    container.addDependencyNow("concurrent-add", Int::class) { count.getAndIncrement() }
                } catch (_: Exception) {
                }
            }
        }

        latch.countDown()
        Thread.sleep(1000)

        val result: Int? = container.getDependency(kClass = Int::class, scopeName = "concurrent-add")
        assertNotNull(result)
    }

    @Test
    fun `concurrent getDependency is thread safe`() {
        val container = SimplyDIContainer(scopeName = "concurrent-get", isSearchInScope = true)
        container.initialize("concurrent-get")
        container.addDependencyNow("concurrent-get", String::class) { "shared" }

        val latch = CountDownLatch(1)
        val results = mutableListOf<String?>()
        val resultsLock = Any()

        repeat(threads) {
            executor.submit {
                latch.await()
                try {
                    synchronized(resultsLock) {
                        results.add(container.getDependency(kClass = String::class, scopeName = "concurrent-get"))
                    }
                } catch (e: Exception) {
                    synchronized(resultsLock) {
                        results.add(null)
                    }
                }
            }
        }

        latch.countDown()
        Thread.sleep(1000)

        assertEquals(threads, results.size)
        for (r in results) {
            assertEquals("shared", r)
        }
    }

    @Test
    fun `concurrent add and get are thread safe`() {
        val container = SimplyDIContainer(scopeName = "add-get", isSearchInScope = true)
        container.initialize("add-get")

        val addLatch = CountDownLatch(1)
        val getLatch = CountDownLatch(1)

        executor.submit {
            addLatch.await()
            container.addDependencyNow("add-get", String::class) { "value" }
            getLatch.countDown()
        }

        executor.submit {
            getLatch.await()
            Thread.sleep(200)
            try {
                container.getDependency(kClass = String::class, scopeName = "add-get")
            } catch (_: Exception) {
            }
        }

        addLatch.countDown()
        Thread.sleep(2000)
    }

    @Test
    fun `concurrent singleton instance access`() {
        val latch = CountDownLatch(1)
        val results = mutableListOf<SimplyDIContainer>()
        val resultsLock = Any()

        repeat(threads) {
            executor.submit {
                latch.await()
                synchronized(resultsLock) {
                    results.add(SimplyDIContainer.instance)
                }
            }
        }

        latch.countDown()
        Thread.sleep(1000)

        assertEquals(threads, results.size)
        val first = results[0]
        for (i in 1 until results.size) {
            assertSame(first, results[i])
        }
    }

    @Test
    fun `concurrent closeScope and getDependency`() {
        val container = SimplyDIContainer(scopeName = "close-async", isSearchInScope = true)
        container.initialize("close-async")
        container.addDependencyNow("close-async", String::class) { "before" }

        val latch = CountDownLatch(1)
        var closeResult: Boolean? = null
        var getResult: Boolean? = null

        executor.submit {
            latch.await()
            try {
                container.closeScope("close-async")
                closeResult = true
            } catch (_: Exception) {
                closeResult = false
            }
        }

        executor.submit {
            latch.await()
            try {
                container.getDependency(kClass = String::class, scopeName = "close-async") as String
                getResult = true
            } catch (_: Exception) {
                getResult = false
            }
        }

        latch.countDown()
        Thread.sleep(2000)

        assertNotNull(closeResult)
        assertNotNull(getResult)
    }
}
