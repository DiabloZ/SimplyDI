package su.vi.simply.di.core

import org.junit.*
import org.junit.Assert.*

class LoggingTest {

    @Test
    fun `LogLevel values`() {
        assertEquals(3, LogLevel.values().size)
        assertEquals("DEBUG", LogLevel.DEBUG.name)
        assertEquals("ERROR", LogLevel.ERROR.name)
        assertEquals("WTF", LogLevel.WTF.name)
    }

    @Test
    fun `LogCallback can be null`() {
        val callback: LogCallback? = null
        assertNull(callback)
    }

    @Test
    fun `LogCallback can be non-null`() {
        var captured = false
        val callback: LogCallback = { _, _, _ -> captured = true }
        callback(LogLevel.DEBUG, "tag", "msg")
        assertTrue(captured)
    }
}
