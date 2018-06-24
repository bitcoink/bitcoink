package info.p2sh.bitcoink.datastructures

import info.p2sh.bitcoink.utils.readUint32
import info.p2sh.bitcoink.utils.writeUint32
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

data class BlockHeader(
    val version: Long,
    val previousBlock: Sha256Hash,
    val merkleRoot: Sha256Hash,
    val timestamp: Long,
    val bits: Long,
    val nonce: Long
) {
    val difficulty: Double by lazy {
        var nShift: Long = (bits shr 24) and 0xff

        var dDiff: Double = 0x0000ffff.toDouble() / (bits and 0x00ffffff).toDouble()

        while (nShift < 29) {
            dDiff *= 256.0
            nShift++
        }

        while (nShift > 29) {
            dDiff /= 256.0
            nShift--
        }

        dDiff
    }

    fun toByteArray(): ByteArray {
        return ByteArrayOutputStream(80).use {
            it.writeUint32(version)
            it.write(previousBlock.hash.reversedArray())
            it.write(merkleRoot.hash.reversedArray())
            it.writeUint32(timestamp)
            it.writeUint32(bits)
            it.writeUint32(nonce)

            it.toByteArray()
        }
    }

    companion object {
        fun fromHex(hex: ByteArray): BlockHeader {
            if (hex.size != 80) {
                throw Exception("Invalid hex size (${hex.size} != 80)")
            }

            val headerBuffer = ByteBuffer.wrap(hex)
            val version = headerBuffer.readUint32()
            val prevBlock = ByteArray(32)
            headerBuffer.get(prevBlock)
            val merkleRoot = ByteArray(32)
            headerBuffer.get(merkleRoot)
            val time = headerBuffer.readUint32()
            val bits = headerBuffer.readUint32()
            val nonce = headerBuffer.readUint32()

            return BlockHeader(
                version,
                Sha256Hash(prevBlock.reversedArray()),
                Sha256Hash(merkleRoot.reversedArray()),
                time,
                bits,
                nonce
            )
        }
    }
}