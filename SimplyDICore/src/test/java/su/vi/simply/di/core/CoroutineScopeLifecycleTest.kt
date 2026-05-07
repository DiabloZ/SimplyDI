package su.vi.simply.di.core

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.*

class CoroutineScopeLifecycleTest {

    private val coroutineCount = 10

    @Test
    fun `container operations inside coroutine scope`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-lifecycle", isSearchInScope = true)
        container.initialize("coro-lifecycle")

        val job = launch {
            container.addDependencyNow("coro-lifecycle", String::class) { "async" }
            assertEquals("async", container.getDependency(kClass = String::class, scopeName = "coro-lifecycle"))
        }
        job.join()
    }

    @Test
    fun `sequential add and get inside coroutines`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-seq", isSearchInScope = true)
        container.initialize("coro-seq")

        val addJob = async<String> {
            container.addDependencyNow("coro-seq", String::class) { "seq" }
            container.getDependency(kClass = String::class, scopeName = "coro-seq")
        }

        assertEquals("seq", addJob.await())
    }

    @Test
    fun `multiple containers in parallel coroutines`() = runTest {
        val results = (1..coroutineCount).map { i ->
            async<Int> {
                val c = SimplyDIContainer(scopeName = "multi-$i", isSearchInScope = true)
                c.initialize("multi-$i")
                c.addDependencyNow("multi-$i", Int::class) { i }
                c.getDependency(kClass = Int::class, scopeName = "multi-$i")
            }
        }.awaitAll()

        for (i in results.indices) {
            assertEquals(i + 1, results[i])
        }
    }

    @Test
    fun `cancel coroutine scope does not affect container`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-cancel", isSearchInScope = true)
        container.initialize("coro-cancel")

        val childScope = coroutineScope {
            val job = launch {
                delay(10000)
            }
            delay(10)
            job.cancel()
        }

        container.addDependencyNow("coro-cancel", String::class) { "after-cancel" }
        assertEquals("after-cancel", container.getDependency(kClass = String::class, scopeName = "coro-cancel"))
    }

    @Test
    fun `async operations with structured concurrency`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-structured", isSearchInScope = true)
        container.initialize("coro-structured")

        val addJob = async<Unit> {
            container.addDependencyLater("coro-structured", Int::class) { 42 }
        }
        addJob.await()

        val getJob = async<Int> {
            container.getDependency(kClass = Int::class, scopeName = "coro-structured")
        }
        assertEquals(42, getJob.await())
    }

    @Test
    fun `deferred dependency with async`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-deferred", isSearchInScope = true)
        container.initialize("coro-deferred")

        var factoryCalled = false
        container.addDependencyLater("coro-deferred", String::class) {
            factoryCalled = true
            "deferred"
        }

        assertFalse(factoryCalled)

        val result = async<String> {
            container.getDependency(kClass = String::class, scopeName = "coro-deferred")
        }.await()

        assertEquals("deferred", result)
        assertTrue(factoryCalled)
    }

    @Test
    fun `timeout on long async operation`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-timeout", isSearchInScope = true)
        container.initialize("coro-timeout")
        container.addDependencyNow("coro-timeout", String::class) { "ok" }

        val result: Int? = withTimeoutOrNull(1000) {
            val jobs = (1..coroutineCount).map {
                async<String> { container.getDependency(kClass = String::class, scopeName = "coro-timeout") }
            }
            jobs.awaitAll().size
        }
        assertEquals(coroutineCount, result)
    }

    @Test
    fun `withContext dispatcher change does not break container`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-dispatcher", isSearchInScope = true)
        container.initialize("coro-dispatcher")

        val result: String = withContext(Dispatchers.Default) {
            container.addDependencyNow("coro-dispatcher", String::class) { "dispatcher" }
            container.getDependency(kClass = String::class, scopeName = "coro-dispatcher")
        }
        assertEquals("dispatcher", result)
    }
}
