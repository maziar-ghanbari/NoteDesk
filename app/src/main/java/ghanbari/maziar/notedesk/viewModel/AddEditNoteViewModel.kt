package ghanbari.maziar.notedesk.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ghanbari.maziar.notedesk.R
import ghanbari.maziar.notedesk.data.model.FolderEntity
import ghanbari.maziar.notedesk.data.model.NoteAndFolder
import ghanbari.maziar.notedesk.data.model.NoteEntity
import ghanbari.maziar.notedesk.data.repository.AddEditNoteRepository
import ghanbari.maziar.notedesk.utils.MyResponse
import ghanbari.maziar.notedesk.utils.PriorityNote
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(private val repository : AddEditNoteRepository) : ViewModel() {

    //received Note
    val noteLiveData = MutableLiveData<MyResponse<MutableList<NoteAndFolder>>>()

    fun getNoteRelatedById(id : Int) = viewModelScope.launch(IO) {
        repository.getNoteRelatedById(id).collect{
            noteLiveData.postValue(it)
        }
    }
    //insert a note
    fun insertNote(note: NoteEntity) = viewModelScope.launch(IO) { repository.insertNote(note) }

    //updateNote
    fun updateNote(note: NoteEntity) = viewModelScope.launch(IO) { repository.updateNote(note) }

    //deleteNote
    fun deleteNote(note: NoteEntity) = viewModelScope.launch(IO) { repository.deleteNote(note) }

    //***folders
    val folderLiveData = MutableLiveData<MyResponse<MutableList<FolderEntity>>>()

    //get all folders
    fun getAllFolders() = viewModelScope.launch(IO) {
        repository.getAllFolders().collect{
            folderLiveData.postValue(it)
        }
    }

    //priority item spinner custom
    fun getArrayPriorityItemsSpinner() = arrayOf<Pair<Int,PriorityNote?>>(
        Pair(R.color.priority_low, PriorityNote.LOW),
        Pair(R.color.priority_normal, PriorityNote.NORMAL),
        Pair(R.color.priority_high, PriorityNote.HIGH)
    )
}

