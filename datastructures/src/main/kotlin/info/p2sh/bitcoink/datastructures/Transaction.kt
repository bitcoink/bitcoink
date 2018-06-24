package info.p2sh.bitcoink.datastructures

import info.p2sh.bitcoink.utils.readUint32
import info.p2sh.bitcoink.utils.readVarInt
import info.p2sh.bitcoink.utils.writeUint32
import info.p2sh.bitcoink.utils.writeVarInt
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.xml.bind.DatatypeConverter

data class Transaction(
    val version: Long,
    val inputs: List<Input>,
    val outputs: List<Output>,
    val locktime: Long
) {
    /**
     * Coinbase transactions spend from one input, whose hash it zero-filled
     */
    val isCoinbase: Boolean by lazy {
        this.inputs[0].transaction.hash.contentEquals(ByteArray(32))
    }

    fun serialize(stripped: Boolean = true): ByteArray {
        return ByteArrayOutputStream().use {
            it.writeUint32(version)

            if (!stripped && inputs.any { it.witness.isNotEmpty() }) {
                it.write(marker)
            }

            it.writeVarInt(inputs.size.toLong())
            inputs.forEach { input ->
                it.write(input.toByteArray())
            }

            it.writeVarInt(outputs.size.toLong())
            outputs.forEach { output ->
                it.write(output.toByteArray())
            }

            if (!stripped && inputs.any { it.witness.isNotEmpty() }) {
                inputs.forEach { input ->
                    it.writeVarInt(input.witness.size.toLong())
                    input.witness.forEach { witness ->
                        it.writeVarInt(witness.size.toLong())
                        it.write(witness)
                    }
                }
            }

            it.writeUint32(locktime)

            it.toByteArray()
        }
    }

    val bytes: ByteArray by lazy { serialize() }

    val txid: Sha256Hash by lazy {
        Sha256Hash.fromByteArray(bytes)
    }

    val weight: Int by lazy {
        3 * serialize(stripped = true).size + serialize(stripped = false).size
    }

    companion object {
        private val marker = DatatypeConverter.parseHexBinary("0001")

        fun fromHex(hex: ByteArray): Transaction {
            return fromHexStream(ByteArrayInputStream(hex))
        }

        fun fromHexStream(hexStream: ByteArrayInputStream): Transaction {
            val version = hexStream.readUint32()

            // If the transaction uses SegWit, the next two following bytes will
            // be 0001, if not, it's the number of inputs
            hexStream.mark(4)
            val segwitMarker = ByteArray(2)
            hexStream.read(segwitMarker)
            val usesSegwit = segwitMarker.contentEquals(marker)
            if (!usesSegwit) {
                hexStream.reset()
            }

            val nInputs = hexStream.readVarInt()
            val inputs = (0 until nInputs).map { Input.fromHexStream(hexStream) }

            val nOutputs = hexStream.readVarInt()
            val outputs = (0 until nOutputs).map { Output.fromHexStream(hexStream) }

            // If the transaction uses SegWit, the witnesses are here
            val witnesses = if (usesSegwit) {
                (0 until nInputs).map {
                    val nWitnesses = hexStream.readVarInt()
                    (0 until nWitnesses).map {
                        val length = hexStream.readVarInt()
                        val witness = ByteArray(length.toInt())
                        hexStream.read(witness)
                        witness
                    }
                }
            } else {
                (0 until nInputs).map { emptyList<ByteArray>() }
            }

            // Edit inputs to set the correct witness
            val editedInputs = inputs.mapIndexed { index, input ->
                input.copy(witness = witnesses[index])
            }

            val locktime = hexStream.readUint32()

            return Transaction(
                version,
                editedInputs,
                outputs,
                locktime
            )
        }
    }
}