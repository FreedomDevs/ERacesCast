package dev.elysium.eracescast.screen

import dev.elysium.eblanexp.EBlanexpMain.Companion.LOGGER
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.screen.GameMenuScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import kotlin.jvm.java

object EscMenu {
    fun initEscMenuScreen (){
        ScreenEvents.AFTER_INIT.register { client, screen, scaledWidth, scaledHeight ->
            if(screen is GameMenuScreen) {
                try {
                    val button = ButtonWidget.builder(Text.of("Открыть меню рас"), {it ->
                        client.setScreen(AbilitiesScreen())
                        })
                        .position(2, 2).size(100, 20).build()

                    // Добавляем через рефлексию
                    val method = Screen::class.java.declaredMethods.first {
                        it.name == "method_37063" && it.parameterCount == 1
                    }
                    method.isAccessible = true
                    method.invoke(screen, button)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}