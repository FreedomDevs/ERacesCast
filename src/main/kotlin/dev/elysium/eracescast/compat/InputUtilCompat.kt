package dev.elysium.eracescast.compat

import dev.elysium.eracescast.utils.VersionUtil.isVersionAtLeast
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.Window

object InputUtilCompat {
    private var cachedMethod: java.lang.reflect.Method? = null

    fun isKeyPressed(window: Window, keyCode: Int): Boolean {

        return try {
            if (cachedMethod == null) {
                cachedMethod = if (isVersionAtLeast(1, 21, 9)) {
                    InputUtil::class.java.getDeclaredMethod("method_15987", Window::class.java, Int::class.javaPrimitiveType)
                } else {
                    InputUtil::class.java.getDeclaredMethod("method_15987", Long::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                }
            }

            val result = if (isVersionAtLeast(1, 21, 9)) {
                cachedMethod!!.invoke(null, window, keyCode)
            } else {
                cachedMethod!!.invoke(null, window.handle, keyCode)
            }
            result as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}