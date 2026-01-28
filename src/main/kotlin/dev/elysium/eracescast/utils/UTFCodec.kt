package dev.elysium.eracescast.utils

import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

object UTFEncoder : PacketCodec<RegistryByteBuf, String> {

    // Encode оставляем как было
    override fun encode(buf: RegistryByteBuf, value: String) {
        try {
            val baos = ByteArrayOutputStream()
            DataOutputStream(baos).use { out ->
                out.writeUTF(value)
                out.flush()
            }
            buf.writeBytes(baos.toByteArray())
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    // Decode фиксируем
    override fun decode(buf: RegistryByteBuf): String {
        try {
            // Берём ровно столько байт, сколько занимает одна строка UTF
            val dis = DataInputStream(ByteArrayInputStream(buf.readBytesForUTF()))
            return dis.readUTF()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    // Вспомогательная функция для безопасного чтения одной UTF-строки
    private fun RegistryByteBuf.readBytesForUTF(): ByteArray {
        // DataInputStream.readUTF() читает 2 байта длины + данные
        // Проверяем, что хотя бы 2 байта доступны
        if (this.readableBytes() < 2) throw IOException("Not enough bytes to read UTF length")
        val lengthBytes = ByteArray(2)
        this.readBytes(lengthBytes)
        val length = ((lengthBytes[0].toInt() and 0xFF) shl 8) or (lengthBytes[1].toInt() and 0xFF)
        if (this.readableBytes() < length) throw IOException("Not enough bytes to read UTF string")
        val dataBytes = ByteArray(length)
        this.readBytes(dataBytes)
        return lengthBytes + dataBytes
    }
}
