package nay.kirill.beomitric_auth.impl.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

internal class DataStorage(
    private val context: Context
) {

    companion object {
        private const val ENCRYPTED_DATA_STORAGE_NAME = "access_token"

        private val ENCRYPTED_DATA_KEY = stringPreferencesKey("EXPIRATION_DATE_KEY")
        private val INITIALIZE_VECTOR_KEY = stringPreferencesKey("INITIALIZE_VECTOR_KEY")

        private val Context.dataStore by preferencesDataStore(name = ENCRYPTED_DATA_STORAGE_NAME)
    }

    suspend fun saveData(data: EncryptedData) {
        context.dataStore.edit { pref ->
            pref[ENCRYPTED_DATA_KEY] = data.encryptedValue
            pref[INITIALIZE_VECTOR_KEY] = data.initializeVector
        }
    }

    fun getData(): Flow<EncryptedData?> = context.dataStore.data.map { pref ->
        val value = pref[ENCRYPTED_DATA_KEY]
        val vector = pref[INITIALIZE_VECTOR_KEY]

        if (value != null && vector != null) {
            EncryptedData(value, vector)
        } else {
            null
        }
    }

    internal data class EncryptedData(
        val encryptedValue: String,
        val initializeVector: String
    )

}
