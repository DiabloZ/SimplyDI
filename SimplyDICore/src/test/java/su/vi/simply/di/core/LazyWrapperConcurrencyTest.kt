package su.vi.simply.di.core

import org.junit.*
import org.junit.Assert.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class LazyWrapperConcurrencyTest {

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
    fun `SimplyDILazyWrapper value is evaluated only once under concurrency`() {
        var count = 0
        val wrapper = su.vi.simply.di.core.lazy.SimplyDILazyWrapper<Int> { count++; 42 }

        val latch = CountDownLatch(1)
        val results = mutableListOf<Int>()
        val lock = Any()

        repeat(threads) {
            executor.submit {
                latch.await()
                synchronized(lock) {
                    results.add(wrapper.value)
                }
            }
        }

        latch.countDown()
        Thread.sleep(1000)

        assertEquals(threads, results.size)
        for (r in results) {
            assertEquals(42, r)
        }
    }

    @Test
    fun `SimplyDILazyWrapper invoke is evaluated only once under concurrency`() {
        var count = 0
        val wrapper = su.vi.simply.di.core.lazy.SimplyDILazyWrapper<Int> { count++; 99 }

        val latch = CountDownLatch(1)
        val results = mutableListOf<Int>()
        val lock = Any()

        repeat(threads) {
            executor.submit {
                latch.await()
                synchronized(lock) {
                    results.add(wrapper())
                }
            }
        }

        latch.countDown()
        Thread.sleep(1000)

        assertEquals(threads, results.size)
        for (r in results) {
            assertEquals(99, r)
        }
    }

    @Test
    fun `SimplyDILazyWrapper value and invoke mixed access`() {
        var count = 0
        val wrapper = su.vi.simply.di.core.lazy.SimplyDILazyWrapper<String> { count++; "mixed" }

        val latch = CountDownLatch(1)
        val results = mutableListOf<String>()
        val lock = Any()

        repeat(threads) { i ->
            executor.submit {
                latch.await()
                synchronized(lock) {
                    if (i % 2 == 0) {
                        results.add(wrapper.value)
                    } else {
                        results.add(wrapper())
                    }
                }
            }
        }

        latch.countDown()
        Thread.sleep(1000)

        assertEquals(threads, results.size)
        for (r in results) {
            assertEquals("mixed", r)
        }
    }

    @Test
    fun `inject with SYNCHRONIZED is thread safe`() {
        val container = SimplyDIContainer(scopeName = "inject-sync", isSearchInScope = true)
        container.initialize("inject-sync")
        container.addDependencyLater("inject-sync", Int::class) { 777 }

        val latch = CountDownLatch(1)
        val results = mutableListOf<Int>()
        val lock = Any()

        repeat(threads) {
            executor.submit {
                latch.await()
                val lazy = su.vi.simply.di.core.delegates.inject<Int>(
                    container = container,
                    mode = LazyThreadSafetyMode.SYNCHRONIZED
                )
                synchronized(lock) {
                    results.add(lazy.value)
                }
            }
        }

        latch.countDown()
        Thread.sleep(1000)

        assertEquals(threads, results.size)
        for (r in results) {
            assertEquals(777, r)
        }
    }

    @Test
    fun `inject with PUBLICATION is thread safe`() {
        val container = SimplyDIContainer(scopeName = "inject-pub", isSearchInScope = true)
        container.initialize("inject-pub")
        container.addDependencyLater("inject-pub", Int::class) { 888 }

        val latch = CountDownLatch(1)
        val results = mutableListOf<Int>()
        val lock = Any()

        repeat(threads) {
            executor.submit {
                latch.await()
                val lazy = su.vi.simply.di.core.delegates.inject<Int>(
                    container = container,
                    mode = LazyThreadSafetyMode.PUBLICATION
                )
                synchronized(lock) {
                    results.add(lazy.value)
                }
            }
        }

        latch.countDown()
        Thread.sleep(1000)

        assertEquals(threads, results.size)
        for (r in results) {
            assertEquals(888, r)
        }
    }

    @Test
    fun `inject with NONE is thread safe`() {
        val container = SimplyDIContainer(scopeName = "inject-none", isSearchInScope = true)
        container.initialize("inject-none")
        container.addDependencyLater("inject-none", Int::class) { 111 }

        val latch = CountDownLatch(1)
        val results = mutableListOf<Int>()
        val lock = Any()

        repeat(threads) {
            executor.submit {
                latch.await()
                val lazy = su.vi.simply.di.core.delegates.inject<Int>(
                    container = container,
                    mode = LazyThreadSafetyMode.NONE
                )
                synchronized(lock) {
                    results.add(lazy.value)
                }
            }
        }

        latch.countDown()
        Thread.sleep(1000)

        assertEquals(threads, results.size)
        for (r in results) {
            assertEquals(111, r)
        }
    }

    @Test
    fun `inject with container and SYNCHRONIZED`() {
        val container = SimplyDIContainer(scopeName = "inject-container-sync", isSearchInScope = true)
        container.initialize("inject-container-sync")
        container.addDependencyLater("inject-container-sync", String::class) { "container-sync" }

        val latch = CountDownLatch(1)
        val results = mutableListOf<String>()
        val lock = Any()

        repeat(threads) {
            executor.submit {
                latch.await()
                val lazy = su.vi.simply.di.core.delegates.inject<String>(
                    container = container,
                    mode = LazyThreadSafetyMode.SYNCHRONIZED
                )
                synchronized(lock) {
                    results.add(lazy.value)
                }
            }
        }

        latch.countDown()
        Thread.sleep(1000)

        assertEquals(threads, results.size)
        for (r in results) {
            assertEquals("container-sync", r)
        }
    }
}
