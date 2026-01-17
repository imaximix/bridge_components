package com.imaximix.geovote

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Modern, hardware-backed secure storage library.
 * Built with Jetpack DataStore and Google Tink.
 */
class SecureStorage(private val context: Context) {

    companion object {
        private const val TAG = "SecureStorage"
        private const val PREFS_NAME = "geovote_secure_storage"
        private const val KEYSET_NAME = "geovote_keyset"
        private const val PREF_FILE_NAME = "geovote_tink_prefs"

        // Use a unique URI for your library's master key in Android Keystore
        private const val MASTER_KEY_URI = "android-keystore://geovote_master_key"

        private val Context.dataStore by preferencesDataStore(name = PREFS_NAME)

        @Volatile
        private var instance: SecureStorage? = null

        fun getInstance(context: Context): SecureStorage {
            return instance ?: synchronized(this) {
                instance ?: SecureStorage(context.applicationContext).also { instance = it }
            }
        }
    }

    private val aead: Aead by lazy {
        try {
            AeadConfig.register()
            AndroidKeysetManager.Builder()
                .withSharedPref(context, KEYSET_NAME, PREF_FILE_NAME)
                .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                .withMasterKeyUri(MASTER_KEY_URI)
                .build()
                .keysetHandle
                .getPrimitive(Aead::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Tink Aead", e)
            throw SecurityException("Secure storage initialization failed", e)
        }
    }

    /**
     * Encrypts and saves a string.
     */
    fun putString(key: String, value: String?) {
        runBlocking {
            context.dataStore.edit { prefs ->
                val prefKey = stringPreferencesKey(key)
                if (value == null) {
                    prefs.remove(prefKey)
                } else {
                    val encrypted = aead.encrypt(value.toByteArray(Charsets.UTF_8), null)
                    prefs[prefKey] = Base64.encodeToString(encrypted, Base64.NO_WRAP)
                }
            }
        }
    }

    /**
     * Retrieves and decrypts a string. Returns null if key doesn't exist or decryption fails.
     */
    fun getString(key: String, defaultValue: String? = null): String? = runBlocking {
        try {
            val prefKey = stringPreferencesKey(key)
            val encryptedBase64 = context.dataStore.data.map { it[prefKey] }.first()

            if (encryptedBase64 == null) return@runBlocking defaultValue

            val decoded = Base64.decode(encryptedBase64, Base64.NO_WRAP)
            val decrypted = aead.decrypt(decoded, null)
            String(decrypted, Charsets.UTF_8)
        } catch (e: Exception) {
            // "Silent Reset" Policy: Catch failures (like key corruption) and return default.
            Log.e(TAG, "Decryption failed for key: $key. Returning default.", e)
            defaultValue
        }
    }

    // --- Type Helpers ---

    fun putInt(key: String, value: Int) = putString(key, value.toString())
    fun getInt(key: String, default: Int = 0): Int = getString(key)?.toIntOrNull() ?: default

    fun putBoolean(key: String, value: Boolean) = putString(key, value.toString())
    fun getBoolean(key: String, default: Boolean = false): Boolean = getString(key)?.toBoolean() ?: default

    fun remove(key: String) {
        runBlocking {
            context.dataStore.edit { it.remove(stringPreferencesKey(key)) }
        }
    }

    fun clearAll() {
        runBlocking {
            context.dataStore.edit { it.clear() }
        }
    }
}