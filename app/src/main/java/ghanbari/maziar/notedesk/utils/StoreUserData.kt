package ghanbari.maziar.notedesk.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StoreUserData @Inject constructor(@ApplicationContext private val context: Context) {
    companion object {
        //DATA STORE
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DATA_STORE)
        //is the first launch data store
        private val isTheFirstLaunch = booleanPreferencesKey(IS_FIRST_LAUNCH_DATA_STORE)
    }
    suspend fun setStateFirstLaunch(tf : Boolean){
        context.dataStore.edit {
            it[isTheFirstLaunch] = tf
        }
    }
    fun getStateFirstLaunch() = context.dataStore.data.map{
        it[isTheFirstLaunch] ?: true
    }
}