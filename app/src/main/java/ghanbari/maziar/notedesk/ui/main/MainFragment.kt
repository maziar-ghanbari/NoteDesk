package ghanbari.maziar.notedesk.ui.main


import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ghanbari.maziar.notedesk.R
import ghanbari.maziar.notedesk.databinding.FragmentMainBinding
import ghanbari.maziar.notedesk.ui.MainActivity
import ghanbari.maziar.notedesk.ui.main.adapters.FolderAdapter
import ghanbari.maziar.notedesk.ui.main.adapters.NoteAdapter
import ghanbari.maziar.notedesk.ui.main.pages.addEditFolder.AddEditFolderFragment
import ghanbari.maziar.notedesk.ui.main.pages.addEditNote.AddEditNoteFragment
import ghanbari.maziar.notedesk.ui.main.pages.noteContentShow.NoteContentFragment
import ghanbari.maziar.notedesk.utils.*
import ghanbari.maziar.notedesk.viewModel.MainViewModel
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog
import ir.hamsaa.persiandatepicker.api.PersianPickerDate
import ir.hamsaa.persiandatepicker.api.PersianPickerListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding
    private val viewModel: MainViewModel by viewModels()
    private var numberOfFolders = 0

    //open close searchbox and slide menu(folders) animation
    private var openCloseAnimState = SearchFolderAnimState.SLIDE_MENU_OPEN

    @Inject
    lateinit var folderAdapter: FolderAdapter

    @Inject
    lateinit var noteAdapter: NoteAdapter

    @Inject
    lateinit var filesExporter: FilesExporter

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
            //toolbar as action bar
            //(activity as AppCompatActivity).setSupportActionBar(binding?.toolbar)
            //on option click
            toolbarOnOptionsClickListener()
            //open close searchBox ConstrainSet
            searchMenuTxt.setOnClickListener {
                when (openCloseAnimState) {
                    SearchFolderAnimState.SLIDE_MENU_OPEN -> {
                        root.setTransition(R.id.state1, R.id.state2)
                        openCloseAnimState = SearchFolderAnimState.ALL_OPEN
                        searchBoxEdtTxt.showKeyboard()
                    }
                    SearchFolderAnimState.ALL_CLOSE -> {
                        root.setTransition(R.id.state4, R.id.state3)
                        openCloseAnimState = SearchFolderAnimState.SEARCH_BOX_OPEN
                        searchBoxEdtTxt.showKeyboard()
                    }
                    SearchFolderAnimState.SEARCH_BOX_OPEN -> {
                        root.setTransition(R.id.state3, R.id.state4)
                        openCloseAnimState = SearchFolderAnimState.ALL_CLOSE
                        searchBoxEdtTxt.hideKeyboard()
                    }
                    SearchFolderAnimState.ALL_OPEN -> {
                        root.setTransition(R.id.state2, R.id.state1)
                        openCloseAnimState = SearchFolderAnimState.SLIDE_MENU_OPEN
                        searchBoxEdtTxt.hideKeyboard()
                    }
                }
                root.transitionToEnd()
            }
            //open close slideMenu ConstrainSet
            slideMenuVisibleTxt.setOnClickListener {
                when (openCloseAnimState) {
                    SearchFolderAnimState.SLIDE_MENU_OPEN -> {
                        root.setTransition(R.id.state1, R.id.state4)
                        openCloseAnimState = SearchFolderAnimState.ALL_CLOSE
                    }
                    SearchFolderAnimState.ALL_CLOSE -> {
                        root.setTransition(R.id.state4, R.id.state1)
                        openCloseAnimState = SearchFolderAnimState.SLIDE_MENU_OPEN
                    }
                    SearchFolderAnimState.SEARCH_BOX_OPEN -> {
                        root.setTransition(R.id.state3, R.id.state2)
                        openCloseAnimState = SearchFolderAnimState.ALL_OPEN
                    }
                    SearchFolderAnimState.ALL_OPEN -> {
                        root.setTransition(R.id.state2, R.id.state3)
                        openCloseAnimState = SearchFolderAnimState.SEARCH_BOX_OPEN
                    }
                }
                root.transitionToEnd()
            }
            //on click listeners
            onClickAdapters()
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
                        //operation folder att top
                        val allFolders = viewModel.operationFolderAtTop()
                        //all folder
                        it.data?.let { data ->
                            allFolders.addAll(data)
                            folderAdapter.setData(allFolders)
                            numberOfFolders = data.size
                        }
                        //init recycler
                        slideMenuRecycler.init(
                            folderAdapter, LinearLayoutManager(
                                requireContext(), LinearLayoutManager.VERTICAL, true
                            )
                        )
                    }
                    MyResponse.DataState.ERROR -> {
                        requireActivity().snackBar(
                            R.color.holo_red_dark,
                            it.errorMessage.toString()
                        )
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
                        notesListRecycler.showBut(notesListProgress, notesListEmpty)
                        requireActivity().snackBar(
                            R.color.holo_red_dark,
                            it.errorMessage.toString()
                        )
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
                        withContext(Dispatchers.Main) {
                            requireActivity().snackBar(
                                R.color.holo_green_dark,
                                "نمایش بر اساس ${it.name}"
                            )
                        }
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

        //check permission observable
        (requireActivity() as MainActivity).isStoragePermissionGrantedLiveData.observe(viewLifecycleOwner) { isGranted ->
            if (isGranted) {
                //export files

                //get last version of notes from database dataStream
                val noteValue = viewModel.databaseNotesStateFlow.value
                //if their are exist export them to external storage as .txt file
                if (noteValue.state == MyResponse.DataState.SUCCESS){
                    val note = noteValue.data
                    note?.let { noteAndFolder ->
                        lifecycleScope.launch(Dispatchers.Main) {
                            val message = filesExporter.exportAllNotes(noteAndFolder)
                            requireActivity().snackBar(R.color.holo_blue_dark,message)
                        }
                    }
                }else if(noteValue.state == MyResponse.DataState.EMPTY){
                    requireActivity().snackBar(R.color.holo_red_dark,getString(R.string.no_note_available))
                }
            } else {
                //permission is denied
                requireActivity().snackBar(R.color.holo_blue_dark,getString(R.string.permission_denied)){
                    //open Setting to get permission
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also { intent ->
                        val uri =
                            Uri.fromParts("package", requireActivity().packageName, null)
                        intent.data = uri
                        requireActivity().startActivity(intent)
                    }
                }
            }
        }
    }


    //click on options of toolbars
    private fun toolbarOnOptionsClickListener() {
        binding?.apply {
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.export_all_to_phone -> {
                        //export all notes
                        (requireActivity() as MainActivity).exportAllNotes()
                    }
                    R.id.search_by_date_menu -> {
                        //search note by date
                        datePicker()
                    }
                    R.id.about_us_menu -> {
                        requireActivity().snackBar(R.color.holo_green_dark,"سازنده : مازیار قنبری")
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    //date picker
    private fun datePicker() {
        //use coroutine for avoid blocking ui main
        lifecycleScope.launch(Dispatchers.IO) {
            val picker = PersianDatePickerDialog(requireContext())
                .setPositiveButtonString("گُزیدن")
                .setNegativeButton("لغو")
                .setTodayButton("برو به امروز")
                .setTodayButtonVisible(true)
                .setMinYear(1300)
                .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                .setMaxMonth(PersianDatePickerDialog.THIS_MONTH)
                .setMaxDay(PersianDatePickerDialog.THIS_DAY)
                .setActionTextColor(Color.DKGRAY)
                .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                .setShowInBottomSheet(true)
                .setListener(object : PersianPickerListener {
                    override fun onDateSelected(persianPickerDate: PersianPickerDate) {
                        with(persianPickerDate) {
                            //standardizing date
                            val year = persianYear.toString()
                            val month =
                                if (persianMonth.toString().length == 1) "0$persianMonth" else persianMonth.toString()
                            val day =
                                if (persianDay.toString().length == 1) "0$persianDay" else persianDay.toString()
                            //convert to iranian number
                            val date = "$year/$month/$day".englishToIranianNumber()
                            //show message
                            requireActivity().snackBar(
                                R.color.holo_green_dark,
                                "نمایش یادداشت ها بر اساس تاریخ $date"
                            )
                            //send operator filter by date selected
                            lifecycleScope.launch(Dispatchers.IO) {
                                viewModel._operatorNotesStateFlow.emit(
                                    NoteOperator.getByDateSearch(
                                        date
                                    )
                                )
                            }
                        }
                    }

                    override fun onDismissed() {}
                })
            withContext(Dispatchers.Main) { picker.show() }
        }
    }

    //onClick in adapters items
    private fun onClickAdapters() {
        //note item click
        noteAdapter.setOnItemClickListener {
            //open note content fragment
            val noteContentFragment = NoteContentFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_ID_NOTE_SHOW, it.note.id)
            noteContentFragment.arguments = bundle
            noteContentFragment.show(
                requireActivity().supportFragmentManager,
                noteContentFragment.tag
            )
        }
        //edit notes item click
        noteAdapter.setOnEditClickListener {
            activity?.let { act ->
                val updatePage = AddEditNoteFragment()
                val bundle = Bundle()
                bundle.putInt(ARG_ID_NOTE_UPDATE, it.note.id)
                updatePage.arguments = bundle
                updatePage.show(act.supportFragmentManager, updatePage.tag)
            }
        }
        //folder adapter item click
        folderAdapter.setOnItemClickListener {
            when (it.title) {
                ADD_FOLDER -> {
                    //open fragment add folder
                    if (numberOfFolders < MAX_OF_NUMBER_OF_FOLDERS) {
                        activity?.let { act ->
                            val folderEditAdd = AddEditFolderFragment()
                            folderEditAdd.show(act.supportFragmentManager, folderEditAdd.tag)
                        }
                    } else {
                        //error max of number of folders
                        requireActivity().snackBar(
                            R.color.holo_red_dark,
                            getString(R.string.max_of_number_of_folder_error)
                        )
                    }
                }
                ALL_FOLDER -> {
                    //show all notes
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel._operatorNotesStateFlow.emit(NoteOperator.getAllNotes())
                    }
                    requireActivity().snackBar(
                        R.color.holo_green_dark,
                        getString(R.string.note_filter_by_all_folder)
                    )
                }
                else -> {
                    //show notes by each folder filtering
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel._operatorNotesStateFlow.emit(NoteOperator.getByFolderSearch(it.title))
                    }
                    requireActivity().snackBar(
                        R.color.holo_green_dark,
                        "نمایش بر اساس ${it.title}"
                    )
                }
            }
        }
        //folder adapter option item click
        folderAdapter.setOnOptionsClickListener { controll, folder ->
            //menu option folders
            when (controll) {
                EDIT_FOLDER -> {
                    //edit folder icon & title
                    activity?.let { act ->
                        val folderEditAdd = AddEditFolderFragment()
                        val bundle = Bundle()

                        bundle.putInt(ARG_ID_FOLDER_UPDATE, folder.id)
                        bundle.putString(ARG_TITLE_FOLDER_UPDATE, folder.title)
                        bundle.putInt(ARG_ICON_FOLDER_UPDATE, folder.img)
                        folderEditAdd.arguments = bundle
                        folderEditAdd.show(act.supportFragmentManager, folderEditAdd.tag)
                    }
                }
                DELETE_FOLDER -> {
                    //delete folder dialog show
                    requireActivity().alertDialog(
                        getString(R.string.delete_folder_title),
                        "آیا از حذف پوشه (${folder.title}) مطمئنید؟"
                    ) {
                        requireActivity().snackBar(
                            R.color.holo_green_dark,
                            getString(R.string.delete_folder_successfully)
                        )
                        viewModel.deleteFolder(folder)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}