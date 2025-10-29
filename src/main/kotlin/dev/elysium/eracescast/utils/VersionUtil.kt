package dev.elysium.eracescast.utils

object VersionUtil {
    fun isVersionAtLeast(version: String, major: Int, minor: Int, patch: Int): Boolean {
        val parts = version.split(".").map { it.toIntOrNull() ?: 0 }
        val (vMajor, vMinor, vPatch) = parts + listOf(0, 0, 0) // добавляем нули, если нет
        return (vMajor > major) || (vMajor == major && vMinor > minor) || (vMajor == major && vMinor == minor && vPatch >= patch)
    }
}