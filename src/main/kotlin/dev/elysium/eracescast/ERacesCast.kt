package dev.elysium.eracescast

import dev.elysium.eracescast.mangers.*
import dev.elysium.eracescast.screen.EscMenu
import org.lwjgl.glfw.GLFW.*

object ERacesCast {
    fun onInitializeClient() {
        AbilitiesKeybindingManger.keybindings["throughskies"] = listOf(GLFW_KEY_LEFT_SHIFT, GLFW_KEY_G)
        AbilitiesKeybindingManger.keybindings["fireboom"] = listOf(GLFW_KEY_LEFT_SHIFT, GLFW_KEY_T)

        AbilitiesKeybindingManger.initKeybindingListener()
        PacketSendManager.initPackets()

        EscMenu.initEscMenuScreen()
    }
}