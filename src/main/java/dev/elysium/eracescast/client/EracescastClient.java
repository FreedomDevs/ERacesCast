package dev.elysium.eracescast.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class EracescastClient implements ClientModInitializer {

    private static KeyMapping myKey;
    private boolean isHolding = false;

    @Override
    public void onInitializeClient() {
        myKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.elysium.erace",       // идентификатор бинда
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,           // клавиша R
                "category.elysium.keys"    // категория биндов
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (myKey.isDown()) {
                isHolding = true;
            } else if (isHolding) {
                isHolding = false;
                System.out.println("Отжата клавиша");
            }
        });
    }
}
