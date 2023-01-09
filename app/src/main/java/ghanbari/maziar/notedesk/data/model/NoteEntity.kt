package ghanbari.maziar.notedesk.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ghanbari.maziar.notedesk.utils.NOTE_TABLE_NAME


@Entity(tableName = NOTE_TABLE_NAME)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    var title : String = "",
    var des : String = "",
    var folderTitle : String = "",
    var folderImg : Int = 0,
    var date : String = "",
    var time : String = "",
    var priority : String = "",
    var isPinned : Boolean = false
)