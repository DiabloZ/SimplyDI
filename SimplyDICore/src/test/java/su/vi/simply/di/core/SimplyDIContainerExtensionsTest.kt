package su.vi.simply.di.core

import org.junit.*
import org.junit.Assert.*
import su.vi.simply.di.core.entry_point.*
import su.vi.simply.di.core.lazy.SimplyDILazyWrapper

class SimplyDIContainerExtensionsTest {

    private lateinit var container: SimplyDIContainer

    @Before
    fun setup() {
        container = SimplyDIContainer(scopeName = "ext-test", isSearchInScope = true)
        container.initialize("ext-test")
    }

    @Test
    fun `addDependencyNow reified`() {
        container.addDependencyNow<String>("ext-test") { "reified" }
        assertEquals("reified", container.getDependency<String>("ext-test"))
    }

    @Test
    fun `addDependencyNow with kclass`() {
        container.addDependencyNow("ext-test", String::class) { "kclass" }
        assertEquals("kclass", container.getDependency("ext-test", String::class))
    }

   @Test
    fun `addDependencyLater reified`() {
        container.addDependencyLater<Int>("ext-test") { 42 }
        assertEquals(42, container.getDependency<Int>("ext-test"))
    }

    @Test
    fun `addDependencyLater with kclass`() {
        container.addDependencyLater("ext-test", Boolean::class) { true }
        assertEquals(true, container.getDependency("ext-test", Boolean::class))
    }

    @Test
    fun `replaceDependencyNow reified`() {
        container.addDependencyNow<String>("ext-test") { "old" }
        container.replaceDependencyNow<String>("ext-test") { "new" }
        assertEquals("new", container.getDependency<String>("ext-test"))
    }

    @Test
    fun `replaceDependencyNow with kclass`() {
        container.addDependencyNow("ext-test", String::class) { "old" }
        container.replaceDependencyNow("ext-test", String::class) { "new" }
        assertEquals("new", container.getDependency("ext-test", String::class))
    }

    @Test
    fun `replaceDependencyLater reified`() {
        container.addDependencyLater<String>("ext-test") { "old" }
        container.replaceDependencyLater<String>("ext-test") { "new" }
        assertEquals("new", container.getDependency<String>("ext-test"))
    }

    @Test
    fun `replaceDependencyLater with kclass`() {
        container.addDependencyLater("ext-test", String::class) { "old" }
        container.replaceDependencyLater("ext-test", String::class) { "new" }
        assertEquals("new", container.getDependency("ext-test", String::class))
    }

    @Test
    fun `getDependency reified`() {
        container.addDependencyNow<String>("ext-test") { "reified-get" }
        assertEquals("reified-get", container.getDependency<String>())
    }

    @Test
    fun `getDependency with kclass`() {
        container.addDependencyNow("ext-test", Int::class) { 99 }
        assertEquals(99, container.getDependency("ext-test", Int::class))
  }

    @Test
    fun `getByClassAnyway reified`() {
        container.addDependencyNow<String>("ext-test") { "anyway" }
        assertEquals("anyway", container.getByClassAnyway<String>("ext-test"))
    }

    @Test
    fun `getByClassAnyway with kclass`() {
        container.addDependencyNow("ext-test", String::class) { "anyway-kclass" }
        assertEquals("anyway-kclass", container.getByClassAnyway("ext-test", String::class))
    }

    @Test
    fun `getByClassAnyway without scopeName`() {
        container.addDependencyNow(String::class) { "anyway-no-scope" }
        assertEquals("anyway-no-scope", container.getByClassAnyway(String::class))
    }

    @Test
    fun `getDependencyByLazy reified`() {
        container.addDependencyLater<String>("ext-test") { "lazy-reified" }
        val wrapper: SimplyDILazyWrapper<String> = container.getDependencyByLazy<String>("ext-test")
        assertEquals("lazy-reified", wrapper())
    }

