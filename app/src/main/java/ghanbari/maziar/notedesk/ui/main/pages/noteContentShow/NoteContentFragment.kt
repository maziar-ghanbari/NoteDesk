package ghanbari.maziar.notedesk.ui.main.pages.noteContentShow

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ghanbari.maziar.notedesk.R
import ghanbari.maziar.notedesk.data.model.NoteAndFolder
import ghanbari.maziar.notedesk.databinding.FragmentNoteContentBinding
import ghanbari.maziar.notedesk.ui.MainActivity
import ghanbari.maziar.notedesk.utils.*
import ghanbari.maziar.notedesk.viewModel.NoteContentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class NoteContentFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentNoteContentBinding? = null
    private val binding get() = _binding

    private val viewModel: NoteContentViewModel by viewModels()

    @Inject
    lateinit var clipboardManager: ClipboardManager

    @Inject
    lateinit var filesExporter: FilesExporter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteContentBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            arguments?.let { bundle ->
                //check id argument if it is -1 return ,else continue
                val id = bundle.getInt(ARG_ID_NOTE_SHOW, -1)
                if (id == -1) return
                //search note by its id
                viewModel.getNoteRelatedById(id)
                viewModel.noteLiveData.observe(viewLifecycleOwner) {
                    when (it.state) {
                        MyResponse.DataState.ERROR -> {
                            requireActivity().snackBar(
                                R.color.holo_red_dark,
                                it.errorMessage.toString(),
                                dialog?.window?.decorView
                            )
                        }
                        MyResponse.DataState.EMPTY -> {
                            requireActivity().snackBar(
                                R.color.holo_blue_dark,
                                getString(R.string.note_not_found_error),
                                dialog?.window?.decorView
                            )
                        }
                        MyResponse.DataState.SUCCESS -> {
                            it.data?.get(0)?.let { noteAndFolder ->
                                setNoteDataToView(noteAndFolder)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun setNoteDataToView(noteAndFolder: NoteAndFolder) {
        binding?.apply {
            noteAndFolder.note.also {
                //set title
                titleNoteShowTxt.text = it.title
                //set des of note
                desNoteShowTxt.text = it.des
                //set date of note
                dateNoteShowTxt.text = it.date
                //set time of note
                timeNoteShowTxt.text = it.time
                //set pin state
                pinNoteShow.isShown(it.isPinned)
            }
            //set priority
            noteAndFolder.note.priority.also {
                //find priority of note
                val priorityColor = when (it) {
                    PriorityNote.HIGH.name -> {
                        R.color.priority_high
                    }
                    PriorityNote.NORMAL.name -> {
                        R.color.priority_normal
                    }
                    PriorityNote.LOW.name -> {
                        R.color.priority_low
                    }
                    else -> {
                        R.color.priority_low
                    }
                }
                //set priority color to star
                priorityShowNoteImg.setColorFilter(
                    ContextCompat.getColor(requireContext(), priorityColor),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
            //set folder data of note
            noteAndFolder.folder.also {
                //set folder title
                folderNoteShowTxt.text = it.title
                //set folder icon
                folderNoteShowTxt.setCompoundDrawablesWithIntrinsicBounds(
                    it.img,
                    0,
                    0,
                    0
                )
            }
            //set menu
            menuOptionShowNoteImg.setOnClickListener {
                menuPopup(noteAndFolder)
            }
            //set note as a notification
            submitNotificationNote.setOnClickListener {
                noteAndFolder.also {
                    requireActivity().notificationCreation(
                        //title
                        it.note.title,
                        //des
                        it.note.des,
                        //icon
                        it.folder.img,
                        //id
                        it.note.id
                    )
                }
            }
        }
    }

    //init menu
    private fun menuPopup(noteAndFolder: NoteAndFolder) {
        binding?.apply {
            menuOptionShowNoteImg.setOnClickListener {
                val popupMenu = PopupMenu(context, it as ImageView)
                popupMenu.menuInflater.inflate(R.menu.note_show_menu_option, popupMenu.menu)
                popupMenu.show()
                //Select
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        //share note
                        R.id.share_note_show_content -> {
                            //share note
                            Intent(Intent.ACTION_SEND).also { intent ->

                                //share note content
                                intent.type = "text/plain"
                                intent.putExtra(
                                    Intent.EXTRA_SUBJECT,
                                    noteAndFolder.note.title
                                )
                                intent.putExtra(Intent.EXTRA_TEXT, noteAndFolder.toString())
                                startActivity(
                                    Intent.createChooser(
                                        intent,
                                        getString(R.string.share_note)
                                    )
                                )
                            }
                        }
                        //copy note
                        R.id.copy_note_show_content -> {
                            lifecycleScope.launch(Dispatchers.IO) {
                                //copy note to clip board
                                val clipData = ClipData.newPlainText(
                                    noteAndFolder.note.title,
                                    noteAndFolder.toString()
                                )
                                clipboardManager.setPrimaryClip(clipData)
                                withContext(Dispatchers.Main) {
                                    //message : note successfully copied to clipboard
                                    requireActivity().snackBar(
                                        R.color.holo_green_dark,
                                        requireActivity().getString(R.string.copy_note_to_clipboard_successfully),
                                        dialog?.window?.decorView
                                    )
                                }
                            }
                        }
                        //delete note
                        R.id.delete_note_show_content -> {
                            //delete note show alert dialog
                            val note = noteAndFolder.note
                            requireActivity().alertDialog(
                                getString(R.string.delete_note_title),
                                "آیا از حذف یادداشت (${note.title}) مطمئنید؟"
                            ) {
                                requireActivity().snackBar(
                                    R.color.holo_green_dark,
                                    getString(R.string.delete_note_successfully)
                                )
                                viewModel.deleteNote(note)
                                dismiss()
                            }
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}