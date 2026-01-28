package dev.elysium.eracescast.screen

import dev.elysium.eracescast.mangers.PacketSendManager
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text

class AbilitiesScreen : Screen(Text.literal("Способности")) {

    override fun init() {
        var counter = 0

        PacketSendManager.abilities?.forEach { ability ->
            val button = ButtonWidget.builder(
                Text.literal(ability.toString())
            ) {
                PacketSendManager.sendAbility(ability.id)
            }
                .dimensions(2, 1+ 19*counter, width - 4, 18)
                .build()

            addDrawableChild(button)
            counter++
        }
    }

    override fun shouldPause(): Boolean = false
}