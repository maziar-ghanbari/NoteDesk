package ghanbari.maziar.notedesk.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import ghanbari.maziar.notedesk.R
import ghanbari.maziar.notedesk.data.model.NoteAndFolder
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilesExporter @Inject constructor(@ApplicationContext private val context: Context) {

    //root
    private val documentsPath = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOCUMENTS + "/" + context.getString(
            R.string.app_name_persian
        )
    )


    suspend fun exportAllNotes(noteAndFolders : MutableList<NoteAndFolder>) : String = withContext(IO){
        //try catch for exception when exporting note
        try {
            if (documentsPath.exists()){
                //delete this file with all its children.
                documentsPath.deleteRecursively()
            }
            if (!documentsPath.exists()) {
                //make this files again
                documentsPath.mkdirs()
            }

            val directory = documentsPath.toString()
            noteAndFolders.forEach { noteAndFolder ->

                val folderName = noteAndFolder.folder.title
                val folder = File("$directory/$folderName")
                Log.e(TAG, "exportNote: ${folder.path}")
                if (!folder.exists()) {
                    folder.mkdirs()
                }
                val noteTitle =
                    "${noteAndFolder.note.title}${noteAndFolder.note.id}".convertByPolicyNoteDeskFileNaming()
                val noteTxt = File.createTempFile(noteTitle, ".txt", folder)
                noteTxt.writeText(noteAndFolder.toString())
            }

            "$directory✅"
        }catch (e : Exception){
            e.message.toString()
        }
    }


}