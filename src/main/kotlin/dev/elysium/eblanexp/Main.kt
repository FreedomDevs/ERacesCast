package dev.elysium.eblanexp

import dev.elysium.eracescast.ERacesCast
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class EBlanexpMain : ClientModInitializer {
    override fun onInitializeClient() {
        LOGGER.info("Инициализация")

        val version = FabricLoader.getInstance().rawGameVersion
        if (version !in listOf("1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11")) {
            LOGGER.error("Версия майнкрафт не поддерживается: $version")
        }

        ERacesCast.onInitializeClient();
        LOGGER.info("Инициализация завершена")
    }

    companion object {
        val LOGGER: Logger = LogManager.getLogger("EBlanexp")
    }
}