    @Test
    fun `getDependencyByLazy with kclass`() {
        container.addDependencyLater("ext-test", String::class) { "lazy-kclass" }
        val wrapper: SimplyDILazyWrapper<String> = container.getDependencyByLazy("ext-test", String::class)
        assertEquals("lazy-kclass", wrapper())
    }

    @Test
    fun `getDependencyByLazy without scopeName`() {
        container.addDependencyLater(String::class) { "lazy-no-scope" }
        val wrapper: SimplyDILazyWrapper<String> = container.getDependencyByLazy(String::class)
        assertEquals("lazy-no-scope", wrapper())
    }

    @Test
    fun `getFactoryDependency reified`() {
        container.addDependencyLater<Int>("ext-test") { 42 }
        assertEquals(42, container.getFactoryDependency<Int>("ext-test"))
    }

    @Test
    fun `getFactoryDependency with kclass`() {
        container.addDependencyLater("ext-test", Int::class) { 42 }
        assertEquals(42, container.getFactoryDependency("ext-test", Int::class))
    }

    @Test
    fun `deleteDependency reified`() {
        container.addDependencyNow<String>("ext-test") { "del" }
        container.deleteDependency<String>("ext-test")
        try {
            container.getDependency<String>("ext-test")
            fail()
        } catch (e: Exception) {
            assertTrue(e is su.vi.simply.di.core.error.SimplyDINotFoundException)
        }
    }

    @Test
    fun `deleteDependency with kclass`() {
        container.addDependencyNow("ext-test", String::class) { "del" }
        container.deleteDependency("ext-test", String::class)
        try {
            val result: String = container.getDependency("ext-test", String::class)
            fail()
        } catch (e: Exception) {
            assertTrue(e is su.vi.simply.di.core.error.SimplyDINotFoundException)
        }
    }

    @Test
    fun `deleteDependency without scopeName`() {
        container.addDependencyNow(String::class) { "del" }
        container.deleteDependency(String::class)
        try {
            val result: String = container.getDependency(String::class)
            fail()
        } catch (e: Exception) {
            assertTrue(e is su.vi.simply.di.core.error.SimplyDINotFoundException)
        }
    }

    @Test
    fun `addChainScopes extension`() {
        container.addChainScopes(listOf("ext-test", "other"))
    }

    @Test
    fun `deleteChainedScopes extension`() {
        container.addChainScopes(listOf("ext-test", "other"))
        container.deleteChainedScopes(listOf("ext-test", "other"))
    }

    @Test
    fun `closeScope extension`() {
        container.addDependencyNow<String>("ext-test") { "close" }
        val result = container.closeScope("ext-test")
        assertEquals(listOf("ext-test"), result)
    }

    @Test
    fun `createScope extension`() {
        container.createScope("new-ext", true)
        container.addDependencyNow("new-ext", String::class) { "new" }
        assertEquals("new", container.getDependency<String>("new-ext"))
    }

    @Test
    fun `getOrNull returns value when found`() {
        container.addDependencyNow<String>("ext-test") { "ok" }
        val result = container.getOrNull<String>("ext-test")
        assertEquals("ok", result)
    }

    @Test
    fun `getOrNull returns null when not found`() {
        val result = container.getOrNull<String>("ext-test")
        assertNull(result)
    }

    @Test
    fun `getOrNull with kclass returns value`() {
        container.addDependencyNow("ext-test", Int::class) { 42 }
        val result: Int? = container.getOrNull("ext-test", Int::class)
        assertEquals(42, result)
    }

    @Test
    fun `getOrNull with kclass returns null`() {
        val result: String? = container.getOrNull("ext-test", String::class)
        assertNull(result)
    }

    @Test
    fun `override replaces dependency`() {
        container.addDependencyNow<String>("ext-test") { "old" }
        container.override("ext-test", String::class) { "overridden" }
        assertEquals("overridden", container.getDependency<String>("ext-test"))
    }

    @Test
    fun `override on non-existent dependency`() {
        container.override("ext-test", String::class) { "fresh" }
        assertEquals("fresh", container.getDependency<String>("ext-test"))
    }
}
