package dev.elysium.eracescast.client;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ERacesPayload(Long timestamp, String type, String payload) implements CustomPayload {
    public static final CustomPayload.Id<ERacesPayload> ID = new CustomPayload.Id<>(Identifier.of("elysium", "eraces_cast"));

    public static final PacketCodec<RegistryByteBuf, ERacesPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.LONG, ERacesPayload::timestamp,
            PacketCodecs.STRING, ERacesPayload::type,
            PacketCodecs.STRING, ERacesPayload::payload,
            ERacesPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
