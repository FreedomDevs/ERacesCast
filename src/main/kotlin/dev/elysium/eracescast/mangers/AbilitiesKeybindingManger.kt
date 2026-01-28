package dev.elysium.eracescast.mangers

import dev.elysium.eblanexp.EBlanexpMain.Companion.LOGGER
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

object AbilitiesKeybindingManger {
    val keybindings: MutableMap<String, List<Int>> = mutableMapOf()
    private val pressed: MutableSet<String> = mutableSetOf()

    fun initKeybindingListener() {
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            tick(client)
        }
    }

    private fun tick(client: MinecraftClient) {
        for (binding in keybindings) {
            var isReleased = false
            for (code in binding.value) {
                if (GLFW.glfwGetKey(client.window.handle, code) == GLFW_RELEASE) {
                    isReleased = true
                }
            }

            if (!pressed.contains(binding.key)) {
                if (!isReleased) {
                    LOGGER.info("НАЖАТ БИНД " + binding.key + " (бинд будет активирован после отжатия)")
                    pressed.add(binding.key)
                }
            } else {
                if (isReleased) {
                    LOGGER.info("АКТИВИРОВАН БИНД " + binding.key)
                    pressed.remove(binding.key)

                    PacketSendManager.sendAbility(binding.key)
                }
            }
        }
    }
}