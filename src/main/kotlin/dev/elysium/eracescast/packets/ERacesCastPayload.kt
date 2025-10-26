package dev.elysium.eracescast.packets

import dev.elysium.eracescast.utils.UTFEncoder
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier

data class ERacesCastPayload(
    val timestamp: Long,
    val type: String,
    val payload: String
) : CustomPayload {

    companion object {
        val ID: CustomPayload.Id<ERacesCastPayload> =
            CustomPayload.Id(Identifier.of("elysium", "eraces_cast"))

        val CODEC: PacketCodec<RegistryByteBuf, ERacesCastPayload> = PacketCodec.tuple(
            PacketCodecs.LONG, ERacesCastPayload::timestamp,
            UTFEncoder, ERacesCastPayload::type,
            UTFEncoder, ERacesCastPayload::payload,
            ::ERacesCastPayload
        )
    }

    override fun getId(): CustomPayload.Id<out CustomPayload> = ID
}
