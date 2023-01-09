package ghanbari.maziar.notedesk.utils.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ghanbari.maziar.notedesk.data.local.DatabaseND
import ghanbari.maziar.notedesk.data.model.NoteEntity
import ghanbari.maziar.notedesk.utils.NOTE_DESK_DATABASE_NAME
import javax.inject.Singleton


//module for providing NoteDesk DB in app
@Module
@InstallIn(SingletonComponent::class)
object NoteDeskDBModule {

    //provide note entity
    @Provides
    fun provideNoteEntity() = NoteEntity()

    @Provides
    fun provideFolderEntity() = NoteEntity()

    //provide database
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
        context, DatabaseND::class.java,
        NOTE_DESK_DATABASE_NAME
    )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

    //provide note dao
    @Provides
    @Singleton
    fun provideNoteDao(db : DatabaseND) = db.noteDao()

    @Provides
    @Singleton
    fun provideFolderDao(db : DatabaseND) = db.folderDao()

}