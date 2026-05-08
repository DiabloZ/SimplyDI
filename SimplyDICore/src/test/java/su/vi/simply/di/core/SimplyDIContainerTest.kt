package su.vi.simply.di.core

import org.junit.*
import org.junit.Assert.*

class SimplyDIContainerTest {

    private lateinit var container: SimplyDIContainer

    @Before
    fun setup() {
        container = SimplyDIContainer(scopeName = "test")
        container.initialize("test")
    }

    @Test
    fun `constructor defaults`() {
        val c = SimplyDIContainer()
        assertEquals("MAIN CONTAINER", c.scopeName)
        assertFalse(c.isSearchInScope)
    }

    @Test
    fun `constructor with parameters`() {
        val c = SimplyDIContainer(scopeName = "custom", isSearchInScope = false)
        assertEquals("custom", c.scopeName)
        assertFalse(c.isSearchInScope)
    }

    @Test
    fun `initialize creates scope`() {
        val c = SimplyDIContainer(scopeName = "init-test", isSearchInScope = true)
        c.initialize("init-test")
        c.addDependencyNow("init-test", String::class) { "hello" }
        assertEquals("hello", c.getDependency(kClass = String::class, scopeName = "init-test"))
    }

    @Test
    fun `initialize already existing scope does not crash`() {
        container.initialize("test")
    }

    @Test
    fun `addDependencyNow stores and retrieves`() {
        container.addDependencyNow("test", String::class) { "now" }
        assertEquals("now", container.getDependency(kClass = String::class, scopeName = "test"))
    }

    @Test
    fun `addDependencyLater stores factory`() {
        container.addDependencyLater("test", Int::class) { 42 }
        assertEquals(42, container.getDependency(kClass = Int::class, scopeName = "test"))
    }

    @Test
    fun `addDependencyNow returns same cached instance`() {
        container.addDependencyNow("test", String::class) { "v" }
        val a: String = container.getDependency(kClass = String::class, scopeName = "test")
        val b: String = container.getDependency(kClass = String::class, scopeName = "test")
        assertSame(a, b)
    }

    @Test
    fun `addDependencyLater returns same cached instance after first call`() {
        var count = 0
        container.addDependencyLater("test", String::class) { "v${++count}" }
        val a: String = container.getDependency(kClass = String::class, scopeName = "test")
        val b: String = container.getDependency(kClass = String::class, scopeName = "test")
        assertSame(a, b)
        assertEquals("v1", a)
    }

    @Test
    fun `addDependencyNow prevents duplicate addition`() {
        container.addDependencyNow("test", String::class) { "first" }
        container.addDependencyNow("test", String::class) { "second" }
        assertEquals("first", container.getDependency(kClass = String::class, scopeName = "test"))
    }

    @Test
    fun `addDependencyLater prevents duplicate addition`() {
        container.addDependencyLater("test", String::class) { "first" }
        container.addDependencyLater("test", String::class) { "second" }
        assertEquals("first", container.getDependency(kClass = String::class, scopeName = "test"))
    }

    @Test
    fun `replaceDependencyNow replaces existing`() {
        container.addDependencyNow("test", String::class) { "old" }
        container.replaceDependencyNow("test", String::class) { "new" }
        assertEquals("new", container.getDependency(kClass = String::class, scopeName = "test"))
    }

    @Test
    fun `replaceDependencyLater replaces existing`() {
        container.addDependencyLater("test", String::class) { "old" }
        container.replaceDependencyLater("test", String::class) { "new" }
        assertEquals("new", container.getDependency(kClass = String::class, scopeName = "test"))
    }

    @Test
    fun `getDependency throws when not found`() {
        try {
            val result: String = container.getDependency(kClass = String::class, scopeName = "test")
            fail("Expected SimplyDINotFoundException")
        } catch (e: Exception) {
            assertTrue(e is su.vi.simply.di.core.error.SimplyDINotFoundException)
        }
    }

    @Test
    fun `getDependency returns value from cache`() {
        container.addDependencyNow("test", Int::class) { 100 }
        assertEquals(100, container.getDependency(kClass = Int::class, scopeName = "test"))
    }

    @Test
    fun `getDependencyByLazy returns wrapper`() {
        container.addDependencyLater("test", String::class) { "lazy" }
        val wrapper: su.vi.simply.di.core.lazy.SimplyDILazyWrapper<String> = container.getDependencyByLazy(scopeName = "test", kClass = String::class)
        assertEquals("lazy", wrapper())
    }

    @Test
    fun `getFactoryDependency creates new instance each time`() {
        var count = 0
        container.addDependencyLater("test", Int::class) { ++count }
        val a: Int = container.getFactoryDependency(scopeName = "test", kClass = Int::class)
        val b: Int = container.getFactoryDependency(scopeName = "test", kClass = Int::class)
        assertNotEquals(a, b)
    }

    @Test
    fun `getByClassAnyway finds in current scope cache`() {
        container.addDependencyNow("test", String::class) { "cached" }
        assertEquals("cached", container.getByClassAnyway(scopeName = "test", kClass = String::class))
    }

