package info.p2sh.bitcoink.utils

import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class ByteExtensionsTest {
    @Test
    fun testVarint() {
        val value = 358385749458385L

        val serialized = ByteArrayOutputStream().use { output ->
            output.writeVarInt(value)
            output.toByteArray()
        }

        val deserialized = ByteArrayInputStream(serialized).use { input ->
            input.readVarInt()
        }

        Assert.assertEquals(deserialized, value)
    }

    @Test
    fun testUint64() {
        val value = 358385749458385L

        val serialized = ByteArrayOutputStream().use { output ->
            output.writeUint64(value)
            output.toByteArray()
        }

        Assert.assertEquals(8, serialized.size)

        val deserialized = ByteArrayInputStream(serialized).use { input ->
            input.readUint64()
        }

        Assert.assertEquals(deserialized, value)
    }

    @Test
    fun testUint32() {
        fun testInt(int: Long) {
            val serialized = ByteArrayOutputStream().use { output ->
                output.writeUint32(int)
                output.toByteArray()
            }

            Assert.assertEquals(4, serialized.size)

            val deserialized = ByteArrayInputStream(serialized).use { input ->
                input.readUint32()
            }

            Assert.assertEquals(deserialized, int)
        }

        testInt(0)
        testInt(1)
        testInt(129)
        testInt(204939583)
        testInt(3945678963)
        testInt(4294967295)
    }

    @Test
    fun testUint16() {
        val int = 3583

        val serialized = ByteArrayOutputStream().use { output ->
            output.writeUint16(int)
            output.toByteArray()
        }

        Assert.assertEquals(2, serialized.size)

        val deserialized = ByteArrayInputStream(serialized).use { input ->
            input.readUint16()
        }

        Assert.assertEquals(deserialized, int)
    }
}