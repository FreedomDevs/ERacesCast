package dev.elysium.eracescast.mangers

import dev.elysium.eracescast.ERacesCast.Companion.isNetworkingEnabled
import dev.elysium.eracescast.packets.ERacesCastPayload
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import kotlin.Int

object PacketSendManager {
    fun initPackets() {
        PayloadTypeRegistry.playC2S().register(ERacesCastPayload.ID, ERacesCastPayload.CODEC)
    }

    fun sendCastPacket(type: String, payload: String) {
        val packet = ERacesCastPayload(System.currentTimeMillis(), type, payload)

        if (isNetworkingEnabled) ClientPlayNetworking.send(packet)
    }

    fun sendStartCast() {
        sendCastPacket("start_cast", "")
    }

    fun sendEndCast(keys: List<Int>) {
        sendCastPacket("end_cast", keys.joinToString(separator = ""))
    }

    fun sendKey(key: Int) {
        sendCastPacket("cast_key", key.toString())
    }
}