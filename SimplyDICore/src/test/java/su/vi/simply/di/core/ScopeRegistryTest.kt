package su.vi.simply.di.core

import org.junit.*
import org.junit.Assert.*

class ScopeRegistryTest {

    private lateinit var registry: ScopeRegistry

    @Before
    fun setup() {
        registry = ScopeRegistry()
    }

    @Test
    fun `create returns new scope`() {
        val scope = registry.create("s1", true)
        assertTrue(scope.isSearchInScope)
    }

    @Test
    fun `create returns same scope on second call`() {
        val a = registry.create("s1", true)
        val b = registry.create("s1", true)
        assertSame(a, b)
    }

    @Test
    fun `get returns scope by name`() {
        registry.create("s1", true)
        assertNotNull(registry.get("s1"))
    }

    @Test
    fun `get returns null for unknown name`() {
        assertNull(registry.get("unknown"))
    }

    @Test
    fun `allScopes returns all registered`() {
        registry.create("s1", true)
        registry.create("s2", false)
        val all = registry.allScopes()
        assertEquals(2, all.size)
        assertTrue(all.containsKey("s1"))
        assertTrue(all.containsKey("s2"))
    }

    @Test
    fun `allScopes returns copy`() {
        registry.create("s1", true)
        val copy = registry.allScopes()
        assertEquals(1, copy.size)
        assertEquals(1, registry.allScopes().size)
    }

    @Test
    fun `destroyScope removes scope and clears it`() {
        registry.create("s1", true)
        registry.get("s1")!!.createDependencyNow(String::class) { "data" }
        val destroyed = registry.destroyScope("s1")
        assertNotNull(destroyed)
        assertNull(registry.get("s1"))
        assertFalse(destroyed!!.isDependencyInScope(String::class))
    }

    @Test
    fun `destroyScope returns null for unknown`() {
        val result = registry.destroyScope("nonexistent")
        assertNull(result)
    }

    @Test
    fun `empty registry allScopes`() {
        assertTrue(registry.allScopes().isEmpty())
    }
}
