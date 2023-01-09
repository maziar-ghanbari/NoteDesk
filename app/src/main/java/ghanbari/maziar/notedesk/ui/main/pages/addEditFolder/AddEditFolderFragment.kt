package ghanbari.maziar.notedesk.ui.main.pages.addEditFolder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ghanbari.maziar.notedesk.data.model.FolderEntity
import ghanbari.maziar.notedesk.databinding.FragmentAddEditFolderBinding
import ghanbari.maziar.notedesk.utils.MyResponse
import ghanbari.maziar.notedesk.utils.setUpIconSpinner
import ghanbari.maziar.notedesk.viewModel.AddEditFolderViewModel

@AndroidEntryPoint
class AddEditFolderFragment : DialogFragment() {

    private var _binding : FragmentAddEditFolderBinding? = null
    private val binding get() = _binding

    private val viewModel : AddEditFolderViewModel by viewModels()
    //folder for creating
    private val folder = FolderEntity()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddEditFolderBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            //get suggestions
            viewModel.getSuggestIconTitleFolder()
            //close fragment
            closeImg.setOnClickListener {
                dismiss()
            }
            //cancelable fragment
            isCancelable = true
            //icon suggestion folder
            viewModel.folderSuggestionIconLiveData.observe(viewLifecycleOwner){
                when(it.state){
                    MyResponse.DataState.SUCCESS -> {
                        //setUpSpinner
                        it.data?.let { data ->
                            folderIconSpinner.setUpIconSpinner(data) { icon ->
                                folder.img = icon
                            }
                        }
                    }
                    MyResponse.DataState.ERROR -> {
                        //TODO snackbar error and close fragment
                    }
                    else ->{}
                }
            }
            //no suggestion title
            //******
            addFolderNewBtn.setOnClickListener{
                //folder title edt
                val folderTitle = folderTitleEdt.text.toString()
                //return if title is not suitable
                val noSuitableTitles = viewModel.folderNoSuggestionTitle
                noSuitableTitles.forEach {
                    if (folderTitle == it) {
                        //TODO title repeated snackbar
                        return@setOnClickListener
                    }
                }
                //folder title filling
                folder.title = folderTitle
                //insert folder
                viewModel.insertFolder(folder)
                //TODO snackbar SUCCESS insert new folder
                dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}