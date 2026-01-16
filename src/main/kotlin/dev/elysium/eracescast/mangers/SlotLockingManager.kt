package dev.elysium.eracescast.mangers

import dev.elysium.eblanexp.EBlanexpMain.Companion.LOGGER
import dev.elysium.eracescast.compat.PlayerInventoryCompat
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient

object SlotLockingManager {
    private var lockedSlot: Int = -1

    fun initListener() {
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            if (lockedSlot == -1)
                return@register

            val player = client.player
            if (player == null) {
                LOGGER.error("Невозможно поддерживать блокировку слота хотбара так как player==null, слот принудительно разблокирован")
                lockedSlot = -1
                return@register
            }


            PlayerInventoryCompat(player).selectedSlot = lockedSlot
        }
    }

    fun lockSlot() {
        val player = MinecraftClient.getInstance().player
        if (lockedSlot != -1) {
            LOGGER.error("Не удалось заблокировать слот хотбара так как он уже заблокирован")
            return
        }
        if (player == null) {
            LOGGER.error("Не удалось заблокировать слот хотбара так как player==null")
            return
        }

        lockedSlot = PlayerInventoryCompat(player).selectedSlot
        LOGGER.info("Блокировка хотбара установлена")
    }

    fun unlockSlot() {
        if (lockedSlot == -1) {
            LOGGER.warn("Попытка разблокировать хотбар, но он уже разблокирован")
            return
        }

        lockedSlot = -1
        LOGGER.info("Блокировка хотбара разблокирована")
    }

    @Suppress("unused")
    fun isLocked(): Boolean {
        return lockedSlot != -1
    }
    @Suppress("unused")
    fun isUnlocked(): Boolean {
        return lockedSlot == -1
    }
}