package dev.elysium.eracescast.compat

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerInventory
import java.lang.reflect.Field

class PlayerInventoryCompat(player: ClientPlayerEntity) {
    private val player: ClientPlayerEntity = player
    private val selectedSlotField: Field? by lazy {
        try {
            PlayerInventory::class.java.getDeclaredField("field_7545").apply { isAccessible = true }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
            null
        }
    }

    var selectedSlot: Int
        get() = try {
            selectedSlotField?.get(player.inventory) as? Int ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
        set(value) {
            try {
                selectedSlotField?.set(player.inventory, value)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
}
