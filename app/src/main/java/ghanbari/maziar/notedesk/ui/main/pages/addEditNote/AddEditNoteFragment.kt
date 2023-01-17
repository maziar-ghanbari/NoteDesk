package ghanbari.maziar.notedesk.ui.main.pages.addEditNote

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ghanbari.maziar.notedesk.data.model.FolderEntity
import ghanbari.maziar.notedesk.data.model.NoteEntity
import ghanbari.maziar.notedesk.databinding.FragmentAddEditNoteBinding
import ghanbari.maziar.notedesk.utils.*
import ghanbari.maziar.notedesk.viewModel.AddEditNoteViewModel
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat


@AndroidEntryPoint
class AddEditNoteFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddEditNoteBinding? = null
    private val binding get() = _binding
    private val viewModel: AddEditNoteViewModel by viewModels()
    private var isForUpdate = false
    private var prioritySelected: PriorityNote? = null
    //folder for selecting current folder in note edit fragment
    //private var folder = FolderEntity()
    private var note = NoteEntity()
    private var oldFolderLiveData = MutableLiveData<FolderEntity>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddEditNoteBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            //check arg
            checkArg()
            //close
            closeFragment.setOnClickListener { dismiss() }
            //spinner priority
            prioritySelectionSpinner.setUpPriorityIconWithAdapter(viewModel.getArrayPriorityItemsSpinner()) {
                note.priority = it!!.name
            }
            //get all folder
            //spinner folder
            viewModel.getAllFolders()
            viewModel.folderLiveData.observe(viewLifecycleOwner) {
                when (it.state) {
                    MyResponse.DataState.SUCCESS -> {
                        val data = it.data ?: mutableListOf<FolderEntity>()
                        //spinner folder
                        folderSelectionSpinner.setUpFolderItemsWithAdapter(
                            data
                        ) { folderSelected ->
                            note.folder_id = folderSelected.id
                        }
                        //select old folder
                        oldFolderLiveData.observe(viewLifecycleOwner) { oldFolderSelected ->
                            i@for (i in data) {
                                if (i.id == oldFolderSelected.id) {
                                    val index = data.indexOf(i)
                                    folderSelectionSpinner.setSelection(index)
                                    break@i
                                }
                            }
                        }
                    }
                    MyResponse.DataState.ERROR -> {

                    }
                    else -> {}
                }
            }
            //pin checkbox
            pinCheckbox.setOnCheckedChangeListener { _, isChecked ->
                note.isPinned = isChecked
            }
            //delete
            deleteNoteTxt.setOnClickListener {
                //TODO alert dialog for delete
                viewModel.deleteNote(note)
                dismiss()
            }
            //save update
            saveNoteBtn.setOnClickListener {
                if (titleNoteEdtTxt.text.isEmpty() || desNoteEdtTxt.text.isEmpty()) {
                    Log.e(
                        TAG,
                        "onViewCreated: ${titleNoteEdtTxt.text.isEmpty()} ${desNoteEdtTxt.text.isEmpty()} ${prioritySelected == null}"
                    )
                    return@setOnClickListener
                }
                //TODO snackbar for null items
                note.title = titleNoteEdtTxt.text.toString()
                note.des = desNoteEdtTxt.text.toString()

                if (isForUpdate) {
                    viewModel.updateNote(note)
                } else {
                    note.date = getCurrentDate()
                    note.time = getCurrentTime()
                    viewModel.insertNote(note)
                }
                //TODO snackBar for success
                dismiss()
            }
        }
    }

    private fun checkArg() {
        binding?.apply {
            arguments?.let {
                val id = it.getInt(ARG_ID_NOTE_UPDATE, -1)

                Log.e(TAG, "onClickAdapters: $id")
                if (id == -1) return
                viewModel.getNoteRelatedById(id)
                viewModel.noteLiveData.observe(viewLifecycleOwner) { response ->
                    response.data?.get(0)?.let { noteUpdate ->
                        when (response.state) {
                            MyResponse.DataState.SUCCESS -> {
                                note.id = id
                                note.time = noteUpdate.note.time
                                note.date = noteUpdate.note.date
                                titleNoteEdtTxt.setText(noteUpdate.note.title)
                                desNoteEdtTxt.setText(noteUpdate.note.des)
                                pinCheckbox.isChecked = noteUpdate.note.isPinned
                                note.folder_id = noteUpdate.note.folder_id
                                //show delete option when it is for update (it is for insert)
                                deleteNoteTxt.isShown(true)
                                //select old priority
                                val (_, priorityNote) = viewModel.getArrayPriorityItemsSpinner()
                                    .unzip()
                                val statePriority = when (noteUpdate.note.priority) {
                                    PriorityNote.LOW.name -> {
                                        PriorityNote.LOW
                                    }
                                    PriorityNote.NORMAL.name -> {
                                        PriorityNote.NORMAL
                                    }
                                    PriorityNote.HIGH.name -> {
                                        PriorityNote.HIGH
                                    }
                                    else -> {
                                        PriorityNote.LOW
                                    }
                                }
                                val priorityId = priorityNote.indexOf(statePriority)
                                prioritySelectionSpinner.setSelection(priorityId)
                                //select old folder
                                oldFolderLiveData.value = noteUpdate.folder

                                //update
                                isForUpdate = true
                            }
                            MyResponse.DataState.EMPTY -> {
                                //TODO emptyError
                            }
                            MyResponse.DataState.ERROR -> {
                                //TODO snackbar error
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun getCurrentDate(): String {
        val pdate = PersianDate()
        val pdformater1 = PersianDateFormat("Y/m/d")
        return pdformater1.format(pdate) //1396/05/20
    }

    private fun getCurrentTime(): String {
        val pdate = PersianDate()
        val pdformater1 = PersianDateFormat("H:i")
        return pdformater1.format(pdate) //01:10
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}