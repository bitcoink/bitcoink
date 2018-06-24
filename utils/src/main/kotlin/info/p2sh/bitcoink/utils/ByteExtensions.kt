package info.p2sh.bitcoink.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

private fun sizeOf(value: Long): Int {
    // if negative, it's actually a very large unsigned long value
    if (value < 0) return 9 // 1 marker + 8 data bytes
    if (value < 253) return 1 // 1 data byte
    if (value <= 0xFFFFL) return 3 // 1 marker + 2 data bytes
    return if (value <= 0xFFFFFFFFL) 5 else 9 // 1 marker + 4 data bytes or 1 marker + 8 data bytes
}

fun ByteArrayInputStream.readVarInt(): Long {
    val first = 0xFF and read()
    return when {
        first < 253 -> first.toLong()
        first == 253 -> (0xFF and read() or (0xFF and read() shl 8)).toLong()
        first == 254 -> readUint32()
        else -> readUint64()
    }
}

fun ByteArrayOutputStream.writeVarInt(varint: Long) {
    when (sizeOf(varint)) {
        1 -> write(varint.toInt())
        3 -> write(byteArrayOf(253.toByte(), varint.toByte(), (varint shr 8).toByte()))
        5 -> {
            write(254)
            writeUint32(varint)
        }
        else -> {
            write(255)
            writeUint64(varint)
        }
    }
}

fun ByteBuffer.readUint32(): Long {
    return get().toLong() and 0xff or
        (get().toLong() and 0xff shl 8) or
        (get().toLong() and 0xff shl 16) or
        (get().toLong() and 0xff shl 24)
}

fun ByteArrayInputStream.readUint32(): Long {
    val data = ByteArray(4)
    read(data)
    return data[0].toLong() and 0xff or
        (data[1].toLong() and 0xff shl 8) or
        (data[2].toLong() and 0xff shl 16) or
        (data[3].toLong() and 0xff shl 24)
}

fun ByteArrayOutputStream.writeUint32(uint: Long) {
    write(0xff and uint.toInt())
    write(0xff and (uint.toInt() shr 8))
    write(0xff and (uint.toInt() shr 16))
    write(0xff and (uint.toInt() shr 24))
}

fun ByteArrayInputStream.readUint16(): Int {
    return read() and 0xff or
        (read() and 0xff shl 8)
}

fun ByteArrayOutputStream.writeUint16(uint: Int) {
    write(0xFF and uint)
    write(0xFF and (uint shr 8))
}

fun ByteArrayInputStream.readUint64(): Long {
    return read().toLong() and 0xffL or
        (read().toLong() and 0xffL shl 8) or
        (read().toLong() and 0xffL shl 16) or
        (read().toLong() and 0xffL shl 24) or
        (read().toLong() and 0xffL shl 32) or
        (read().toLong() and 0xffL shl 40) or
        (read().toLong() and 0xffL shl 48) or
        (read().toLong() and 0xffL shl 56)
}

fun ByteArrayOutputStream.writeUint64(uint: Long) {
    write((0xFFL and uint).toInt())
    write((0xFFL and (uint shr 8)).toInt())
    write((0xFFL and (uint shr 16)).toInt())
    write((0xFFL and (uint shr 24)).toInt())
    write((0xFFL and (uint shr 32)).toInt())
    write((0xFFL and (uint shr 40)).toInt())
    write((0xFFL and (uint shr 48)).toInt())
    write((0xFFL and (uint shr 56)).toInt())
}