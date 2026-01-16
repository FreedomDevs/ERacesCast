package dev.elysium.eracescast

import dev.elysium.eracescast.mangers.CastManager
import dev.elysium.eracescast.mangers.HudManager
import dev.elysium.eracescast.mangers.PacketSendManager
import dev.elysium.eracescast.mangers.SlotLockingManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object ERacesCast {
    fun onInitializeClient() {
        val args = FabricLoader.getInstance().getLaunchArguments(true)
        isNetworkingEnabled = !listOf(*args).contains("--guiTest")

        PacketSendManager.initPackets()
        SlotLockingManager.initListener()
        HudManager.initHudManager()
        CastManager.initListeners()
    }

    var isNetworkingEnabled: Boolean = true
}