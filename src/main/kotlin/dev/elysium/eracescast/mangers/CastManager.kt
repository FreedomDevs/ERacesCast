package dev.elysium.eracescast.mangers

import dev.elysium.eracescast.ERacesCast.Companion.LOGGER
import dev.elysium.eracescast.ERacesCast.Companion.isNetworkingEnabled
import dev.elysium.eracescast.mangers.PacketSendManager.sendKey
import dev.elysium.eracescast.mangers.PacketSendManager.sendStartCast
import dev.elysium.eracescast.mangers.SlotLockingManager.lockSlot
import dev.elysium.eracescast.mangers.SlotLockingManager.unlockSlot
import dev.elysium.eracescast.packets.ERacesCastPayload
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

object CastManager {
    private lateinit var keybind: KeyBinding
    private val lastNumbersPressed: MutableSet<Int> = HashSet()
    private val keys: MutableList<Int> = ArrayList()
    private var isHoldingCastButton = false
    const val castDuration: Long = 5_000
    var castEnableTime: Long = -1
        private set


    fun castEnd() {
        castEnableTime = -1
        LOGGER.info("Каст завершён с $keys")
        unlockSlot()
        PacketSendManager.sendEndCast(keys)
        keys.clear()
    }

    fun initListeners() {
        keybind = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.elysium.erace",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.elysium.keys"
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register({ client ->
            if (isNetworkingEnabled && !ClientPlayNetworking.canSend(ERacesCastPayload.ID)) return@register

            if (keybind.wasPressed()) {
                isHoldingCastButton = true
            } else if (isHoldingCastButton) {
                isHoldingCastButton = false
                if (!isCastEnabled()) {
                    castEnableTime = System.currentTimeMillis()

                    lockSlot()
                    sendStartCast()
                    LOGGER.info("Каст запущен")
                } else if (System.currentTimeMillis() - castEnableTime > 100) castEnd()
            }

            if (isCastEnabled()) {
                if (System.currentTimeMillis() - castEnableTime > castDuration) {
                    castEnd()
                    return@register
                }

                for (i in 0..8) {
                    val num = i + 1
                    val key = GLFW.GLFW_KEY_1 + i // 1..9
                    if (InputUtil.isKeyPressed(client.window.handle, key)) {
                        lastNumbersPressed.add(num)
                    } else if (lastNumbersPressed.contains(num)) {
                        sendKey(num)
                        keys.add(num)
                        lastNumbersPressed.remove(num)
                        LOGGER.info("Нажата клавиша: $num")
                    }
                }
            }
        })
    }

    fun isCastEnabled(): Boolean {
        return castEnableTime != (-1).toLong()
    }
}