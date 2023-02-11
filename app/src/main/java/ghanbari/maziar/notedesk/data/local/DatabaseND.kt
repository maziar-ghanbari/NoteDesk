package ghanbari.maziar.notedesk.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ghanbari.maziar.notedesk.data.model.FolderEntity
import ghanbari.maziar.notedesk.data.model.NoteEntity

@Database(entities = [NoteEntity::class,FolderEntity::class] , version = 2 , exportSchema = false)
abstract class DatabaseND : RoomDatabase() {
    abstract fun noteDao() : NoteDao
    abstract fun folderDao() : FolderDao
}