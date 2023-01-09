package ghanbari.maziar.notedesk.ui.main.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.qualifiers.ApplicationContext
import ghanbari.maziar.notedesk.R
import ghanbari.maziar.notedesk.data.model.NoteEntity
import ghanbari.maziar.notedesk.databinding.ItemNoteRecyclerBinding
import ghanbari.maziar.notedesk.utils.PriorityNote
import ghanbari.maziar.notedesk.utils.isShown
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteAdapter @Inject constructor(@ApplicationContext private val context: Context) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    private lateinit var binding: ItemNoteRecyclerBinding
    private var noteList = emptyList<NoteEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            ItemNoteRecyclerBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(noteList[position])
    }


    override fun getItemCount(): Int = noteList.size
    override fun getItemViewType(position: Int): Int = position

    inner class ViewHolder() : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindData(note: NoteEntity) {
            binding.apply {
                //title
                itemNoteTitleTxt.text = note.title
                //des
                itemNoteDesTxt.text = note.des
                //on edit click listener
                itemNoteEditImg.setOnClickListener {
                    onEditClickListener?.let { edt -> edt(note) }
                }
                //on item click listener
                itemNoteCardBody.setOnClickListener {
                    onItemClickListener?.let { item -> item(note) }
                }
                itemNotePriorityColor.setOnClickListener {
                    onItemClickListener?.let { item -> item(note) }
                }
                ////itemNoteFolderTxt.compoundPaddingTop,itemNoteFolderTxt.compoundPaddingRight,itemNoteFolderTxt.compoundPaddingBottom
                //folder icon
                itemNoteFolderTxt.setCompoundDrawablesWithIntrinsicBounds(
                    note.folderImg,
                    itemNoteFolderTxt.compoundPaddingTop,
                    itemNoteFolderTxt.compoundPaddingRight,
                    itemNoteFolderTxt.compoundPaddingBottom
                )
                //folder name
                itemNoteFolderTxt.text = note.folderTitle
                //date and time
                itemNoteDateTimeTxt.text = "${note.date} ~ ${note.time}"
                //date and time icon
                itemNoteDateTimeTxt.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_twotone_calendar_month_24,
                    itemNoteDateTimeTxt.compoundPaddingTop,
                    itemNoteDateTimeTxt.compoundPaddingRight,
                    itemNoteDateTimeTxt.compoundPaddingBottom
                )
                //find priority of note
                val priorityColor = when (note.priority) {
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
                itemNotePriorityColor.setColorFilter(
                    ContextCompat.getColor(context, priorityColor),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                itemNotePinImg.isShown(note.isPinned)
            }
        }
    }

    //on item click listener
    private var onItemClickListener: ((note: NoteEntity) -> Unit)? = null
    fun setOnItemClickListener(onItemClickListener: (note: NoteEntity) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    //on edit click listener
    private var onEditClickListener: ((note: NoteEntity) -> Unit)? = null
    fun setOnEditClickListener(onEditClickListener: (note: NoteEntity) -> Unit) {
        this.onEditClickListener = onEditClickListener
    }

    fun setData(data: MutableList<NoteEntity>) {
        val callback = DiffUtilsCallBack(data, noteList.toMutableList())
        val differ = DiffUtil.calculateDiff(callback)
        noteList = data
        differ.dispatchUpdatesTo(this)
    }

    class DiffUtilsCallBack(
        private val oldItem: MutableList<NoteEntity>,
        private val newItem: MutableList<NoteEntity>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldItem.size

        override fun getNewListSize(): Int = newItem.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItem[oldItemPosition] === newItem[newItemPosition]

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItem[oldItemPosition] === newItem[newItemPosition]
    }

}