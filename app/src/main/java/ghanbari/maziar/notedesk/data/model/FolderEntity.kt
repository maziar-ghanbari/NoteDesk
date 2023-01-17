package ghanbari.maziar.notedesk.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ghanbari.maziar.notedesk.utils.FOLDERS_TABLE_NAME
import ghanbari.maziar.notedesk.utils.FOLDER_ID
import ghanbari.maziar.notedesk.utils.FOLDER_IMG
import ghanbari.maziar.notedesk.utils.FOLDER_TITLE


@Entity(tableName = FOLDERS_TABLE_NAME)
data class FolderEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = FOLDER_ID)
    var id : Int = 0,
    @ColumnInfo(name = FOLDER_IMG)
    var img : Int = 0,
    @ColumnInfo(name = FOLDER_TITLE)
    var title : String = ""
)