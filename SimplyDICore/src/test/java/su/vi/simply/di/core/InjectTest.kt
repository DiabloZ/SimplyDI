package su.vi.simply.di.core

import org.junit.*
import org.junit.Assert.*
import su.vi.simply.di.core.delegates.inject

class InjectTest {

    @Test
    fun `inject with container returns lazy`() {
        val container = SimplyDIContainer(scopeName = "inj-scope", isSearchInScope = true)
        container.initialize("inj-scope")
        container.addDependencyNow("inj-scope", Int::class) { 777 }
        val lazy: Lazy<Int> = inject(container = container)
        assertEquals(777, lazy.value)
    }

    @Test
    fun `inject is lazy`() {
        val container = SimplyDIContainer(scopeName = "lazy-inj", isSearchInScope = true)
        container.initialize("lazy-inj")
        container.addDependencyLater("lazy-inj", String::class) { "deferred" }
        val lazy: Lazy<String> = inject(container = container)
        assertEquals("deferred", lazy.value)
    }

    @Test
    fun `inject cached on repeated access`() {
        var count = 0
        val container = SimplyDIContainer(scopeName = "cache-inj", isSearchInScope = true)
        container.initialize("cache-inj")
        container.addDependencyLater("cache-inj", Int::class) { ++count }
        val lazy: Lazy<Int> = inject(container = container)
        lazy.value
        lazy.value
        assertEquals(1, count)
    }

    @Test
    fun `inject throws when dependency not found`() {
        val container = SimplyDIContainer(scopeName = "fail-inj", isSearchInScope = true)
        container.initialize("fail-inj")
        val lazy: Lazy<String> = inject(container = container)
        try {
            lazy.value
            fail("Expected SimplyDINotFoundException")
        } catch (e: Exception) {
            assertTrue(e is su.vi.simply.di.core.error.SimplyDINotFoundException)
        }
    }
}
