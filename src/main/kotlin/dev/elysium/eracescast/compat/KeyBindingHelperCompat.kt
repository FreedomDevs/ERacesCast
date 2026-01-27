package dev.elysium.eracescast.compat

import dev.elysium.eracescast.utils.VersionUtil.isVersionAtLeast
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.util.InputUtil
import java.lang.reflect.Constructor

object  KeyBindingHelperCompat {
    fun createKeyBinding(
        translationKey: String,
        type: InputUtil.Type,
        keyCode: Int,
        category: String
    ): Any { // возвращаем Any, чтобы не зависеть от конкретной версии KeyBinding
        val rawVersion = FabricLoader.getInstance().rawGameVersion
        val keyBindingClass = Class.forName("net.minecraft.class_304")
        return try {
            val clazz = Class.forName("net.minecraft.class_304") // KeyBinding
            if (isVersionAtLeast(1, 21, 9)) {
                // net.minecraft.class_304$class_11900 — KeyBinding$KeyCategory
                val keyCategoryClass = Class.forName("net.minecraft.class_304\$class_11900")
                val identifierClass = Class.forName("net.minecraft.class_2960")

                // Identifier.method_60655(namespace, path)
                val ofMethod = identifierClass.getDeclaredMethod(
                    "method_60655",
                    String::class.java,
                    String::class.java
                )
                val identifier = ofMethod.invoke(null, "elysium", category)

                // new KeyCategory(Identifier)
                val keyCategoryConstructor = keyCategoryClass.getConstructor(identifierClass)
                val keyCategory = keyCategoryConstructor.newInstance(identifier)

                // new KeyBinding(String, InputUtil.Type, int, KeyCategory)
                val constructor: Constructor<*> = keyBindingClass.getConstructor(
                    String::class.java,
                    InputUtil.Type::class.java,
                    Int::class.javaPrimitiveType,
                    keyCategoryClass
                )
                constructor.newInstance(translationKey, type, keyCode, keyCategory)
            } else {
                // старый конструктор
                val constructor = clazz.getConstructor(
                    String::class.java,
                    InputUtil.Type::class.java,
                    Int::class.javaPrimitiveType,
                    String::class.java
                )
                constructor.newInstance(translationKey, type, keyCode, category)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Failed to create KeyBinding")
        }
    }
}