    @Test
    fun `getByClassAnyway throws when not found`() {
        try {
            val result: String = container.getByClassAnyway(scopeName = "test", kClass = String::class)
            fail("Expected SimplyDINotFoundException")
        } catch (e: Exception) {
            assertTrue(e is su.vi.simply.di.core.error.SimplyDINotFoundException)
        }
    }

    @Test
    fun `deleteDependency removes from scope`() {
        container.addDependencyNow("test", String::class) { "will delete" }
        container.deleteDependency(scopeName = "test", kClass = String::class)
        try {
            val result: String = container.getDependency(kClass = String::class, scopeName = "test")
            fail("Expected SimplyDINotFoundException")
        } catch (e: Exception) {
            assertTrue(e is su.vi.simply.di.core.error.SimplyDINotFoundException)
        }
    }

    @Test
    fun `addChainScopes finds dependency in local chain scope`() {
        container.createScope("other")
        container.addDependencyNow(scopeName = "other", kClass = Int::class, factory = { 99 })
        container.addChainScopes(listOf("test", "other"))
        val result: Int = container.getDependency(kClass = Int::class, scopeName = "test")
        assertEquals(99, result)
    }

    @Test
    fun `deleteChainedScopes removes chain lookup`() {
        container.createScope("other")
        container.addDependencyNow(scopeName = "other", kClass = Int::class, factory = { 99 })
        container.addChainScopes(listOf("test", "other"))
        container.deleteChainedScopes(listOf("test", "other"))
        try {
            val _result: Int = container.getDependency(kClass = Int::class, scopeName = "test")
            fail("Expected SimplyDINotFoundException")
        } catch (e: Exception) {
            assertTrue(e is su.vi.simply.di.core.error.SimplyDINotFoundException)
        }
    }

    @Test
    fun `cross-container chain lookup works`() {
        val containerA = SimplyDIContainer(scopeName = "moduleA", isSearchInScope = true)
        containerA.initialize("moduleA")
        containerA.addDependencyNow(scopeName = "moduleA", kClass = String::class, factory = { "from A" })

        val containerB = SimplyDIContainer(scopeName = "moduleB", isSearchInScope = true)
        containerB.initialize("moduleB")
        containerB.addDependencyNow(scopeName = "moduleB", kClass = Int::class, factory = { 42 })

        containerA.addChainScopes(listOf("moduleA", "moduleB"))
        containerB.addChainScopes(listOf("moduleA", "moduleB"))

        val result: Int = containerA.getDependency(kClass = Int::class, scopeName = "moduleA")
        assertEquals(42, result)

        val result2: String = containerB.getDependency(kClass = String::class, scopeName = "moduleB")
        assertEquals("from A", result2)
    }

    @Test
    fun `unlinked third container cannot access chain dependencies`() {
        val containerA = SimplyDIContainer(scopeName = "moduleA", isSearchInScope = true)
        containerA.initialize("moduleA")
        containerA.addDependencyNow(scopeName = "moduleA", kClass = String::class, factory = { "from A" })

        val containerB = SimplyDIContainer(scopeName = "moduleB", isSearchInScope = true)
        containerB.initialize("moduleB")
        containerB.addDependencyNow(scopeName = "moduleB", kClass = Int::class, factory = { 42 })

        containerA.addChainScopes(listOf("moduleA", "moduleB"))
        containerB.addChainScopes(listOf("moduleA", "moduleB"))

        val containerC = SimplyDIContainer(scopeName = "moduleC", isSearchInScope = true)
        containerC.initialize("moduleC")

        try {
            val _r1: String = containerC.getDependency(kClass = String::class, scopeName = "moduleC")
            fail("Expected SimplyDINotFoundException")
        } catch (e: Exception) {
            assertTrue(e is su.vi.simply.di.core.error.SimplyDINotFoundException)
        }

        try {
            val _r2: Int = containerC.getDependency(kClass = Int::class, scopeName = "moduleC")
            fail("Expected SimplyDINotFoundException")
        } catch (e: Exception) {
            assertTrue(e is su.vi.simply.di.core.error.SimplyDINotFoundException)
        }

        try {
            val _r3: String = containerC.getByClassAnyway(scopeName = "moduleC", kClass = String::class)
            fail("Expected SimplyDINotFoundException")
        } catch (e: Exception) {
            assertTrue(e is su.vi.simply.di.core.error.SimplyDINotFoundException)
        }

        try {
            val _r4: Int = containerC.getByClassAnyway(scopeName = "moduleC", kClass = Int::class)
            fail("Expected SimplyDINotFoundException")
        } catch (e: Exception) {
            assertTrue(e is su.vi.simply.di.core.error.SimplyDINotFoundException)
        }
    }

    @Test
    fun `getDependency throws when scope not initialized`() {
        val c = SimplyDIContainer(scopeName = "uninitialized", isSearchInScope = true)
        try {
            val result: String = c.getDependency(kClass = String::class, scopeName = "uninitialized")
            fail("Expected SimplyDINotFoundException")
        } catch (e: Exception) {
            assertTrue(e is su.vi.simply.di.core.error.SimplyDINotFoundException)
        }
    }

