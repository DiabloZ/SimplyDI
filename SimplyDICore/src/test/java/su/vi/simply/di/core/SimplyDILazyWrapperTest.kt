package su.vi.simply.di.core

import org.junit.*
import org.junit.Assert.*

class SimplyDILazyWrapperTest {

    @Test
    fun `value is lazily evaluated`() {
        var evaluated = false
        val wrapper = su.vi.simply.di.core.lazy.SimplyDILazyWrapper<String> {
            evaluated = true
            "hello"
        }
        assertFalse(evaluated)
        assertEquals("hello", wrapper.value)
        assertTrue(evaluated)
    }

    @Test
    fun `value is cached after first access`() {
        var count = 0
        val wrapper = su.vi.simply.di.core.lazy.SimplyDILazyWrapper<Int> { ++count }
        val a = wrapper.value
        val b = wrapper.value
        assertEquals(1, count)
        assertSame(a, b)
    }

    @Test
    fun `invoke operator returns cached value`() {
        var count = 0
        val wrapper = su.vi.simply.di.core.lazy.SimplyDILazyWrapper<Int> { ++count }
        val a = wrapper()
        val b = wrapper()
        assertEquals(1, count)
        assertSame(a, b)
    }

    @Test
    fun `value and invoke return same reference`() {
        var count = 0
        val wrapper = su.vi.simply.di.core.lazy.SimplyDILazyWrapper<String> {
            count++
            "shared"
        }
        assertSame(wrapper.value, wrapper())
        assertEquals(1, count)
    }

    @Test
    fun `value throws when factory throws`() {
        val wrapper = su.vi.simply.di.core.lazy.SimplyDILazyWrapper<String> { throw RuntimeException("boom") }
        try {
            wrapper.value
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals("boom", e.message)
        }
    }
}
