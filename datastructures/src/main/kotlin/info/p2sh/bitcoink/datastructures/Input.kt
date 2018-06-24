package info.p2sh.bitcoink.datastructures

import info.p2sh.bitcoink.utils.readUint32
import info.p2sh.bitcoink.utils.readVarInt
import info.p2sh.bitcoink.utils.writeUint32
import info.p2sh.bitcoink.utils.writeVarInt
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

data class Input(
    val transaction: Sha256Hash,
    val index: Long,
    val script: ByteArray,
    val sequenceNumber: Long,
    val witness: List<ByteArray> = emptyList()
) {
    fun toByteArray(): ByteArray {
        return ByteArrayOutputStream().use {
            it.write(transaction.hash.reversedArray())
            it.writeUint32(index)
            it.writeVarInt(script.size.toLong())
            it.write(script)
            it.writeUint32(sequenceNumber)

            it.toByteArray()
        }
    }

    companion object {
        fun fromHexStream(hexStream: ByteArrayInputStream): Input {
            val transaction = ByteArray(32)
            hexStream.read(transaction)
            val index = hexStream.readUint32()
            val scriptLength = hexStream.readVarInt()
            val script = ByteArray(scriptLength.toInt())
            hexStream.read(script)
            val sequenceNumber = hexStream.readUint32()

            return Input(
                Sha256Hash(transaction.reversedArray()),
                index,
                script,
                sequenceNumber
            )
        }
    }
}