    @Test
    fun `addDependencyNow throws when scope not initialized`() {
        val c = SimplyDIContainer(scopeName = "uninitialized", isSearchInScope = true)
        try {
            c.addDependencyNow(scopeName = "uninitialized", kClass = String::class, factory = { "fail" })
            fail("Expected SimplyDINotFoundException")
        } catch (e: Exception) {
            assertTrue(e is su.vi.simply.di.core.error.SimplyDINotFoundException)
        }
    }

    @Test
    fun `singleton instance auto initialises`() {
        val instance = SimplyDIContainer.instance
        assertNotNull(instance)
        assertEquals("MAIN CONTAINER", instance.scopeName)
    }

    @Test
    fun `singleton instance is same on repeated calls`() {
        val a = SimplyDIContainer.instance
        val b = SimplyDIContainer.instance
        assertSame(a, b)
    }

    @Test
    fun `createScope creates new scope`() {
        val c = SimplyDIContainer()
        c.initialize()
        c.createScope("newScope")
        c.addDependencyNow(scopeName = "newScope", kClass = String::class, factory = { "new scope dep" })
        assertEquals("new scope dep", c.getDependency(kClass = String::class, scopeName = "newScope"))
    }

    @Test
    fun `createScope prevents duplicate`() {
        val c = SimplyDIContainer()
        c.initialize()
        c.createScope("dup")
        c.createScope("dup")
        c.addDependencyNow(scopeName = "dup", kClass = String::class, factory = { "ok" })
        assertEquals("ok", c.getDependency(kClass = String::class, scopeName = "dup"))
    }

    @Test
    fun `closeScope clears scope`() {
        val c = SimplyDIContainer()
        c.initialize("close-test")
        c.addDependencyNow(scopeName = "close-test", kClass = String::class, factory = { "before" })
        c.closeScope("close-test")
        try {
            val result: String = c.getDependency(kClass = String::class, scopeName = "close-test")
            fail("Expected SimplyDINotFoundException")
        } catch (e: Exception) {
            assertTrue(e is su.vi.simply.di.core.error.SimplyDINotFoundException)
        }
    }

    @Test
    fun `closeScope returns closed scope name`() {
        val c = SimplyDIContainer()
        c.initialize("close-ret")
        val result = c.closeScope("close-ret")
        assertEquals(listOf("close-ret"), result)
    }

    @Test
    fun `closeScope on unknown returns empty`() {
        val c = SimplyDIContainer()
        val result = c.closeScope("nonexistent")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getByClassAnyway cache first then factory`() {
        var factoryCount = 0
        container.addDependencyLater("test", Int::class) { ++factoryCount }
        val first: Int = container.getDependency(kClass = Int::class, scopeName = "test")
        val second: Int = container.getByClassAnyway(scopeName = "test", kClass = Int::class)
        assertEquals(first, second)
        assertEquals(1, factoryCount)
    }

    @Test
    fun `getByClassAnyway respects isSearchInScope`() {
        val c1 = SimplyDIContainer(scopeName = "visible", isSearchInScope = true)
        c1.initialize("visible")
        c1.addDependencyNow("visible", String::class) { "visible" }
        assertEquals("visible", c1.getByClassAnyway(scopeName = "visible", kClass = String::class))
    }

    @Test
    fun `multiple dependencies in same scope`() {
        container.addDependencyNow("test", String::class) { "str" }
        container.addDependencyNow("test", Int::class) { 42 }
        container.addDependencyLater("test", Boolean::class) { true }
        assertEquals("str", container.getDependency(kClass = String::class, scopeName = "test"))
        assertEquals(42, container.getDependency(kClass = Int::class, scopeName = "test"))
        assertEquals(true, container.getDependency(kClass = Boolean::class, scopeName = "test"))
    }

    @Test
    fun `getDependencyByLazy cached on multiple accesses`() {
        var count = 0
        container.addDependencyLater("test", Int::class) { ++count }
        val lazy: su.vi.simply.di.core.lazy.SimplyDILazyWrapper<Int> = container.getDependencyByLazy(scopeName = "test", kClass = Int::class)
        assertEquals(1, lazy())
        assertEquals(1, lazy())
        assertEquals(1, count)
    }

    @Test
    fun `getFactoryDependency throws when scope not initialized`() {
        val c = SimplyDIContainer(scopeName = "nope", isSearchInScope = true)
        try {
            val result: String = c.getFactoryDependency(scopeName = "nope", kClass = String::class)
            fail("Expected SimplyDINotFoundException")
        } catch (e: Exception) {
            assertTrue(e is su.vi.simply.di.core.error.SimplyDINotFoundException)
        }
    }

    @Test
    fun `getFactoryDependency throws when factory not found`() {
        try {
            val result: String = container.getFactoryDependency(scopeName = "test", kClass = String::class)
            fail("Expected SimplyDINotFoundException")
        } catch (e: Exception) {
            assertTrue(e is su.vi.simply.di.core.error.SimplyDINotFoundException)
        }
    }
}
