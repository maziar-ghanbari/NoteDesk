package ghanbari.maziar.notedesk.ui.main.pages.addEditNote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ghanbari.maziar.notedesk.R
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
                        val data = it.data ?: mutableListOf()
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
                        requireActivity().snackBar(
                            R.color.holo_red_dark,
                            it.errorMessage.toString()
                        )
                        dismiss()
                    }
                    else -> {}
                }
            }
            //pin checkbox
            pinCheckbox.setOnCheckedChangeListener { _, isChecked ->
                note.isPinned = isChecked
            }
            //delete note
            deleteNoteTxt.setOnClickListener {
                //delete note show alert dialog
                requireActivity().alertDialog(
                    getString(R.string.delete_note_title),
                    "آیا از حذف یادداشت (${titleNoteEdtTxt.text}) مطمئنید؟"
                ){
                    requireActivity().snackBar(
                        R.color.holo_green_dark,
                        getString(R.string.delete_note_successfully)
                    )
                    viewModel.deleteNote(note)
                    dismiss()
                }
            }
            //save update
            saveNoteBtn.setOnClickListener {
                if (titleNoteEdtTxt.text.isEmpty() || desNoteEdtTxt.text.isEmpty()) {
                    //des & title can not be null
                    requireActivity().snackBar(
                        R.color.holo_blue_dark,
                        getString(R.string.note_not_null_des_title),dialog?.window?.decorView
                    )
                    return@setOnClickListener
                }else if (titleNoteEdtTxt.text.length < 3){
                    //
                    requireActivity().snackBar(
                        R.color.holo_blue_dark,
                        getString(R.string.empty_title_of_note_error),dialog?.window?.decorView
                    )
                    return@setOnClickListener
                }
                note.title = titleNoteEdtTxt.text.toString()
                note.des = desNoteEdtTxt.text.toString()

                if (isForUpdate) {
                    viewModel.updateNote(note)
                    //update
                    requireActivity().snackBar(
                        R.color.holo_green_dark,
                        getString(R.string.update_note_successfully)
                    )
                } else {
                    //insert
                    note.date = getCurrentDate()
                    note.time = getCurrentTime()
                    viewModel.insertNote(note)
                    requireActivity().snackBar(
                        R.color.holo_green_dark,
                        getString(R.string.insert_note_successfully)
                    )
                }

                dismiss()
            }
        }
    }

    private fun checkArg() {
        binding?.apply {
            arguments?.let {
                val id = it.getInt(ARG_ID_NOTE_UPDATE, -1)

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
                                requireActivity().snackBar(
                                    R.color.holo_red_dark,
                                    "خطا"
                                )
                                dismiss()
                            }
                            MyResponse.DataState.ERROR -> {
                                requireActivity().snackBar(
                                    R.color.holo_red_dark,
                                    response.errorMessage.toString()
                                )
                                dismiss()
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
        return pdformater1.format(pdate).englishToIranianNumber() //1396/05/20
    }

    private fun getCurrentTime(): String {
        val pdate = PersianDate()
        val pdformater1 = PersianDateFormat("H:i")
        return pdformater1.format(pdate).englishToIranianNumber() //01:10
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}