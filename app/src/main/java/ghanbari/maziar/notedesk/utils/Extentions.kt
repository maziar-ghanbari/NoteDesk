package ghanbari.maziar.notedesk.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import ghanbari.maziar.notedesk.R
import ghanbari.maziar.notedesk.data.model.FolderEntity
import ghanbari.maziar.notedesk.data.model.NoteAndFolder
import javax.inject.Inject

fun RecyclerView.init(adapter: RecyclerView.Adapter<*>, layoutManager: LayoutManager) {
    this.adapter = adapter
    this.layoutManager = layoutManager
}

fun View.isShown(visible: Boolean, isInVisible: Boolean = false) {
    if (visible) {
        this.visibility = View.VISIBLE
    } else {
        if (isInVisible) {
            this.visibility = View.INVISIBLE
        } else {
            this.visibility = View.GONE
        }
    }
}

//open keyboard
fun View.showKeyboard() {
    this.requestFocus()
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

//close keyboard
fun View.hideKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

//show view but gone another
fun View.showBut(paitiAr1: View? = null, paitiAr2: View? = null) {
    this.visibility = View.VISIBLE
    paitiAr1?.visibility = View.GONE
    paitiAr2?.visibility = View.GONE
}

//****notes entity list operators****

//get notes by title search
fun MutableList<NoteAndFolder>.byTitleSearch(title: String): MutableList<NoteAndFolder> {
    val result = mutableListOf<NoteAndFolder>()
    this.forEach {
        if (it.note.title.contains(title, true)) {
            result.add(it)
        }
    }
    return result
}

//get notes by folder search
fun MutableList<NoteAndFolder>.byFolderSearch(folder: String): MutableList<NoteAndFolder> {
    val result = mutableListOf<NoteAndFolder>()
    this.forEach {
        if (it.folder.title.contains(folder, true)) {
            result.add(it)
        }
    }
    return result
}

//get note by id search
fun MutableList<NoteAndFolder>.byIdSearch(id: Int): MutableList<NoteAndFolder> {
    val result = mutableListOf<NoteAndFolder>()
    this.forEach {
        if (it.note.id == id) {
            result.add(it)
            return result
        }
    }
    return result
}

//get notes by priority search
fun MutableList<NoteAndFolder>.byPrioritySearch(priority: String): MutableList<NoteAndFolder> {
    val result = mutableListOf<NoteAndFolder>()
    this.forEach {
        if (it.note.priority.contains(priority, true)) {
            result.add(it)
        }
    }
    return result
}

//get notes by pin search
fun MutableList<NoteAndFolder>.orderByPinSearch(): MutableList<NoteAndFolder> {
    val result = mutableListOf<NoteAndFolder>()
    result.addAll(this)
    result.sortBy {
        it.note.isPinned
    }
    Log.e(TAG, "orderByPinSearch: $result\n$this")
    return result
}

//get notes by date search
fun MutableList<NoteAndFolder>.byDateSearch(date: String): MutableList<NoteAndFolder> {
    val result = mutableListOf<NoteAndFolder>()
    this.forEach {
        if (it.note.date.contains(date, true)) {
            result.add(it)
        }
    }
    return result
}

//init spinner priority
fun Spinner.setUpPriorityIconWithAdapter(
    customItemArray: Array<Pair<Int, PriorityNote?>>? = null,
    callback: (PriorityNote?) -> Unit
) {
    val tintIcon = mutableListOf<Pair<Int, PriorityNote?>>()
    if (customItemArray == null) {
        val array = arrayOf(
            Pair(R.color.priority_non, null),
            Pair(R.color.priority_high, PriorityNote.HIGH),
            Pair(R.color.priority_normal, PriorityNote.NORMAL),
            Pair(R.color.priority_low, PriorityNote.LOW)
        )
        tintIcon.addAll(array)
    } else {
        tintIcon.addAll(customItemArray)
    }

    val adapterB = SpinnerAdapters.PriorityFilterNoteAdapter(context, tintIcon)

    val onSelect = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            callback(tintIcon[p2].second)
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {

        }
    }

    this.adapter = adapterB
    this.onItemSelectedListener = onSelect
}

//select folder in note creation
//init folder spinner for add note bottom sheet
fun Spinner.setUpFolderItemsWithAdapter(
    folders: MutableList<FolderEntity>,
    callback: (FolderEntity) -> Unit
) {
    val adapterB = SpinnerAdapters.FolderSelectionAdapter(context, folders)

    val onSelect = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            callback(folders[p2])
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {

        }
    }

    this.adapter = adapterB
    this.onItemSelectedListener = onSelect
}

//spinner icon suggestion folder
fun Spinner.setUpIconSpinner(icons: MutableList<Int>, callback: (Int) -> Unit) {
    val adapter = SpinnerAdapters.IconAdapter(icons, context)
    val onSelect = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            callback(icons[p2])
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {

        }
    }
    this.adapter = adapter
    this.onItemSelectedListener = onSelect
}

//snackBar
fun Activity.snackBar(colorBackground : Int,message : String){
    val mySnack = Snackbar.make(this.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)

    //set background
    mySnack.setBackgroundTint(ContextCompat.getColor(this, colorBackground))
    //setShape
    mySnack.view.background = ContextCompat.getDrawable(this, R.drawable.snack_bar_shape_bg)

    //margin
    val params = mySnack.view.layoutParams as (FrameLayout.LayoutParams)
    params.setMargins(16, 32, 16, 32)
    mySnack.view.layoutParams = params

    //wrap content size
    //gravity center
    params.width = FrameLayout.LayoutParams.WRAP_CONTENT
    params.gravity = Gravity.CENTER_HORIZONTAL
    mySnack.view.layoutParams = params

    //icon
    val textView = mySnack.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
    textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_info_24, 0, 0, 0)
    textView.compoundDrawablePadding = resources.getDimensionPixelOffset(`in`.nouri.dynamicsizeslib.R.dimen._3mdp)

    //set behavior
    mySnack.behavior = object : BaseTransientBottomBar.Behavior(){
        //TODO set snackbar behavior for dialogs interactive
    }

    mySnack.show()
}
//alert dialog
fun Activity.alertDialog(title :String,message :String ,yes : (() -> Unit)){
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("بله"){_,_->
            yes()
        }.setNegativeButton("خیر"){_,_->
        }.create().show()
}