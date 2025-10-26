package dev.elysium.eracescast.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EracescastClient implements ClientModInitializer {

    private static KeyBinding myKey;
    private boolean isHolding = false;

    private boolean isCastEnabled = false;
    private long castEnableTime = 0;
    private int lockedslot = 0;
    private Set<Integer> lastPressd = new HashSet<>();
    private List<Integer> keys = new ArrayList<>();

    public void castEnd() {
        isCastEnabled = false;
        System.out.println("Каст завершён с " + keys);
        MinecraftClient.getInstance().player.sendMessage(Text.literal("Каст завершён с " + keys), false);
        sendEndCast();
    }

    public void sendStartCast() {
        ERacesPayload payload = new ERacesPayload(System.currentTimeMillis(), "start_cast", "");
        ClientPlayNetworking.send(payload);
    }

    public void sendEndCast() {
        ERacesPayload payload = new ERacesPayload(System.currentTimeMillis(), "end_cast", keys.stream()
                .map(String::valueOf)
                .collect(Collectors.joining()));
        ClientPlayNetworking.send(payload);
    }

    public void sendKey(int key) {
        ERacesPayload payload = new ERacesPayload(System.currentTimeMillis(), "cast_key", String.valueOf(key));
        ClientPlayNetworking.send(payload);
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
            if (!ClientPlayNetworking.canSend(ERacesPayload.ID))
                return;

            if (myKey.wasPressed()) {
                isHolding = true;
            } else if (isHolding) {
                isHolding = false;
                if (!isCastEnabled) {
                    isCastEnabled = true;
                    castEnableTime = System.currentTimeMillis();
                    keys = new ArrayList<>();
                    if (client.player != null)
                        lockedslot = client.player.getInventory().selectedSlot;
                    sendStartCast();
                    System.out.println("Каст запущен");
                } else if (System.currentTimeMillis() - castEnableTime > 100)
                    castEnd();
            }

            if (isCastEnabled && System.currentTimeMillis() - castEnableTime > 1000 * 10)
                castEnd();

            if (isCastEnabled) {
                if (client.player != null)
                    client.player.getInventory().selectedSlot = lockedslot;

                for (int i = 0; i < 9; i++) {
                    int num = i + 1;
                    int key = GLFW.GLFW_KEY_1 + i; // 1..9
                    if (InputUtil.isKeyPressed(client.getWindow().getHandle(), key)) {
                        lastPressd.add(num);
                    } else if (lastPressd.contains(num)) {
                        sendKey(num);
                        keys.add(num);
                        lastPressd.remove(num);
                        System.out.println("Нажата клавиша: " + num);
                    }
                }
            }
        });
        PayloadTypeRegistry.playC2S().register(ERacesPayload.ID, ERacesPayload.CODEC);
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            int x = 7;
            int width = 70;
            int height = 14;
            int barHeight = 2;

            int textColor = 0xFFFFCC88;
            int backgroundColor = 0x88000000;
            int borderColor = 0xFF444444;
            int progressStartColor = 0xFFFFAA00;
            int progressEndColor = 0xFFFF3300;

            if (isCastEnabled) {
                long elapsed = System.currentTimeMillis() - castEnableTime;
                long duration = 10_000;
                double progress = Math.min(1.0, Math.max(0.0, (double) elapsed / duration));

                int startY = -height - 5;
                int endY = 7;

                double animProgress;
                if (progress < 1.0) {
                    animProgress = 1 - Math.pow(1 - Math.min(1.0, elapsed / 300.0), 3);
                } else {
                    long afterEnd = elapsed - duration;
                    animProgress = 1 - Math.pow(Math.min(1.0, afterEnd / 300.0), 3);
                }

                int y = (int) (startY + (endY - startY) * animProgress);

                float alpha = Math.min(1.0f, elapsed / 200f);
                int bgColor = (backgroundColor & 0x00FFFFFF) | ((int) (alpha * 200) << 24);
                drawContext.fill(x - 1, y - 1, x + width + 1, y + height + 1, borderColor);
                drawContext.fill(x, y, x + width, y + height, bgColor);

                String text = "Режим каста";
                drawContext.drawText(client.textRenderer, text, x + 5, y + 4, 0x44000000, false);
                drawContext.drawText(client.textRenderer, text, x + 4, y + 3, textColor, false);

                int textHeight = 9;
                int barY1 = y + textHeight + 3;
                int barY2 = barY1 + barHeight;
                int barWidth = (int) ((width - 4) * (1.0 - Math.pow(progress, 1.5)));

                for (int i = 0; i < barWidth; i++) {
                    double ratio = (double) i / barWidth;
                    int r = (int) (((progressStartColor >> 16 & 0xFF) * (1 - ratio)) + ((progressEndColor >> 16 & 0xFF) * ratio));
                    int g = (int) (((progressStartColor >> 8 & 0xFF) * (1 - ratio)) + ((progressEndColor >> 8 & 0xFF) * ratio));
                    int b = (int) (((progressStartColor & 0xFF) * (1 - ratio)) + ((progressEndColor & 0xFF) * ratio));

                    double pulse = Math.sin(System.currentTimeMillis() / 300.0) * 0.1 + 0.9;
                    r = Math.min(255, (int) (r * pulse));
                    g = Math.min(255, (int) (g * pulse));
                    b = Math.min(255, (int) (b * pulse));

                    int color = (0xFF << 24) | (r << 16) | (g << 8) | b;
                    drawContext.fill(x + 2 + i, barY1, x + 2 + i + 1, barY2, color);
                }

                if (barWidth > 0) {
                    int glowColor = 0x66FFFFFF;
                    drawContext.fill(x + 2 + barWidth - 1, barY1, x + 2 + barWidth, barY2, glowColor);
                }
            }
        });

    }
}
