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
        System.out.println("Каст завершён с "+keys);
        MinecraftClient.getInstance().player.sendMessage(Text.literal("Каст завершён с "+keys), false);
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

            // Координаты квадрата
            int x1 = 7;
            int y1 = 7;

            int textColor = 0xFF662244;
            int backgroundColor = 0x80AAAAAA;
            int progressBarColor = 0xFFAAAA00;

            if (isCastEnabled) {
                drawContext.fill(x1 - 3, y1 - 3, x1 + 65, y1 + 11, backgroundColor);
                drawContext.drawText(client.textRenderer, "Режим каста", x1, y1, textColor, false);

                long timeToEnd = System.currentTimeMillis() - castEnableTime;
                long min = 0;
                long max = 10 * 1000;

                double progress = (double) (timeToEnd - min) / (max - min);

                drawContext.fill(x1 - 1, y1 + 9, x1 + (63 - (int) (63 * progress)), y1 + 10, progressBarColor);
            }
        });

    }
}
