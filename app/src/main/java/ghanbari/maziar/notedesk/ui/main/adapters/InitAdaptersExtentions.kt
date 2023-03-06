package ghanbari.maziar.notedesk.ui.main.adapters

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import ghanbari.maziar.notedesk.R
import ghanbari.maziar.notedesk.data.model.FolderEntity
import ghanbari.maziar.notedesk.utils.PriorityNote

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