package ghanbari.maziar.notedesk.ui.main.pages.addEditNote

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
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

    private var note = NoteEntity()


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
            viewModel.getAllFodders()
            viewModel.folderLiveData.observe(viewLifecycleOwner) {
                when (it.state) {
                    MyResponse.DataState.SUCCESS -> {
                        folderSelectionSpinner.setUpFolderItemsWithAdapter(
                            it.data ?: mutableListOf<FolderEntity>()
                        ) { folder ->
                            note.folderImg = folder.img
                            note.folderTitle = folder.title
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
                dismiss()
            }
        }
    }

    private fun checkArg() {
        binding?.apply {
            arguments?.let {
                val id = it.getInt(ARG_ID_NOTE_UPDATE, -1)
                Toast.makeText(requireContext(), id.toString(), Toast.LENGTH_SHORT).show()
                if (id == -1) return
                viewModel.getNoteById(id)
                viewModel.noteLiveData.observe(viewLifecycleOwner) { response ->
                    response.data?.get(0)?.let { noteUpdate ->
                        when (response.state) {
                            MyResponse.DataState.SUCCESS -> {
                                note.id = id
                                note.time = noteUpdate.time
                                note.date = noteUpdate.date
                                titleNoteEdtTxt.setText(noteUpdate.title)
                                desNoteEdtTxt.setText(noteUpdate.des)
                                pinCheckbox.isChecked = noteUpdate.isPinned
                                //show delete option when it is for update (it is for insert)
                                deleteNoteTxt.isShown(true)
                                //select old priority
                                val (color, priorityNote) = viewModel.getArrayPriorityItemsSpinner()
                                    .unzip()
                                val statePriority = when (noteUpdate.priority) {
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
                                // TODO
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