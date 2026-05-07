package su.vi.simply.di.core

import org.junit.*
import org.junit.Assert.*

class SimplyDIContainerSingletonTest {

    @Test
    fun `instance is singleton`() {
        val a = SimplyDIContainer.instance
        val b = SimplyDIContainer.instance
        assertSame(a, b)
    }

    @Test
    fun `instance has default scope name`() {
        val instance = SimplyDIContainer.instance
        assertEquals("MAIN CONTAINER", instance.scopeName)
    }

    @Test
    fun `instance has isSearchInScope false`() {
        val instance = SimplyDIContainer.instance
        assertFalse(instance.isSearchInScope)
    }

    @Test
    fun `instance works for dependency operations`() {
        val instance = SimplyDIContainer.instance
        instance.addDependencyNow(
            scopeName = instance.scopeName,
            kClass = String::class,
            factory = { "singleton dep" }
        )
        val result: String = instance.getDependency(
            kClass = String::class,
            scopeName = instance.scopeName
        )
        assertEquals("singleton dep", result)
    }
}
