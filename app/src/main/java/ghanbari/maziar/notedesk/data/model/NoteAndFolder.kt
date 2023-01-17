package ghanbari.maziar.notedesk.data.model

import androidx.room.Embedded
import androidx.room.Relation
import ghanbari.maziar.notedesk.utils.FOLDER_ID
import ghanbari.maziar.notedesk.utils.NOTE_FOLDER_ID

data class NoteAndFolder (
    @Embedded
    val note : NoteEntity,
    @Embedded
    val folder: FolderEntity
)