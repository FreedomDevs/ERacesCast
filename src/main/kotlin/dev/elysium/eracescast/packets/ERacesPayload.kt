package dev.elysium.eracescast.packets

import dev.elysium.eracescast.utils.UTFEncoder
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier

data class ERacesPayload(
    val type: String,
    val payload: String
) : CustomPayload {

    companion object {
        val ID: CustomPayload.Id<ERacesPayload> =
            CustomPayload.Id(Identifier.of("elysium", "eraces_cast"))

        val CODEC: PacketCodec<RegistryByteBuf, ERacesPayload> = PacketCodec.tuple(
            UTFEncoder, ERacesPayload::type,
            UTFEncoder, ERacesPayload::payload,
            ::ERacesPayload
        )
    }

    override fun getId(): CustomPayload.Id<out CustomPayload> = ID
}
