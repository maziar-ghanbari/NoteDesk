package ghanbari.maziar.notedesk.ui.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ghanbari.maziar.notedesk.databinding.FragmentMainBinding
import ghanbari.maziar.notedesk.ui.main.adapters.FolderAdapter
import ghanbari.maziar.notedesk.ui.main.adapters.NoteAdapter
import ghanbari.maziar.notedesk.ui.main.pages.addEditFolder.AddEditFolderFragment
import ghanbari.maziar.notedesk.ui.main.pages.addEditNote.AddEditNoteFragment
import ghanbari.maziar.notedesk.utils.*
import ghanbari.maziar.notedesk.viewModel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding
    private val viewModel: MainViewModel by viewModels()
    private var numberOfFolders = 0

    @Inject
    lateinit var folderAdapter: FolderAdapter

    @Inject
    lateinit var noteAdapter: NoteAdapter

    @Inject
    lateinit var dataStore: StoreUserData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            //on click listeners
            onClickAdapters()
            //set first launch and add default folder
            firstLaunch()
            //start connection between viewModel(database lovedata) and room database
            viewModel.connectToModel()
            //call to get all folders and notes
            viewModel.getAllFolders()

            //search box
            searchBoxEdtTxt.addTextChangedListener {
                lifecycleScope.launch {
                    viewModel._operatorNotesStateFlow.emit(NoteOperator.getByTitleSearch(it.toString()))
                }
            }
            //get all folders
            viewModel.receiveFoldersLiveData.observe(viewLifecycleOwner) {
                //just error and success are important
                when (it.state) {
                    MyResponse.DataState.SUCCESS -> {
                        //set data to adapter
                        it.data?.let { data ->
                            folderAdapter.setData(data)
                            numberOfFolders = data.size
                        }
                        //init recycler
                        slideMenuRecycler.init(
                            folderAdapter, LinearLayoutManager(
                                requireContext(), LinearLayoutManager.VERTICAL, true
                            )
                        )
                    }
                    MyResponse.DataState.ERROR ->{
                        //TODO upgrade snackbar
                        Snackbar.make(root, it.errorMessage.toString(), Snackbar.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }

            //get all note
            viewModel.receiveNotesLiveData.observe(viewLifecycleOwner) {
                when (it.state) {
                    MyResponse.DataState.LOADING -> {
                        //show loading
                        notesListProgress.showBut(notesListRecycler, notesListEmpty)
                    }
                    MyResponse.DataState.SUCCESS -> {
                        //show notes
                        notesListRecycler.showBut(notesListProgress, notesListEmpty)
                        it.data?.let { data ->
                            noteAdapter.setData(data)
                            notesListRecycler.init(
                                noteAdapter,
                                LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                            )
                        }
                    }
                    MyResponse.DataState.EMPTY -> {
                        notesListEmpty.showBut(notesListProgress, notesListRecycler)
                    }
                    MyResponse.DataState.ERROR -> {
                        //TODO upgrade snackbar
                        notesListRecycler.showBut(notesListProgress, notesListEmpty)
                        Snackbar.make(root, it.errorMessage.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            //spinner priority
            prioritiesMenuSpinner.setUpPriorityIconWithAdapter {
                lifecycleScope.launch(Dispatchers.IO) {
                    if (it == null) {
                        //no priority selected
                        viewModel._operatorNotesStateFlow.emit(NoteOperator.getAllNotes())
                    } else {
                        //send selected priority as by operator in viewModel to receive note in related
                        viewModel._operatorNotesStateFlow.emit(NoteOperator.getByPrioritySearch(it.name))
                    }
                }
            }
            //*** menus
            //add new note
            addMenuTxt.setOnClickListener {
                //open fragment addEditNote
                activity?.let { act ->
                    AddEditNoteFragment().show(
                        act.supportFragmentManager,
                        AddEditNoteFragment().tag
                    )
                }
            }
        }
    }

    //onClick in adapters items
    private fun onClickAdapters(){
        //note item click
        noteAdapter.setOnItemClickListener {
            viewModel.deleteNote(it)
        }
        //edit click
        noteAdapter.setOnEditClickListener {
            activity?.let { act ->
                val updatePage = AddEditNoteFragment()
                val bundle = Bundle()
                bundle.putInt(ARG_ID_NOTE_UPDATE, it.id)
                updatePage.arguments = bundle
                updatePage.show(act.supportFragmentManager, updatePage.tag)
            }
        }
        //folder adapter item click
        folderAdapter.setOnItemClickListener {
            when(it.title){
                ADD_FOLDER -> {
                    //open fragment add folder
                    Toast.makeText(requireContext()
                        ,"$numberOfFolders < $MAX_OF_NUMBER_OF_FOLDERS",Toast.LENGTH_SHORT).show()
                    if(numberOfFolders < MAX_OF_NUMBER_OF_FOLDERS) {
                        activity?.let { act ->
                            val folderEditAdd = AddEditFolderFragment()
                            folderEditAdd.show(act.supportFragmentManager, folderEditAdd.tag)
                        }
                    }
                }
            }
        }
        //folder adapter option item click
        folderAdapter.setOnOptionsClickListener { controll , folder ->
            when (controll){
                EDIT_FOLDER ->{
                    //TODO edit folder
                }
                DELETE_FOLDER ->{
                    AlertDialog.Builder(requireContext())
                        .setTitle("حذف پوشه")
                        .setMessage("آیا از حذف پوشه ${folder.title} مطمئنید؟")
                        .setPositiveButton("بله"){_,_->
                            viewModel.deleteFolder(folder)
                        }.setNegativeButton("خیر"){_,_->
                        }.create().show()
                }
            }
        }
    }
    //run in the first launch app
    private fun firstLaunch() {
        lifecycleScope.launchWhenCreated {
            dataStore.getStateFirstLaunch().collect {
                if (it) {
                    insertDefaultFolders()
                    dataStore.setStateFirstLaunch(false)
                }
            }
        }
    }

    //insert default folder
    private fun insertDefaultFolders() {
        viewModel.getDefaultFolders().forEach {
            viewModel.insertFolder(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}