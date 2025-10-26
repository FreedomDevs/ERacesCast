package dev.elysium.eracescast.utils

import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

object UTFEncoder : PacketCodec<RegistryByteBuf, String> {
    override fun decode(buf: RegistryByteBuf): String {
        try {
            val data = ByteArray(buf.readableBytes())
            buf.readBytes(data)
            return DataInputStream(ByteArrayInputStream(data)).use { it.readUTF() }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

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
}
