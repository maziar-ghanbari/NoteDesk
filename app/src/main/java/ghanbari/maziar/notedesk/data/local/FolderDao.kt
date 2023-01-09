package ghanbari.maziar.notedesk.data.local

import androidx.room.*
import ghanbari.maziar.notedesk.data.model.FolderEntity
import ghanbari.maziar.notedesk.utils.FOLDERS_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Query("SELECT * FROM $FOLDERS_TABLE_NAME")
    fun getAllFolders() : Flow<MutableList<FolderEntity>>

    @Update
    suspend fun updateFolder(folder : FolderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder : FolderEntity)

    @Delete
    suspend fun deleteFolder(folder: FolderEntity)
}