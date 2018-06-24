package info.p2sh.bitcoink.datastructures

import info.p2sh.bitcoink.utils.readUint64
import info.p2sh.bitcoink.utils.readVarInt
import info.p2sh.bitcoink.utils.writeUint64
import info.p2sh.bitcoink.utils.writeVarInt
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

data class Output(
    val value: Long,
    val script: ByteArray
) {
    fun toByteArray(): ByteArray {
        return ByteArrayOutputStream().use {
            it.writeUint64(value)
            it.writeVarInt(script.size.toLong())
            it.write(script)
            it.toByteArray()
        }
    }

    companion object {
        fun fromHexStream(hexStream: ByteArrayInputStream): Output {
            val value = hexStream.readUint64()
            val scriptLength = hexStream.readVarInt()
            val script = ByteArray(scriptLength.toInt())
            hexStream.read(script)

            return Output(value, script)
        }
    }
}