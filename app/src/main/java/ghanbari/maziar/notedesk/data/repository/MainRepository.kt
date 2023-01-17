package ghanbari.maziar.notedesk.data.repository

import android.util.Log
import ghanbari.maziar.notedesk.data.local.FolderDao
import ghanbari.maziar.notedesk.data.local.NoteDao
import ghanbari.maziar.notedesk.data.model.FolderEntity
import ghanbari.maziar.notedesk.data.model.NoteAndFolder
import ghanbari.maziar.notedesk.data.model.NoteEntity
import ghanbari.maziar.notedesk.utils.MyResponse
import ghanbari.maziar.notedesk.utils.TAG
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainRepository @Inject constructor(private val noteDao: NoteDao, private val folderDao : FolderDao) {

    //get all notes by considering all of states
    fun getAllNotesRelated() = flow<MyResponse<MutableList<NoteAndFolder>>>{
        //collecting
        noteDao.getAllNotesRelated().collect {
            if (it.isEmpty()){
                //notes are empty
                emit(MyResponse.empty())
            }else {
                //notes are success
                emit(MyResponse.success(it))
            }
        }
    }.flowOn(IO)
        /*.catch { emit(MyResponse.error(it.message.toString()))
            Log.e(TAG, it.message.toString() )}
*/
    suspend fun updateNote(notes : NoteEntity) = noteDao.updateNote(notes)
    suspend fun deleteNote(note : NoteEntity) = noteDao.deleteNote(note)
    suspend fun insertNote(note : NoteEntity) = noteDao.insertNote(note)

    //folder
    fun getAllFolders() = flow<MyResponse<MutableList<FolderEntity>>>{
        folderDao.getAllFolders().collect{
            //no empty note declared in kharazmi
            emit(MyResponse.success(it))
        }
    }.flowOn(IO)
        .catch { emit(MyResponse.error(it.message.toString())) }

    suspend fun updateFolder(folder: FolderEntity) = folderDao.updateFolder(folder)
    suspend fun deleteFolder(folder: FolderEntity) = folderDao.deleteFolder(folder)
    suspend fun insertFolder(folder: FolderEntity) = folderDao.insertFolder(folder)
}