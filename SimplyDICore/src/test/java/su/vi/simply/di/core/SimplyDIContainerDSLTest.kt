package su.vi.simply.di.core

import org.junit.*
import org.junit.Assert.*
import su.vi.simply.di.core.entry_point.*
import su.vi.simply.di.core.lazy.SimplyDILazyWrapper

class SimplyDIContainerDSLTest {

    @Test
    fun `DSL builder creates container`() {
        val container = SimplyDIContainer("dsl-test", true)
        container.initialize("dsl-test")
        container.addDependencyNow<String>("dsl-test") { "builder" }
        assertEquals("builder", container.getDependency<String>("dsl-test"))
    }

    @Test
    fun `DSL addDependencyNow via extensions`() {
        val container = SimplyDIContainer("dsl-now", true)
        container.initialize("dsl-now")
        container.addDependencyNow<Int>("dsl-now") { 42 }
        assertEquals(42, container.getDependency<Int>("dsl-now"))
    }

    @Test
    fun `DSL addDependencyLater via extensions`() {
        val container = SimplyDIContainer("dsl-later", true)
        container.initialize("dsl-later")
        container.addDependencyLater<String>("dsl-later") { "lazy" }
        assertEquals("lazy", container.getDependency<String>("dsl-later"))
    }

    @Test
    fun `DSL replaceDependencyNow via extensions`() {
        val container = SimplyDIContainer("dsl-replace", true)
        container.initialize("dsl-replace")
        container.addDependencyNow<String>("dsl-replace") { "old" }
        container.replaceDependencyNow<String>("dsl-replace") { "new" }
        assertEquals("new", container.getDependency<String>("dsl-replace"))
    }

    @Test
    fun `DSL replaceDependencyLater via extensions`() {
        val container = SimplyDIContainer("dsl-replace-later", true)
        container.initialize("dsl-replace-later")
        container.addDependencyLater<String>("dsl-replace-later") { "old" }
        container.replaceDependencyLater<String>("dsl-replace-later") { "new" }
        assertEquals("new", container.getDependency<String>("dsl-replace-later"))
    }

    @Test
    fun `DSL getDependency via extensions`() {
        val container = SimplyDIContainer("dsl-get", true)
        container.initialize("dsl-get")
        container.addDependencyNow<String>("dsl-get") { "get" }
        assertEquals("get", container.getDependency<String>("dsl-get"))
    }

    @Test
    fun `DSL getDependencyByLazy via extensions`() {
        val container = SimplyDIContainer("dsl-lazy-get", true)
        container.initialize("dsl-lazy-get")
        container.addDependencyLater<Int>("dsl-lazy-get") { 99 }
        val wrapper: SimplyDILazyWrapper<Int> = container.getDependencyByLazy("dsl-lazy-get", Int::class)
        assertEquals(99, wrapper())
    }

    @Test
    fun `DSL getFactoryDependency via extensions`() {
        val container = SimplyDIContainer("dsl-factory", true)
        container.initialize("dsl-factory")
        container.addDependencyLater<Int>("dsl-factory") { 100 }
        val a = container.getFactoryDependency<Int>("dsl-factory")
        val b = container.getFactoryDependency<Int>("dsl-factory")
        assertEquals(100, a)
        assertEquals(100, b)
    }

    @Test
    fun `DSL deleteDependency via extensions`() {
        val container = SimplyDIContainer("dsl-del", true)
        container.initialize("dsl-del")
        container.addDependencyNow<String>("dsl-del") { "del" }
        container.deleteDependency<String>("dsl-del")
        try {
            container.getDependency<String>("dsl-del")
            fail()
        } catch (e: Exception) {
            assertTrue(e is su.vi.simply.di.core.error.SimplyDINotFoundException)
        }
    }

    @Test
    fun `DSL addChainScopes via extensions`() {
        val container = SimplyDIContainer("dsl-chain", true)
        container.initialize("dsl-chain")
        container.addChainScopes(listOf("dsl-chain", "other"))
    }

    @Test
    fun `DSL deleteChainedScopes via extensions`() {
        val container = SimplyDIContainer("dsl-del-chain", true)
        container.initialize("dsl-del-chain")
        container.addChainScopes(listOf("dsl-del-chain", "other"))
        container.deleteChainedScopes(listOf("dsl-del-chain", "other"))
    }

    @Test
    fun `DSL defaults scopeName`() {
        val container = SimplyDIContainer()
        container.initialize()
        container.addDependencyNow<String> { "default" }
        assertEquals("default", container.getDependency<String>())
    }

    @Test
    fun `DSL with custom isSearchInScope`() {
        val container = SimplyDIContainer("dsl-search", false)
        container.initialize("dsl-search")
        container.addDependencyNow<String>("dsl-search") { "no-search" }
        assertEquals("no-search", container.getDependency<String>("dsl-search"))
    }
}
