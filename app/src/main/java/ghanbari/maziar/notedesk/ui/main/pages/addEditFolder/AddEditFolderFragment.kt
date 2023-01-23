package ghanbari.maziar.notedesk.ui.main.pages.addEditFolder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ghanbari.maziar.notedesk.R
import ghanbari.maziar.notedesk.data.model.FolderEntity
import ghanbari.maziar.notedesk.databinding.FragmentAddEditFolderBinding
import ghanbari.maziar.notedesk.utils.*
import ghanbari.maziar.notedesk.viewModel.AddEditFolderViewModel

@AndroidEntryPoint
class AddEditFolderFragment : DialogFragment() {

    private var _binding: FragmentAddEditFolderBinding? = null
    private val binding get() = _binding

    private val viewModel: AddEditFolderViewModel by viewModels()

    //folder for creating
    private val folder = FolderEntity()

    //is for update
    private var isForUpdate: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddEditFolderBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            //checkArgs
            checkArgs()
            //get suggestions
            viewModel.getSuggestIconTitleFolder()
            //close fragment
            closeImg.setOnClickListener {
                dismiss()
            }
            //cancelable fragment
            isCancelable = true
            //icon suggestion folder
            viewModel.folderSuggestionIconLiveData.observe(viewLifecycleOwner) {
                when (it.state) {
                    MyResponse.DataState.SUCCESS -> {
                        //setUpSpinner
                        it.data?.let { data ->
                            //if it is for update add icon of folder witch is for update also, at Index = 0
                            if (isForUpdate) {
                                data.add(0, folder.img)
                                folderTitleEdt.setText(folder.title)
                            }
                            folderIconSpinner.setUpIconSpinner(data) { icon ->
                                folder.img = icon
                            }
                        }
                    }
                    MyResponse.DataState.ERROR -> {
                        requireActivity().snackBar(R.color.holo_red_dark,"خطا : ${it.errorMessage.toString()}")
                        dismiss()
                    }
                    else -> {}
                }
            }
            //update operation result message
            /*viewModel.resultMessageLiveData.observe(viewLifecycleOwner) {
                when (it.state) {
                    MyResponse.DataState.SUCCESS -> {
                        //show success update snackBar
                        Snackbar.make(
                            root,
                            getString(R.string.success_update_folder_properties_in_notes),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    MyResponse.DataState.ERROR -> {
                        //show error update snackBar
                        Snackbar.make(
                            root,
                            getString(R.string.fail_update_folder_properties_in_notes),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    else -> {}
                }
            }*/
            //******
            addFolderNewBtn.setOnClickListener {
                //folder title edt
                val folderTitle = folderTitleEdt.text.toString()
                //folder title can not be empty
                if (folderTitle.isEmpty()){
                    requireActivity().snackBar(
                        R.color.holo_blue_dark,
                        getString(R.string.empty_title_of_folder_error)
                    )
                    return@setOnClickListener
                }
                //return if title is not suitable
                val noSuitableTitles = viewModel.folderNoSuggestionTitle
                for (noPermission in noSuitableTitles){
                    //if old title did not changed it is no problem
                    if (folderTitle == folder.title) continue
                    //if folder title determined by user is for another folder show error message
                    // and do not update or insert in room or dismiss fragment
                    if (folderTitle == noPermission) {
                        requireActivity().snackBar(R.color.holo_blue_dark,getString(R.string.repeated_subject_folder))
                        return@setOnClickListener
                    }
                }
                //folder title filling
                folder.title = folderTitle

                if (isForUpdate) {
                    viewModel.updateFolder(folder)
                    requireActivity().snackBar(R.color.holo_green_dark,getString(R.string.folder_upgraded_successfully))
                } else {
                    //insert folder
                    viewModel.insertFolder(folder)
                    requireActivity().snackBar(R.color.holo_green_dark,getString(R.string.folder_insert_successfully))
                }
                dismiss()
            }
        }
    }

    private fun checkArgs() {
        Log.e(TAG, "checkArgs: ")
        binding?.apply {
            arguments?.let {
                val id = it.getInt(ARG_ID_FOLDER_UPDATE, -1)
                val title = it.getString(ARG_TITLE_FOLDER_UPDATE, "")
                val icon = it.getInt(ARG_ICON_FOLDER_UPDATE, -1)
                Log.e(TAG, "checkArgs: $icon| $id| $title|")
                if (id == -1 || icon == -1) return
                //update view
                titleDialogTxt.text = getString(R.string.subject_fragment_edit_folder)
                isForUpdate = true

                folder.id = id
                folder.title = title
                folder.img = icon
                viewModel.oldFolderData.title = title
                viewModel.oldFolderData.img = icon
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}