package ghanbari.maziar.notedesk.utils.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ghanbari.maziar.notedesk.R
import ghanbari.maziar.notedesk.data.local.DatabaseND
import ghanbari.maziar.notedesk.data.model.FolderEntity
import ghanbari.maziar.notedesk.data.model.NoteEntity
import ghanbari.maziar.notedesk.utils.NOTE_DESK_DATABASE_NAME
import ghanbari.maziar.notedesk.utils.NO_FOLDER
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    fun provideDatabase(@ApplicationContext context: Context): DatabaseND {
        lateinit var database: DatabaseND
        database = Room.databaseBuilder(
            context, DatabaseND::class.java,
            NOTE_DESK_DATABASE_NAME
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    //create default folders
                    CoroutineScope(Dispatchers.IO).launch {
                        database.folderDao().insertFolder(
                            FolderEntity(
                                img = R.drawable.ic_baseline_folder_off_24_folderation,
                                title = NO_FOLDER
                            )
                        )
                        database.folderDao().insertFolder(
                            FolderEntity(
                                img = R.drawable.ic_twotone_work_24_folderaion,
                                title = "کار"
                            )
                        )
                        database.folderDao().insertFolder(
                            FolderEntity(
                                img = R.drawable.ic_baseline_school_24_folderation,
                                title = "آموزش"
                            )
                        )
                        database.folderDao().insertFolder(
                            FolderEntity(
                                img = R.drawable.ic_baseline_shopping_cart_folderation_24,
                                title = "خرید"
                            )
                        )
                    }
                }
            })
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        return database
    }

    //provide note dao
    @Provides
    @Singleton
    fun provideNoteDao(db: DatabaseND) = db.noteDao()

    @Provides
    @Singleton
    fun provideFolderDao(db: DatabaseND) = db.folderDao()

}