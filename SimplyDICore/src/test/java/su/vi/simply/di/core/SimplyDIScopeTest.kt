package su.vi.simply.di.core

import org.junit.*
import org.junit.Assert.*

class SimplyDIScopeTest {

    private lateinit var scope: SimplyDIScope

    @Before
    fun setup() {
        scope = SimplyDIScope(isSearchInScope = true)
    }

    @Test
    fun `isSearchInScope is true`() {
        assertTrue(scope.isSearchInScope)
    }

    @Test
    fun `isSearchInScope can be false`() {
        val s = SimplyDIScope(isSearchInScope = false)
        assertFalse(s.isSearchInScope)
    }

    @Test
    fun `createDependencyNow stores and retrieves immediately`() {
        scope.createDependencyNow(String::class) { "instant" }
        val result: String? = scope.getNullableDependency(String::class)
        assertEquals("instant", result)
    }

    @Test
    fun `createDependencyLater stores factory only`() {
        scope.createDependencyLater(String::class) { "lazy" }
        val result: String? = scope.getNullableDependency(String::class)
        assertEquals("lazy", result)
    }

    @Test
    fun `getNullableDependency returns null for unknown class`() {
        val result: String? = scope.getNullableDependency(String::class)
        assertNull(result)
    }

    @Test
    fun `getNullableDependency caches after first invocation`() {
        var count = 0
        scope.createDependencyLater(String::class) { "v${++count}" }
        val a: String? = scope.getNullableDependency(String::class)
        val b: String? = scope.getNullableDependency(String::class)
        assertSame(a, b)
        assertEquals("v1", a)
    }

    @Test
    fun `getFactoryDependency creates new instance each time`() {
        var count = 0
        scope.createDependencyLater(Int::class) { ++count }
        val a: Int = scope.getFactoryDependency(Int::class)
        val b: Int = scope.getFactoryDependency(Int::class)
        assertNotEquals(a, b)
        assertEquals(1, a)
        assertEquals(2, b)
    }

    @Test
    fun `getFactoryDependency throws for unknown class`() {
        try {
            scope.getFactoryDependency<String>(String::class)
            fail("Expected SimplyDINotFoundException")
        } catch (e: Exception) {
            assertTrue(e is su.vi.simply.di.core.error.SimplyDINotFoundException)
        }
    }

    @Test
    fun `getByClass creates new instance each time`() {
        var count = 0
        scope.createDependencyLater(Int::class) { ++count }
        val a: Int? = scope.getByClass(Int::class)
        val b: Int? = scope.getByClass(Int::class)
        assertNotEquals(a, b)
    }

    @Test
    fun `getByClass returns null for unknown class`() {
        val result: String? = scope.getByClass(String::class)
        assertNull(result)
    }

    @Test
    fun `delete removes factory and cache`() {
        scope.createDependencyNow(String::class) { "to delete" }
        scope.delete(String::class)
        val a: String? = scope.getNullableDependency(String::class)
        val b: String? = scope.getByClass(String::class)
        assertNull(a)
        assertNull(b)
    }

    @Test
    fun `isDependencyInScope returns true for registered class`() {
        scope.createDependencyLater(String::class) { "exists" }
        assertTrue(scope.isDependencyInScope(String::class))
    }

    @Test
    fun `isDependencyInScope returns false for unregistered class`() {
        assertFalse(scope.isDependencyInScope(String::class))
    }

    @Test
    fun `isDependencyInScope returns true for created now class`() {
        scope.createDependencyNow(Int::class) { 42 }
        assertTrue(scope.isDependencyInScope(Int::class))
    }

    @Test
    fun `isDependencyInScope returns false after delete`() {
        scope.createDependencyNow(String::class) { "temp" }
        scope.delete(String::class)
        assertFalse(scope.isDependencyInScope(String::class))
    }

    @Test
    fun `close clears all factories and caches`() {
        scope.createDependencyNow(String::class) { "a" }
        scope.createDependencyLater(Int::class) { 1 }
        scope.getNullableDependency<Int>(Int::class)
        scope.close()
        val a: String? = scope.getNullableDependency(String::class)
        val b: Int? = scope.getNullableDependency(Int::class)
        assertNull(a)
        assertNull(b)
        assertFalse(scope.isDependencyInScope(String::class))
        assertFalse(scope.isDependencyInScope(Int::class))
    }
}
