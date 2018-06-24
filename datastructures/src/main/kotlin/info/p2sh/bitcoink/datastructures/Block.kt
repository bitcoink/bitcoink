package info.p2sh.bitcoink.datastructures

import info.p2sh.bitcoink.utils.readVarInt
import info.p2sh.bitcoink.utils.writeVarInt
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

data class Block(
    val header: BlockHeader,
    val transactions: List<Transaction>
) {
    val bytes: ByteArray by lazy {
        ByteArrayOutputStream().use {
            it.write(header.toByteArray())

            it.writeVarInt(transactions.size.toLong())

            transactions.forEach { tx ->
                it.write(tx.bytes)
            }

            it.toByteArray()
        }
    }

    val size: Int by lazy { bytes.size }

    val hash: Sha256Hash by lazy {
        Sha256Hash.fromByteArray(header.toByteArray())
    }

    companion object {
        fun fromHex(hex: ByteArray): Block {
            ByteArrayInputStream(hex).use { stream ->
                val headerData = ByteArray(80)
                stream.read(headerData)
                val header = BlockHeader.fromHex(headerData)
                val nTransactions = stream.readVarInt()
                val transactions = (0 until nTransactions).map { Transaction.fromHexStream(stream) }

                return Block(
                    header,
                    transactions
                )
            }
        }
    }
}