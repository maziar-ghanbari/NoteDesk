package ghanbari.maziar.notedesk.data.model

import androidx.room.Embedded

data class NoteAndFolder (
    @Embedded
    val note : NoteEntity,
    @Embedded
    val folder: FolderEntity
) {
    override fun toString(): String {
        return "عنوان یادداشت : (${note.title})\n"+
                "توضیحات یادداشت : (${note.des})\n"+
                "تاریخ : (${note.date})\n"+
                "ساعت : (${note.time})\n"+
                "اولویت : (${note.priority})\n"+
                "نام پوشه : (${folder.title})\n"+
                if (note.isPinned) "[یادداشت سنجاق شده است.]" else "[یادداشت سنجاق نشده است.]"
    }
}