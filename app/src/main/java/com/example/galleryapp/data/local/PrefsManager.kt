package com.example.galleryapp.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest

class PrefsManager(private val prefs: SharedPreferences) {

    // ── Vault PIN ─────────────────────────────────────────────────────────────

    fun setVaultPin(pin: String) {
        prefs.edit()
            .putString(KEY_VAULT_PIN_HASH, hashPin(pin))
            .putInt(KEY_VAULT_FAIL_COUNT, 0)
            .putLong(KEY_VAULT_LOCKOUT_UNTIL, 0L)
            .apply()
    }

    fun hasVaultPin(): Boolean = prefs.getString(KEY_VAULT_PIN_HASH, null) != null

    fun verifyVaultPin(pin: String): Boolean =
        prefs.getString(KEY_VAULT_PIN_HASH, null) == hashPin(pin)

    fun getVaultFailCount(): Int = prefs.getInt(KEY_VAULT_FAIL_COUNT, 0)

    fun incrementVaultFailCount(): Int {
        val newCount = getVaultFailCount() + 1
        prefs.edit().putInt(KEY_VAULT_FAIL_COUNT, newCount).apply()
        return newCount
    }

    fun resetVaultFailCount() {
        prefs.edit()
            .putInt(KEY_VAULT_FAIL_COUNT, 0)
            .putLong(KEY_VAULT_LOCKOUT_UNTIL, 0L)
            .apply()
    }

    fun setVaultLockoutUntil(timeMs: Long) {
        prefs.edit().putLong(KEY_VAULT_LOCKOUT_UNTIL, timeMs).apply()
    }

    fun getVaultLockoutUntil(): Long = prefs.getLong(KEY_VAULT_LOCKOUT_UNTIL, 0L)

    // ── Settings ─────────────────────────────────────────────────────────────

    fun getBoolean(key: String, default: Boolean): Boolean =
        prefs.getBoolean(key, default)

    fun setBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getString(key: String, default: String): String =
        prefs.getString(key, default) ?: default

    fun setString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(pin.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val PREFS_NAME = "gallery_secure_prefs"

        fun create(context: Context): PrefsManager {
            val masterKey = MasterKey.Builder(context.applicationContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            return PrefsManager(
                EncryptedSharedPreferences.create(
                    context.applicationContext,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            )
        }

        const val KEY_VAULT_PIN_HASH = "vault_pin_hash"
        const val KEY_VAULT_FAIL_COUNT = "vault_fail_count"
        const val KEY_VAULT_LOCKOUT_UNTIL = "vault_lockout_until"

        // Settings keys
        const val KEY_APP_LOCK = "setting_app_lock"
        const val KEY_HIDE_APP_ICON = "setting_hide_app_icon"
        const val KEY_STRIP_METADATA = "setting_strip_metadata"
        const val KEY_CLOUD_BACKUP = "setting_cloud_backup"
        const val KEY_SHOW_VIDEO_DURATION = "setting_show_video_duration"
        const val KEY_ROUNDED_THUMBNAILS = "setting_rounded_thumbnails"
        const val KEY_DARK_MODE = "setting_dark_mode"
        const val KEY_DEFAULT_GRID = "setting_default_grid"
    }
}
