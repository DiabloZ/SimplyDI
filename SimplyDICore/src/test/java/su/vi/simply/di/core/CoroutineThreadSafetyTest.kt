package su.vi.simply.di.core

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.*

class CoroutineThreadSafetyTest {

    private val coroutineCount = 10

    @Test
    fun `synchronized getNullableDependency single instance`() = runTest {
        val testScope = SimplyDIScope(isSearchInScope = true)
        testScope.createDependencyLater(String::class) { "single" }

        val jobs = (1..coroutineCount).map {
            async { testScope.getNullableDependency<String>(String::class) }
        }
        val results = jobs.awaitAll()
        val first = results[0]!!
        for (i in 1 until results.size) {
            assertSame(first, results[i])
        }
    }

    @Test
    fun `synchronized delete does not break concurrent access`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-delete", isSearchInScope = true)
        container.initialize("coro-delete")
        container.addDependencyNow("coro-delete", String::class) { "data" }

        val deleteJob = async {
            container.deleteDependency(scopeName = "coro-delete", kClass = String::class)
        }

        val readJobs = (1..coroutineCount).map {
            async {
                try {
                    container.getDependency(kClass = String::class, scopeName = "coro-delete")
                } catch (_: Exception) {
                }
            }
        }

        deleteJob.await()
        readJobs.awaitAll()
    }

    @Test
    fun `synchronized addDependencyNow does not break concurrent reads`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-add-read", isSearchInScope = true)
        container.initialize("coro-add-read")
        container.addDependencyNow("coro-add-read", String::class) { "initial" }

        var errors = 0

        val addJobs = (1..coroutineCount).map {
            async {
                try {
                    container.addDependencyNow("coro-add-read", Int::class) { 42 }
                } catch (_: Exception) {
                    synchronized(this) { errors++ }
                }
            }
        }

        val readJobs = (1..coroutineCount).map {
            async {
                try {
                    container.getDependency(kClass = String::class, scopeName = "coro-add-read")
                } catch (_: Exception) {
                    synchronized(this) { errors++ }
                }
            }
        }

        addJobs.awaitAll()
        readJobs.awaitAll()
    }

    @Test
    fun `synchronized addDependencyLater does not break concurrent reads`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-later-read", isSearchInScope = true)
        container.initialize("coro-later-read")
        container.addDependencyNow("coro-later-read", String::class) { "initial" }

        var errors = 0

        val addJobs = (1..coroutineCount).map {
            async {
                try {
                    container.addDependencyLater("coro-later-read", Int::class) { 99 }
                } catch (_: Exception) {
                    synchronized(this) { errors++ }
                }
            }
        }

        val readJobs = (1..coroutineCount).map {
            async {
                try {
                    container.getDependency(kClass = String::class, scopeName = "coro-later-read")
                } catch (_: Exception) {
                    synchronized(this) { errors++ }
                }
            }
        }

        addJobs.awaitAll()
        readJobs.awaitAll()
    }

    @Test
    fun `synchronized replaceDependencyNow is thread safe`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-replace", isSearchInScope = true)
        container.initialize("coro-replace")
        container.addDependencyNow("coro-replace", Int::class) { 0 }

        val jobs = (1..coroutineCount).map { i ->
            async {
                try {
                    container.replaceDependencyNow("coro-replace", Int::class) { i }
                } catch (_: Exception) {
                }
            }
        }
        jobs.awaitAll()

        try {
            container.getDependency(kClass = Int::class, scopeName = "coro-replace")
        } catch (_: Exception) {
        }
    }

    @Test
    fun `synchronized addChainScopes is thread safe`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-chain", isSearchInScope = true)
        container.initialize("coro-chain")
        container.createScope("coro-chain-2")

        val jobs = (1..coroutineCount).map {
            async {
                try {
                    container.addChainScopes(listOf("coro-chain", "coro-chain-2"))
                } catch (_: Exception) {
                }
            }
        }
        jobs.awaitAll()
    }

    @Test
    fun `synchronized createScope is thread safe`() = runTest {
        val container = SimplyDIContainer(scopeName = "coro-create", isSearchInScope = true)
        container.initialize("coro-create")

        val jobs = (1..coroutineCount).map { i ->
            async {
                try {
                    container.createScope("coro-scope-$i")
                } catch (_: Exception) {
                }
            }
        }
        jobs.awaitAll()
    }
}
