package ghanbari.maziar.notedesk.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ghanbari.maziar.notedesk.R
import ghanbari.maziar.notedesk.data.model.FolderEntity
import ghanbari.maziar.notedesk.data.repository.AddEditFolderRepository
import ghanbari.maziar.notedesk.utils.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditFolderViewModel @Inject constructor(private val repository: AddEditFolderRepository) :
    ViewModel() {
    //get suggestion folder icons
    val folderSuggestionIconLiveData = MutableLiveData<MyResponse<MutableList<Int>>>()
    var folderNoSuggestionTitle = mutableListOf<String>()

    //get all folders to prevent repeating name or icon folder
    fun getSuggestIconTitleFolder() = viewModelScope.launch(IO) {
        repository.getAllFolders().collect {
            //if it is not error , filter icon folder suggestion that used
            if (it.state == MyResponse.DataState.ERROR) {
                folderSuggestionIconLiveData.postValue(
                    MyResponse(
                        state = it.state,
                        errorMessage = it.errorMessage
                    )
                )
            } else {
                //get repeated title that no suggestion
                //get suggested icons
                it.data?.let { data ->
                    //used folder
                    val titles = mutableListOf<String>()
                    val icons = allIconFolder()
                    titles.addAll(defaultTitlesOperation)

                    data.forEach { folder ->
                        titles.add(folder.title)
                        if (icons.contains(folder.img)) {
                            icons.remove(folder.img)
                        }
                    }
                    //send no suggested title
                    folderNoSuggestionTitle = titles
                    //send suggested icons
                    folderSuggestionIconLiveData.postValue(MyResponse.success(icons))
                }
            }
        }
    }

    //insert folder
    fun insertFolder(folder: FolderEntity) =
        viewModelScope.launch(IO) { repository.insertFolder(folder) }

    //all folder
    private fun allIconFolder() = mutableListOf(
        R.drawable.ic_baseline_cookie_24_folderation,
        R.drawable.ic_baseline_format_list_bulleted_24_folderation,
        R.drawable.ic_baseline_kitchen_24_folderation,
        R.drawable.ic_baseline_kitesurfing_24_folderation,
        R.drawable.ic_baseline_school_24_folderation,
        R.drawable.ic_baseline_shopping_cart_folderation_24,
        R.drawable.ic_twotone_home_24_folderation,
        R.drawable.ic_twotone_work_24_folderaion,
        R.drawable.ic_twotone_lightbulb_24_folderation
    )

    //default folders
    private val defaultTitlesOperation = mutableListOf(
        "",
        ADD_FOLDER,
        EDIT_FOLDER,
        ALL_FOLDER,
        NO_FOLDER,
        DELETE_FOLDER
    )
}