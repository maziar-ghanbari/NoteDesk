package ghanbari.maziar.notedesk.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ghanbari.maziar.notedesk.data.model.NoteAndFolder
import ghanbari.maziar.notedesk.data.model.NoteEntity
import ghanbari.maziar.notedesk.data.repository.NoteContentRepository
import ghanbari.maziar.notedesk.utils.MyResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteContentViewModel @Inject constructor(private val repository: NoteContentRepository) : ViewModel(){
    //note response
    val noteLiveData = MutableLiveData<MyResponse<MutableList<NoteAndFolder>>>()

    //get note related by id
    fun getNoteRelatedById(id : Int) = viewModelScope.launch(IO) {
        repository.getNoteRelatedById(id).collect{
            noteLiveData.postValue(it)
        }
    }

    //delete note
    fun deleteNote(note : NoteEntity) = viewModelScope.launch(IO) { repository.deleteNote(note) }
}