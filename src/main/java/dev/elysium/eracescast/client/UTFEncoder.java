package dev.elysium.eracescast.client;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.io.*;

public class UTFEncoder implements PacketCodec<RegistryByteBuf, String> {
    public static final UTFEncoder INSTANCE = new UTFEncoder();

    @Override
    public String decode(RegistryByteBuf buf) {
        try {
            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
            return in.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void encode(RegistryByteBuf buf, String value) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.writeUTF(value);
            out.flush();
            buf.writeBytes(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
