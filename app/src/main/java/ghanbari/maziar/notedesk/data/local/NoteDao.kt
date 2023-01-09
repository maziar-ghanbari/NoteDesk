package ghanbari.maziar.notedesk.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ghanbari.maziar.notedesk.data.model.NoteEntity
import ghanbari.maziar.notedesk.utils.NOTE_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM $NOTE_TABLE_NAME")
    fun getAllNotes() : Flow<MutableList<NoteEntity>>

    @Query("SELECT * FROM $NOTE_TABLE_NAME WHERE :id = ID")
    fun getNoteById(id : Int) : Flow<MutableList<NoteEntity>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNote(vararg notes : NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note : NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)
}