package ghanbari.maziar.notedesk.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ghanbari.maziar.notedesk.utils.*


@Entity(
    tableName = NOTE_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = FolderEntity::class,
        parentColumns = arrayOf(FOLDER_ID),
        childColumns = arrayOf(NOTE_FOLDER_ID),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.SET_DEFAULT
    )]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = NOTE_ID)
    var id: Int = 0,
    @ColumnInfo(name = NOTE_TITLE)
    var title: String = "",
    @ColumnInfo(name = NOTE_DES)
    var des: String = "",
    @ColumnInfo(name = NOTE_FOLDER_ID, defaultValue = "1")//NO_FOLDER is default
    var folder_id :Int = 0,
    @ColumnInfo(name = NOTE_DATE)
    var date: String = "",
    @ColumnInfo(name = NOTE_TIME)
    var time: String = "",
    @ColumnInfo(name = NOTE_PRIORITY)
    var priority: String = "",
    @ColumnInfo(name = NOTE_IS_PINNED)
    var isPinned: Boolean = false
)