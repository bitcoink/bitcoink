package info.p2sh.bitcoink.utils

import org.junit.Assert
import org.junit.Test

class HexExtensionsTest {
    @Test
    fun testHex() {
        val sizes = listOf(
            0, 10, 100, 260, 1_235, 34_959, 1_984_094
        )

        sizes.forEach { size ->
            val data = ByteArray(size, { it.toByte() })
            val str = data.toHex()
            Assert.assertArrayEquals(data, str.fromHex())
        }
    }

    @Test
    fun testStringFormat() {
        val data = ByteArray(4, {
            when (it) {
                0 -> 0xde.toByte()
                1 -> 0xad.toByte()
                2 -> 0xbe.toByte()
                else -> 0xef.toByte()
            }
        })

        Assert.assertEquals("deadbeef", data.toHex())
    }
}