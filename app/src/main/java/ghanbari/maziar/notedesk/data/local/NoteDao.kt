package ghanbari.maziar.notedesk.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ghanbari.maziar.notedesk.data.model.NoteAndFolder
import ghanbari.maziar.notedesk.data.model.NoteEntity
import ghanbari.maziar.notedesk.utils.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Transaction
    @Query("SELECT $NOTE_TABLE_NAME.* ,$FOLDERS_TABLE_NAME.* FROM $NOTE_TABLE_NAME " +
            "INNER JOIN $FOLDERS_TABLE_NAME ON $NOTE_FOLDER_ID = $FOLDER_ID " +
            "ORDER BY note_is_pinned DESC")
    fun getAllNotesRelated(): Flow<MutableList<NoteAndFolder>>

    @Transaction
    @Query("SELECT $NOTE_TABLE_NAME.* ,$FOLDERS_TABLE_NAME.* FROM $NOTE_TABLE_NAME " +
            "INNER JOIN $FOLDERS_TABLE_NAME ON $NOTE_FOLDER_ID = $FOLDER_ID AND $NOTE_ID = :id")
    fun getNoteRelatedById(id: Int): Flow<MutableList<NoteAndFolder>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNote(notes: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)
}