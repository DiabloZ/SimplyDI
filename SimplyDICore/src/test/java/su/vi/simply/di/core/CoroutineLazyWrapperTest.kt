package su.vi.simply.di.core

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.*

class CoroutineLazyWrapperTest {

    private val coroutineCount = 10

    @Test
    fun `SimplyDILazyWrapper value evaluated only once under concurrent coroutines`() = runTest {
        var count = 0
        val wrapper = su.vi.simply.di.core.lazy.SimplyDILazyWrapper<Int> { count++; 42 }

        val jobs = (1..coroutineCount).map {
            async { wrapper.value }
        }
        val results = jobs.awaitAll()
        for (r in results) {
            assertEquals(42, r)
        }
    }

    @Test
    fun `SimplyDILazyWrapper invoke evaluated only once under concurrent coroutines`() = runTest {
        var count = 0
        val wrapper = su.vi.simply.di.core.lazy.SimplyDILazyWrapper<Int> { count++; 99 }

        val jobs = (1..coroutineCount).map {
            async { wrapper() }
        }
        val results = jobs.awaitAll()
        for (r in results) {
            assertEquals(99, r)
        }
    }

    @Test
    fun `SimplyDILazyWrapper mixed value and invoke under concurrent coroutines`() = runTest {
        var count = 0
        val wrapper = su.vi.simply.di.core.lazy.SimplyDILazyWrapper<String> { count++; "mixed" }

        val jobs = (1..coroutineCount).map { i ->
            async {
                if (i % 2 == 0) wrapper.value else wrapper()
            }
        }
        val results = jobs.awaitAll()
        for (r in results) {
            assertEquals("mixed", r)
        }
    }

    @Test
    fun `inject with SYNCHRONIZED under concurrent coroutines`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-inject-sync", isSearchInScope = true)
        container.initialize("coro-inject-sync")
        container.addDependencyLater("coro-inject-sync", Int::class) { 777 }

        val jobs = (1..coroutineCount).map {
            async {
                val lazy = su.vi.simply.di.core.delegates.inject<Int>(
                    container = container,
                    mode = LazyThreadSafetyMode.SYNCHRONIZED
                )
                lazy.value
            }
        }
        val results = jobs.awaitAll()
        for (r in results) {
            assertEquals(777, r)
        }
    }

    @Test
    fun `inject with PUBLICATION under concurrent coroutines`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-inject-pub", isSearchInScope = true)
        container.initialize("coro-inject-pub")
        container.addDependencyLater("coro-inject-pub", Int::class) { 888 }

        val jobs = (1..coroutineCount).map {
            async {
                val lazy = su.vi.simply.di.core.delegates.inject<Int>(
                    container = container,
                    mode = LazyThreadSafetyMode.PUBLICATION
                )
                lazy.value
            }
        }
        val results = jobs.awaitAll()
        for (r in results) {
            assertEquals(888, r)
        }
    }

    @Test
    fun `inject with NONE under concurrent coroutines`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-inject-none", isSearchInScope = true)
        container.initialize("coro-inject-none")
        container.addDependencyLater("coro-inject-none", Int::class) { 111 }

        val jobs = (1..coroutineCount).map {
            async {
                val lazy = su.vi.simply.di.core.delegates.inject<Int>(
                    container = container,
                    mode = LazyThreadSafetyMode.NONE
                )
                lazy.value
            }
        }
        val results = jobs.awaitAll()
        for (r in results) {
            assertEquals(111, r)
        }
    }

    @Test
    fun `SimplyDILazyWrapper value and invoke return same under concurrent coroutines`() = runTest {
        var count = 0
        val wrapper = su.vi.simply.di.core.lazy.SimplyDILazyWrapper<String> {
            count++
            "shared"
        }

        val v = async { wrapper.value }
        val i = async { wrapper() }
        val a = v.await()
        val b = i.await()
        assertSame(a, b)
    }

    @Test
    fun `SimplyDILazyWrapper factory throws under concurrent coroutines`() = runTest {
        val wrapper = su.vi.simply.di.core.lazy.SimplyDILazyWrapper<String> { throw RuntimeException("boom") }

        val jobs = (1..coroutineCount).map {
            async {
                try {
                    wrapper.value
                    false
                } catch (e: RuntimeException) {
                    e.message == "boom"
                }
            }
        }
        val results = jobs.awaitAll()
        for (r in results) {
            assertTrue(r)
        }
    }
}
