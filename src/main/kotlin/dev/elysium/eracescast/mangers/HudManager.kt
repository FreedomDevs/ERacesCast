package dev.elysium.eracescast.mangers

import net.minecraft.client.MinecraftClient
import kotlin.math.pow

object HudManager {
    fun initHudManager() {
        @Suppress("Deprecation")
        net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback.EVENT.register({ drawContext, tickDelta ->
            val client = MinecraftClient.getInstance()
            val x: Int = 7
            val width: Int = 70
            val height: Int = 14
            val barHeight: Int = 2

            val textColor: Int = 0xFFFFCC88.toInt()
            val backgroundColor: Int = 0x88000000.toInt()
            val borderColor: Int = 0xFF444444.toInt()
            val progressStartColor: Int = 0xFFFFAA00.toInt()
            val progressEndColor: Int = 0xFFFF3300.toInt()

            if (CastManager.isCastEnabled()) {
                val elapsed: Long = System.currentTimeMillis() - CastManager.castEnableTime
                val duration: Long = CastManager.castDuration
                val progress: Double = (elapsed.toDouble() / duration).coerceIn(0.0, 1.0)

                val startY: Int = -height - 5
                val endY: Int = 7

                var animProgress: Double
                if (progress < 1.0) {
                    animProgress = 1 - (1 - (elapsed / 300.0).coerceAtMost(1.0)).pow(3)
                } else {
                    val afterEnd: Long = elapsed - duration;
                    animProgress = 1 - (afterEnd / 300.0).coerceAtMost(1.0).pow(3)
                }

                val y: Int = (startY + (endY - startY) * animProgress).toInt()

                val alpha: Float = (elapsed / 200f).coerceAtMost(1.0f)
                val bgColor: Int = (backgroundColor and 0x00FFFFFF) or (((alpha * 200).toInt()) shl 24)
                drawContext.fill(x - 1, y - 1, x + width + 1, y + height + 1, borderColor)
                drawContext.fill(x, y, x + width, y + height, bgColor)

                val text: String = "Режим каста"
                drawContext.drawText(client.textRenderer, text, x + 5, y + 4, 0x44000000, false)
                drawContext.drawText(client.textRenderer, text, x + 4, y + 3, textColor, false)

                val textHeight: Int = 9
                val barY1: Int = y + textHeight + 3
                val barY2: Int = barY1 + barHeight
                val barWidth: Int = ((width - 4) * (1.0 - Math.pow(progress, 1.5))).toInt()

                for (i in 0 until barWidth) {
                    val ratio: Double = (i / barWidth).toDouble()
                    var r: Int =
                        (((progressStartColor shr 16 and 0xFF) * (1 - ratio)) + ((progressEndColor shr 16 and 0xFF) * ratio)).toInt()
                    var g: Int =
                        (((progressStartColor shr 8 and 0xFF) * (1 - ratio)) + ((progressEndColor shr 8 and 0xFF) * ratio)).toInt()
                    var b: Int =
                        (((progressStartColor and 0xFF) * (1 - ratio)) + ((progressEndColor and 0xFF) * ratio)).toInt()

                    val pulse: Double = Math.sin(System.currentTimeMillis() / 300.0) * 0.1 + 0.9;
                    r = (r * pulse).toInt().coerceIn(0, 255);
                    g = (g * pulse).toInt().coerceIn(0, 255);
                    b = (b * pulse).toInt().coerceIn(0, 255)

                    val color = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
                    drawContext.fill(x + 2 + i, barY1, x + 2 + i + 1, barY2, color);
                }

                if (barWidth > 0) {
                    val glowColor: Int = 0x66FFFFFF;
                    drawContext.fill(x + 2 + barWidth - 1, barY1, x + 2 + barWidth, barY2, glowColor);
                }
            }
        });
    }
}