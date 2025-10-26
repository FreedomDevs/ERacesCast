package dev.elysium.eracescast.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class EracescastClient implements ClientModInitializer {

    private static KeyBinding myKey;
    private boolean isHolding = false;

    private boolean isCastEnabled = false;
    private long castEnableTime = 0;

    public void castEnd() {
        isCastEnabled = false;
        System.out.println("Каст завершён");

    }

    @Override
    public void onInitializeClient() {
        myKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.elysium.erace",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.elysium.keys"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (myKey.wasPressed()) {
                isHolding = true;
            } else if (isHolding) {
                isHolding = false;
                if (!isCastEnabled) {
                    isCastEnabled = true;
                    castEnableTime = System.currentTimeMillis();
                    System.out.println("Каст запущен");
                } else if (System.currentTimeMillis() - castEnableTime > 100)
                        castEnd();
            }

            if (isCastEnabled && System.currentTimeMillis() - castEnableTime > 1000 * 3)
                castEnd();
        });

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            // Координаты квадрата
            int x1 = 7;
            int y1 = 7;

            int color = 0xFF662244;
            int color1 = 0x80AAAAAA;
            int color2 = 0xFFFF0000;

            if (isCastEnabled) {
                drawContext.fill(x1 - 3, y1 - 3, x1 + 65, y1 + 11, color1);
                drawContext.drawText(client.textRenderer, "Режим каста", x1, y1, color, false);

                long timeToEnd = System.currentTimeMillis() - castEnableTime;
                long min = 0;
                long max = 3 * 1000;

                double progress = (double) (timeToEnd - min) / (max - min);

                drawContext.fill(x1 - 1, y1 + 10, x1 + (63 - (int) (63 * progress)), y1 + 9, color2);
            }
        });
    }
}
