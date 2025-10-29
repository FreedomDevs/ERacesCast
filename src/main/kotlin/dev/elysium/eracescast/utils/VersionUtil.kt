package dev.elysium.eracescast.utils

import net.fabricmc.loader.api.FabricLoader

object VersionUtil {
    fun isVersionAtLeast(major: Int, minor: Int, patch: Int): Boolean {
        val version = FabricLoader.getInstance().rawGameVersion
        val parts = version.split(".").map { it.toIntOrNull() ?: 0 }
        val (vMajor, vMinor, vPatch) = parts + listOf(0, 0, 0) // добавляем нули, если нет
        return (vMajor > major) || (vMajor == major && vMinor > minor) || (vMajor == major && vMinor == minor && vPatch >= patch)
    }
}