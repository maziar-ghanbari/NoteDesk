package ghanbari.maziar.notedesk.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ghanbari.maziar.notedesk.R
import ghanbari.maziar.notedesk.data.model.FolderEntity
import ghanbari.maziar.notedesk.data.model.NoteAndFolder
import ghanbari.maziar.notedesk.data.model.NoteEntity
import ghanbari.maziar.notedesk.data.repository.MainRepository
import ghanbari.maziar.notedesk.utils.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    //***notes***
    //in any data change in livedatas
    //mix database note list with operators and send the result to view
    private fun connectionDatabaseAndOperator(
        noteOperator: NoteOperator?,
        noteDatabase: MyResponse<MutableList<NoteAndFolder>>?
    ) {
        receiveNotesLiveData.postValue(MyResponse.loading())
        when (noteDatabase?.state) {
            MyResponse.DataState.SUCCESS -> {
                noteOperator?.let { operator ->
                    noteDatabase.data?.let { database ->
                        val strQuery = operator.strQuery ?: ""
                        val intQuery = operator.intQuery ?: 0
                        when (operator.operator) {
                            NoteOperator.Operators.BY_DATE_SEARCH -> {
                                val operatedData = database.byDateSearch(strQuery)
                                if (operatedData.isNotEmpty()) {
                                    receiveNotesLiveData.postValue(MyResponse.success(operatedData))
                                } else {
                                    receiveNotesLiveData.postValue(MyResponse.empty())
                                }
                            }
                            NoteOperator.Operators.BY_FOLDER_SEARCH -> {
                                val operatedData = database.byFolderSearch(strQuery)
                                if (operatedData.isNotEmpty()) {
                                    receiveNotesLiveData.postValue(MyResponse.success(operatedData))
                                } else {
                                    receiveNotesLiveData.postValue(MyResponse.empty())
                                }
                            }
                            NoteOperator.Operators.BY_TITLE_SEARCH -> {
                                val operatedData = database.byTitleSearch(strQuery)
                                if (operatedData.isNotEmpty()) {
                                    receiveNotesLiveData.postValue(MyResponse.success(operatedData))
                                } else {
                                    receiveNotesLiveData.postValue(MyResponse.empty())
                                }
                            }
                            NoteOperator.Operators.BY_ID_SEARCH -> {
                                val operatedData = database.byIdSearch(intQuery)
                                if (operatedData.isNotEmpty()) {
                                    receiveNotesLiveData.postValue(MyResponse.success(operatedData))
                                } else {
                                    receiveNotesLiveData.postValue(MyResponse.empty())
                                }
                            }
                            NoteOperator.Operators.BY_PRIORITY_SEARCH -> {
                                val operatedData = database.byPrioritySearch(strQuery)
                                if (operatedData.isNotEmpty()) {
                                    receiveNotesLiveData.postValue(MyResponse.success(operatedData))
                                } else {
                                    receiveNotesLiveData.postValue(MyResponse.empty())
                                }
                            }
                            NoteOperator.Operators.ORDER_BY_PIN_SEARCH -> {
                                val operatedData = database.orderByPinSearch()
                                if (operatedData.isNotEmpty()) {
                                    receiveNotesLiveData.postValue(MyResponse.success(operatedData))
                                } else {
                                    receiveNotesLiveData.postValue(MyResponse.empty())
                                }
                            }
                            else -> {
                                if (database.isNotEmpty()) {
                                    receiveNotesLiveData.postValue(MyResponse.success(database))
                                } else {
                                    receiveNotesLiveData.postValue(MyResponse.empty())
                                }
                            }
                        }
                    }
                }
            }
            MyResponse.DataState.EMPTY -> receiveNotesLiveData.postValue(MyResponse.empty())
            MyResponse.DataState.ERROR -> receiveNotesLiveData.postValue(
                MyResponse.error(
                    noteDatabase.errorMessage.toString()
                )
            )
            else -> {}
        }
    }

    //stateFlows for database note and operator notes and receive notes
    val receiveNotesLiveData = MutableLiveData<MyResponse<MutableList<NoteAndFolder>>>()
    val _operatorNotesStateFlow = MutableStateFlow<NoteOperator>(NoteOperator.getAllNotes())
    private val operatorNotesStateFlow get() = _operatorNotesStateFlow //collect
    private val _databaseNotesStateFlow =
        MutableStateFlow<MyResponse<MutableList<NoteAndFolder>>>(MyResponse.empty())
    private val databaseNotesStateFlow get() = _databaseNotesStateFlow

    //init database notes content
    fun connectToModel() = viewModelScope.launch(IO) {
        //set collector to stateFlows
        databaseNoteCollector()
        operatorNoteCollector()
        repository.getAllNotesRelated().collect {
            //if they are same , it is no important because the important is changing data of objects in list
            if (databaseNotesStateFlow.value.data == it.data) {
                it.errorMessage = "${it.errorMessage}."
            }
            //check if is (error or empty or loading) or (success)

            _databaseNotesStateFlow.emit(it)
        }
    }

    //set observer to database note
    private fun databaseNoteCollector() = viewModelScope.launch(IO) {
        databaseNotesStateFlow.collect {
            connectionDatabaseAndOperator(operatorNotesStateFlow.value, it)
        }
    }

    //set observer to note observer
    private fun operatorNoteCollector() = viewModelScope.launch(IO) {
        operatorNotesStateFlow.collect {
            connectionDatabaseAndOperator(it, databaseNotesStateFlow.value)
        }
    }

    //insert a note
    fun insertNote(note: NoteEntity) = viewModelScope.launch(IO) { repository.insertNote(note) }

    //updateNote
    private fun updateNote(notes: NoteEntity) =
        viewModelScope.launch(IO) { repository.updateNote(notes) }

    //deleteNote
    fun deleteNote(note: NoteEntity) = viewModelScope.launch(IO) { repository.deleteNote(note) }


    //***folders***
    val receiveFoldersLiveData = MutableLiveData<MyResponse<MutableList<FolderEntity>>>()
    fun getAllFolders() = viewModelScope.launch(IO) {
        repository.getAllFolders().collect {
            receiveFoldersLiveData.postValue(it)
        }
    }

    //default folder after first launch
    fun getDefaultFolders() = listOf(
        FolderEntity(img = R.drawable.ic_baseline_folder_off_24_folderation, title = NO_FOLDER),
        FolderEntity(img = R.drawable.ic_twotone_work_24_folderaion, title = "کار"),
        FolderEntity(img = R.drawable.ic_baseline_school_24_folderation, title = "آموزش"),
        FolderEntity(img = R.drawable.ic_baseline_shopping_cart_folderation_24, title = "خرید")
    )

    //operation folders
    fun operationFolderAtTop() = mutableListOf(
        FolderEntity(img = R.drawable.ic_baseline_density_small_24_folderation, title = ALL_FOLDER),
        FolderEntity(img = R.drawable.ic_baseline_add_24, title = ADD_FOLDER)
    )

    //insert a folder
    fun insertFolder(folder: FolderEntity) =
        viewModelScope.launch(IO) { repository.insertFolder(folder) }

    //updateFolder
    fun updateFolder(folder: FolderEntity) =
        viewModelScope.launch(IO) { repository.updateFolder(folder) }

    //deleteFolder
    fun deleteFolder(folder: FolderEntity) = viewModelScope.launch(IO) {
        repository.deleteFolder(folder)
        /*val list = mutableListOf<NoteEntity>()
        receiveNotesLiveData.value?.data?.forEach {
            //export notes in folder witch deleted
            if (it.folderTitle == folder.title || it.folderImg == folder.img) {
                it.folderTitle = NO_FOLDER
                it.folderImg = R.drawable.ic_baseline_folder_off_24_folderation
                list.add(it)
            }
        }
        //varag insert notes
        updateNote(notes = list.map { it }.toTypedArray())*/
    }

    /*FolderEntity(0, R.drawable.ic_baseline_add_24, "add"),
            FolderEntity(1, R.drawable.ic_baseline_search_24, "search"),
            FolderEntity(2, R.drawable.ic_outline_stairs_24, "priority"),
            FolderEntity(3, R.drawable.ic_twotone_door_sliding_24, "slide"),
            FolderEntity(4, R.drawable.ic_baseline_add_24, "add"),
            FolderEntity(5, R.drawable.ic_baseline_search_24, "search"),
            FolderEntity(6, R.drawable.ic_outline_stairs_24, "priority"),
            FolderEntity(7, R.drawable.ic_twotone_door_sliding_24, "slide"),
            FolderEntity(8, R.drawable.ic_baseline_add_24, "add"),
            FolderEntity(9, R.drawable.ic_baseline_search_24, "search"),
            FolderEntity(10, R.drawable.ic_outline_stairs_24, "priority"),
            FolderEntity(11, R.drawable.ic_twotone_door_sliding_24, "slide")*/
}