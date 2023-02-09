package ghanbari.maziar.notedesk.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyDataStore @Inject constructor(@ApplicationContext private val context:Context) {

    companion object{
        val Context.dataStore : DataStore<Preferences> by preferencesDataStore(DATA_STORE)
        //save title and des to dataStore
        val noteTitleDataStore = stringPreferencesKey(DATA_STORE_TITLE_NOTE)
        val noteDesDataStore = stringPreferencesKey(DATA_STORE_DES_NOTE)
    }
    //save note title to data store
    suspend fun saveNoteTitle(noteTitle :String){
        context.dataStore.edit {
            it[noteTitleDataStore] = noteTitle
        }
    }
    //save note des to data store
    suspend fun saveNoteDes(noteDes : String){
        context.dataStore.edit {
            it[noteDesDataStore] = noteDes
        }
    }
    //get note title from data store
    fun getNoteTitle() = context.dataStore.data.map {
        it[noteTitleDataStore] ?: ""
    }
    //get note des from data store
    fun getNoteDes() = context.dataStore.data.map {
        it[noteDesDataStore] ?: ""
    }
}