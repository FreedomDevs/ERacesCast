package dev.elysium.eracescast.mangers

import dev.elysium.eblanexp.EBlanexpMain.Companion.LOGGER
import dev.elysium.eracescast.datatypes.Ability
import dev.elysium.eracescast.packets.ERacesPayload
import kotlinx.serialization.json.Json
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry

object PacketSendManager {
    var abilities: List<Ability>? = null
        private set

    fun initPackets() {
        PayloadTypeRegistry.playC2S().register(ERacesPayload.ID, ERacesPayload.CODEC)
        PayloadTypeRegistry.playS2C().register(ERacesPayload.ID, ERacesPayload.CODEC)

        ClientPlayNetworking.registerGlobalReceiver(ERacesPayload.ID) { packet, context ->
            context.client().execute {
                if (packet.type == "abilities_list") {
                    abilities = Json.decodeFromString<List<Ability>>(packet.payload)
                } else{
                    LOGGER.info(packet.toString())
                }
            }
        }
    }

    fun sendPacket(type: String, payload: String) {
        val packet = ERacesPayload(type, payload)
        ClientPlayNetworking.send(packet)
    }


    fun sendAbility(id: String) {
        sendPacket("activate_ability", id)
    }
}