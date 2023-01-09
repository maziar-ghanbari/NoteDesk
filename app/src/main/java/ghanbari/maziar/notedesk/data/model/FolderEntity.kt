package ghanbari.maziar.notedesk.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ghanbari.maziar.notedesk.utils.FOLDERS_TABLE_NAME


@Entity(tableName = FOLDERS_TABLE_NAME)
data class FolderEntity(
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    var img : Int = 0,
    var title : String = ""
)