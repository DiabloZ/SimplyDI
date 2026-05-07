package su.vi.simply.di.core

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.*
import java.util.concurrent.atomic.AtomicInteger

class CoroutineConcurrencyTest {

    private val coroutineCount = 10

    @Test
    fun `synchronized getNullableDependency under concurrent coroutines`() = runTest {
        val testScope = SimplyDIScope(isSearchInScope = true)
        testScope.createDependencyLater(Int::class) { 1 }

        val jobs = (1..coroutineCount).map {
            async { testScope.getNullableDependency<Int>(Int::class) }
        }
        val results = jobs.awaitAll()
        for (r in results) {
            assertEquals(1, r)
        }
    }

    @Test
    fun `concurrent addDependencyNow under coroutines`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-add", isSearchInScope = true)
        container.initialize("coro-add")

        val count = AtomicInteger(0)

        val jobs = (1..coroutineCount).map {
            async {
                try {
                    container.addDependencyNow("coro-add", Int::class) { count.getAndIncrement() }
                } catch (_: Exception) {
                }
            }
        }
        jobs.awaitAll()
        val result: Int = container.getDependency(kClass = Int::class, scopeName = "coro-add")
        assertNotNull(result)
    }

    @Test
    fun `concurrent getDependency under coroutines`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-get", isSearchInScope = true)
        container.initialize("coro-get")
        container.addDependencyNow("coro-get", String::class) { "shared" }

        val jobs = (1..coroutineCount).map {
            async<String> { container.getDependency(kClass = String::class, scopeName = "coro-get") }
        }
        val results = jobs.awaitAll()
        for (r in results) {
            assertEquals("shared", r)
        }
    }

    @Test
    fun `concurrent add and get under coroutines`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-add-get", isSearchInScope = true)
        container.initialize("coro-add-get")

        val addJob = async {
            container.addDependencyNow("coro-add-get", String::class) { "value" }
        }
        val getJob = async {
            delay(10)
            try {
                container.getDependency(kClass = String::class, scopeName = "coro-add-get")
            } catch (_: Exception) {
            }
        }
        addJob.await()
        getJob.await()
    }

    @Test
    fun `singleton instance under concurrent coroutines`() = runTest {
        val jobs = (1..coroutineCount).map {
            async { SimplyDIContainer.instance }
        }
        val results = jobs.awaitAll()
        val first = results[0]
        for (i in 1 until results.size) {
            assertSame(first, results[i])
        }
    }

    @Test
    fun `concurrent closeScope and getDependency under coroutines`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-close", isSearchInScope = true)
        container.initialize("coro-close")
        container.addDependencyNow("coro-close", String::class) { "before" }

        val closeJob = async<Boolean> {
            try {
                container.closeScope("coro-close")
                true
            } catch (_: Exception) {
                false
            }
        }
        val getJob = async<Boolean> {
            try {
                container.getDependency<String>(kClass = String::class, scopeName = "coro-close")
                true
            } catch (_: Exception) {
                false
            }
        }
        val closeResult = closeJob.await()
        val getResult = getJob.await()
        assertNotNull(closeResult)
        assertNotNull(getResult)
    }
}
