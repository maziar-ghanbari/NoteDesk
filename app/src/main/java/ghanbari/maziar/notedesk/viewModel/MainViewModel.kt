package ghanbari.maziar.notedesk.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ghanbari.maziar.notedesk.R
import ghanbari.maziar.notedesk.data.model.FolderEntity
import ghanbari.maziar.notedesk.data.model.NoteAndFolder
import ghanbari.maziar.notedesk.data.model.NoteEntity
import ghanbari.maziar.notedesk.data.repository.MainRepository
import ghanbari.maziar.notedesk.ui.main.*
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
        MutableStateFlow<MyResponse<MutableList<NoteAndFolder>>>(MyResponse.loading())
    val databaseNotesStateFlow get() = _databaseNotesStateFlow

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

    //set observer to note operator
    private fun operatorNoteCollector() = viewModelScope.launch(IO) {
        operatorNotesStateFlow.collect {
            connectionDatabaseAndOperator(it, databaseNotesStateFlow.value)
        }
    }

    //deleteNote
    fun deleteNote(note: NoteEntity) = viewModelScope.launch(IO) { repository.deleteNote(note) }


    //***folders***
    val receiveFoldersLiveData = MutableLiveData<MyResponse<MutableList<FolderEntity>>>()
    fun getAllFolders() = viewModelScope.launch(IO) {
        repository.getAllFolders().collect {
            receiveFoldersLiveData.postValue(it)
        }
    }

    //operation folders
    fun operationFolderAtTop() = mutableListOf(
        FolderEntity(img = R.drawable.ic_baseline_density_small_24_folderation, title = ALL_FOLDER),
        FolderEntity(img = R.drawable.ic_baseline_add_24, title = ADD_FOLDER)
    )

    //deleteFolder
    fun deleteFolder(folder: FolderEntity) = viewModelScope.launch(IO) { repository.deleteFolder(folder) }
}