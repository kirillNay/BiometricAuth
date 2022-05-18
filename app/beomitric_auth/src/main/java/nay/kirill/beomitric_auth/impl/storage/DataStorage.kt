package nay.kirill.beomitric_auth.impl.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

internal class DataStorage(
    private val context: Context
) {

    companion object {
        private const val ENCRYPTED_DATA_STORAGE_NAME = "access_token"

        private val ENCRYPTED_DATA_KEY = stringPreferencesKey("EXPIRATION_DATE_KEY")
    }

    private val Context.dataStore by preferencesDataStore(name = ENCRYPTED_DATA_STORAGE_NAME)

    suspend fun saveData(data: String) {
        context.dataStore.edit { pref -> pref[ENCRYPTED_DATA_KEY] = data }
    }

    fun getData(): Flow<String> = context.dataStore.data.mapNotNull { pref -> pref[ENCRYPTED_DATA_KEY] }

}
