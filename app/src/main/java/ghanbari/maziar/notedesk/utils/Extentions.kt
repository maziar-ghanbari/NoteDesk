package ghanbari.maziar.notedesk.utils

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import ghanbari.maziar.notedesk.R
import ghanbari.maziar.notedesk.data.model.FolderEntity
import ghanbari.maziar.notedesk.data.model.NoteAndFolder
import ghanbari.maziar.notedesk.data.model.NoteEntity
import ghanbari.maziar.notedesk.databinding.SpinnerItemFolderBinding
import ghanbari.maziar.notedesk.databinding.SpinnerItemFolderSelectedBinding
import ghanbari.maziar.notedesk.databinding.SpinnerItemPriorityBinding
import ghanbari.maziar.notedesk.databinding.SpinnerItemPrioritySelectedBinding


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
        if (it.note!!.title.contains(title, true)) {
            result.add(it)
        }
    }
    return result
}

//get notes by folder search
fun MutableList<NoteAndFolder>.byFolderSearch(folder: String): MutableList<NoteAndFolder> {
    val result = mutableListOf<NoteAndFolder>()
    this.forEach {
        if (it.folder!!.title.contains(folder, true)) {
            result.add(it)
        }
    }
    return result
}

//get note by id search
fun MutableList<NoteAndFolder>.byIdSearch(id: Int): MutableList<NoteAndFolder> {
    val result = mutableListOf<NoteAndFolder>()
    this.forEach {
        if (it.note!!.id == id) {
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
        if (it.note!!.priority.contains(priority, true)) {
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
        it.note!!.isPinned
    }
    Log.e(TAG, "orderByPinSearch: $result\n$this")
    return result
}

//get notes by date search
fun MutableList<NoteAndFolder>.byDateSearch(date: String): MutableList<NoteAndFolder> {
    val result = mutableListOf<NoteAndFolder>()
    this.forEach {
        if (it.note!!.date.contains(date, true)) {
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

    val adapterB = object : BaseAdapter() {

        private lateinit var binding_selected_item: SpinnerItemPrioritySelectedBinding
        private lateinit var binding_item: SpinnerItemPriorityBinding
        override fun getCount(): Int = tintIcon.size


        override fun getItem(p0: Int) = p0

        override fun getItemId(p0: Int): Long = p0.toLong()

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
            binding_item =
                SpinnerItemPriorityBinding.inflate(LayoutInflater.from(context), parent, false)
            binding_item.root.setColorFilter(
                ContextCompat.getColor(context, tintIcon[position].first),
                android.graphics.PorterDuff.Mode.SRC_IN
            )

            return binding_item.root
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            binding_selected_item = SpinnerItemPrioritySelectedBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            binding_selected_item.prioritySpinnerImg.setColorFilter(
                ContextCompat.getColor(context, tintIcon[position].first),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            return binding_selected_item.root
        }
    }

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
    val adapterB = object : BaseAdapter() {

        private lateinit var binding_selected_item: SpinnerItemFolderSelectedBinding
        private lateinit var binding_item: SpinnerItemFolderBinding
        override fun getCount(): Int = folders.size

        override fun getItem(p0: Int) = p0

        override fun getItemId(p0: Int): Long = p0.toLong()

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
            binding_item =
                SpinnerItemFolderBinding.inflate(LayoutInflater.from(context), parent, false)
            binding_item.apply {
                root.text = folders[position].title
                root.setCompoundDrawablesWithIntrinsicBounds(
                    folders[position].img,
                    root.compoundPaddingTop,
                    root.compoundPaddingRight,
                    root.compoundPaddingBottom
                )
            }
            return binding_item.root
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            binding_selected_item = SpinnerItemFolderSelectedBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            binding_selected_item.apply {
                root.text = folders[position].title
                root.setCompoundDrawablesWithIntrinsicBounds(
                    folders[position].img,
                    root.compoundPaddingTop,
                    root.compoundPaddingRight,
                    root.compoundPaddingBottom
                )
            }
            return binding_selected_item.root
        }

    }

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
fun Spinner.setUpIconSpinner(icons: MutableList<Int>,callback: (Int) -> Unit){
    val adapter = SpinnerAdapters.IconAdapter(icons,context)
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

//flow twice data