package info.p2sh.bitcoink.datastructures

import java.security.MessageDigest
import java.util.Arrays
import javax.xml.bind.DatatypeConverter

data class Sha256Hash(
    val hash: ByteArray
) {
    init {
        if (hash.size != 32) {
            throw ExceptionInInitializerError("Invalid hash size (${hash.size} != 32)")
        }
    }

    override fun toString(): String {
        return DatatypeConverter.printHexBinary(hash).toLowerCase()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as Sha256Hash

        if (!Arrays.equals(hash, other.hash)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(hash)
    }

    companion object {
        private val digest = MessageDigest.getInstance("SHA-256")

        fun fromByteArray(input: ByteArray): Sha256Hash {
            digest.reset()
            digest.update(input)
            return Sha256Hash(digest.digest(digest.digest()).reversedArray())
        }
    }
}