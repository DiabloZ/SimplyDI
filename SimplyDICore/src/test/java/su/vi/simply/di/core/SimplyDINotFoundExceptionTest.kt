package su.vi.simply.di.core

import org.junit.*
import org.junit.Assert.*

class SimplyDINotFoundExceptionTest {

    @Test
    fun `extends RuntimeException`() {
        val ex = su.vi.simply.di.core.error.SimplyDINotFoundException("msg")
        assertTrue(ex is RuntimeException)
    }

    @Test
    fun `message is preserved`() {
        val ex = su.vi.simply.di.core.error.SimplyDINotFoundException("custom message")
        assertEquals("custom message", ex.message)
    }

    @Test
    fun `can be caught as RuntimeException`() {
        val ex = su.vi.simply.di.core.error.SimplyDINotFoundException("catch test")
        try {
            throw ex
            fail()
        } catch (e: RuntimeException) {
            assertEquals("catch test", e.message)
        }
    }

    @Test
    fun `can be caught as Throwable`() {
        val ex = su.vi.simply.di.core.error.SimplyDINotFoundException("throwable test")
        try {
            throw ex
            fail()
        } catch (e: Throwable) {
            assertEquals("throwable test", e.message)
        }
    }
}
