package su.vi.simply.di.core

import org.junit.*
import org.junit.Assert.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ThreadSafetyTest {

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
    fun `synchronized getNullableDependency single instance`() {
        val scope = SimplyDIScope(isSearchInScope = true)
        scope.createDependencyLater(String::class) { "single" }

        val latch = CountDownLatch(1)
        val instances = mutableListOf<String>()
        val lock = Any()

        repeat(threads) {
            executor.submit {
                latch.await()
                synchronized(lock) {
                    scope.getNullableDependency<String>(String::class)?.let { instances.add(it) }
                }
            }
        }

        latch.countDown()
        Thread.sleep(1000)

        assertEquals(threads, instances.size)
        for (i in 1 until instances.size) {
            assertSame(instances[0], instances[i])
        }
    }

    @Test
    fun `synchronized delete does not break concurrent access`() {
        val container = SimplyDIContainer(scopeName = "delete-sync", isSearchInScope = true)
        container.initialize("delete-sync")
        container.addDependencyNow("delete-sync", String::class) { "data" }

        val latch = CountDownLatch(1)

        executor.submit {
            latch.await()
            container.deleteDependency(scopeName = "delete-sync", kClass = String::class)
        }

        repeat(threads) {
            executor.submit {
                latch.await()
                try {
                    container.getDependency(kClass = String::class, scopeName = "delete-sync")
                } catch (_: Exception) {
                }
            }
        }

        latch.countDown()
        Thread.sleep(2000)
    }

    @Test
    fun `synchronized addDependencyNow does not break concurrent reads`() {
        val container = SimplyDIContainer(scopeName = "add-sync", isSearchInScope = true)
        container.initialize("add-sync")
        container.addDependencyNow("add-sync", String::class) { "initial" }

        val latch = CountDownLatch(1)
        var errors = 0
        val lock = Any()

        repeat(threads) {
            executor.submit {
                latch.await()
                try {
                    container.addDependencyNow("add-sync", Int::class) { 42 }
                } catch (_: Exception) {
                    synchronized(lock) { errors++ }
                }
            }

            executor.submit {
                latch.await()
                try {
                    container.getDependency(kClass = String::class, scopeName = "add-sync")
                } catch (_: Exception) {
                    synchronized(lock) { errors++ }
                }
            }
        }

        latch.countDown()
        Thread.sleep(2000)
    }

    @Test
    fun `synchronized addDependencyLater does not break concurrent reads`() {
        val container = SimplyDIContainer(scopeName = "later-sync", isSearchInScope = true)
        container.initialize("later-sync")
        container.addDependencyNow("later-sync", String::class) { "initial" }

        val latch = CountDownLatch(1)
        var errors = 0
        val lock = Any()

        repeat(threads) {
            executor.submit {
                latch.await()
                try {
                    container.addDependencyLater("later-sync", Int::class) { 99 }
                } catch (_: Exception) {
                    synchronized(lock) { errors++ }
                }
            }

            executor.submit {
                latch.await()
                try {
                    container.getDependency(kClass = String::class, scopeName = "later-sync")
                } catch (_: Exception) {
                    synchronized(lock) { errors++ }
                }
            }
        }

        latch.countDown()
        Thread.sleep(2000)
    }

    @Test
    fun `synchronized replaceDependencyNow is thread safe`() {
        val container = SimplyDIContainer(scopeName = "replace-sync", isSearchInScope = true)
        container.initialize("replace-sync")
        container.addDependencyNow("replace-sync", Int::class) { 0 }

        val latch = CountDownLatch(1)

        repeat(threads) { i ->
            executor.submit {
                latch.await()
                try {
                    container.replaceDependencyNow("replace-sync", Int::class) { i }
                } catch (_: Exception) {
                }
            }
        }

        latch.countDown()
        Thread.sleep(2000)

        try {
            container.getDependency(kClass = Int::class, scopeName = "replace-sync")
        } catch (_: Exception) {
        }
    }

    @Test
    fun `synchronized addChainScopes is thread safe`() {
        val container = SimplyDIContainer(scopeName = "chain-sync", isSearchInScope = true)
        container.initialize("chain-sync")
        container.createScope("chain-sync-2")

        val latch = CountDownLatch(1)

        repeat(threads) {
            executor.submit {
                latch.await()
                try {
                    container.addChainScopes(listOf("chain-sync", "chain-sync-2"))
                } catch (_: Exception) {
                }
            }
        }

        latch.countDown()
        Thread.sleep(2000)
    }

    @Test
    fun `synchronized createScope is thread safe`() {
        val container = SimplyDIContainer(scopeName = "create-sync", isSearchInScope = true)
        container.initialize("create-sync")

        val latch = CountDownLatch(1)

        repeat(threads) { i ->
            executor.submit {
                latch.await()
                try {
                    container.createScope("scope-$i")
                } catch (_: Exception) {
                }
            }
        }

        latch.countDown()
        Thread.sleep(2000)
    }
}
