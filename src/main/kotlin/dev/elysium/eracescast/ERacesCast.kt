package dev.elysium.eracescast

import dev.elysium.eracescast.mangers.CastManager
import dev.elysium.eracescast.mangers.HudManager
import dev.elysium.eracescast.mangers.PacketSendManager
import dev.elysium.eracescast.mangers.SlotLockingManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ERacesCast : ClientModInitializer {
    override fun onInitializeClient() {
        LOGGER.info("Инициализация")

        val args = FabricLoader.getInstance().getLaunchArguments(true)
        isNetworkingEnabled = !listOf(*args).contains("--guiTest")

        PacketSendManager.initPackets()
        SlotLockingManager.initListener()
        HudManager.initHudManager()
        CastManager.initListeners()
        LOGGER.info("Инициализация завершена")
    }

    companion object {
        val LOGGER: Logger = LogManager.getLogger("ERacesCast")
        var isNetworkingEnabled: Boolean = true
    }
}