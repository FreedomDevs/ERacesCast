package dev.elysium.eracescast.mangers

import dev.elysium.eracescast.ERacesCast.Companion.LOGGER
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerInventory
import java.lang.reflect.Field

object SlotLockingManager {
    private var lockedSlot: Int = -1

    private var selectedSlotField: Field? = null
    private fun getSelectedSlotField(): Field? {
        if (selectedSlotField == null) {
            try {
                selectedSlotField = PlayerInventory::class.java.getDeclaredField("field_7545")
                selectedSlotField?.isAccessible = true
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            }
        }
        return selectedSlotField
    }
    fun getSelectedSlot(player: ClientPlayerEntity): Int {
        return try {
            getSelectedSlotField()?.get(player.inventory) as Int
        } catch (e: Exception) {
            e.printStackTrace()
            0 // дефолтное значение, если не получилось
        }
    }
    fun setSelectedSlot(player: ClientPlayerEntity, slot: Int) {
        try {
            getSelectedSlotField()?.set(player.inventory, slot)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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

            setSelectedSlot(player, lockedSlot)
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

        lockedSlot = getSelectedSlot(player)
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