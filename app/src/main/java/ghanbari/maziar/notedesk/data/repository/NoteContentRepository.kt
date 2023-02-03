package ghanbari.maziar.notedesk.data.repository

import ghanbari.maziar.notedesk.data.local.NoteDao
import ghanbari.maziar.notedesk.data.model.NoteAndFolder
import ghanbari.maziar.notedesk.data.model.NoteEntity
import ghanbari.maziar.notedesk.utils.MyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class NoteContentRepository @Inject constructor(private val noteDao: NoteDao) {

    //get all notes by considering all of states
    fun getNoteRelatedById(id : Int) = flow<MyResponse<MutableList<NoteAndFolder>>> {
        noteDao.getNoteRelatedById(id).collect {
            if (it.isEmpty()) {
                //notes are empty
                emit(MyResponse.empty())
            } else {
                //notes are success
                emit(MyResponse.success(it))
            }
        }
    }.flowOn(Dispatchers.IO)
        .catch { emit(MyResponse.error(it.message.toString())) }

    //delete note
    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)

}