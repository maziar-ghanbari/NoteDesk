package ghanbari.maziar.notedesk.ui.main.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.qualifiers.ApplicationContext
import ghanbari.maziar.notedesk.R
import ghanbari.maziar.notedesk.data.model.FolderEntity
import ghanbari.maziar.notedesk.databinding.ItemFolderRecyclerBinding
import ghanbari.maziar.notedesk.utils.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FolderAdapter @Inject constructor(@ApplicationContext private val context: Context) :
    RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    private lateinit var binding: ItemFolderRecyclerBinding
    private var foldersList = emptyList<FolderEntity>()
    private var itemSelected: ConstraintLayout? = null

    //default folder operation : add , withoutFolder , all
    private val dFolderOperation = mutableListOf(0, 1, 2)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            ItemFolderRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //no option for operation item
        holder.setIsRecyclable(false)
        holder.bindData(foldersList[position],position)
    }

    override fun getItemCount(): Int = foldersList.size

    //onItemClick & onOptionsClock
    private var onItemClick: ((FolderEntity) -> Unit)? = null
    private var onOptionsClick: ((String,FolderEntity) -> Unit)? = null
    fun setOnItemClickListener(onItemClickListener: (FolderEntity) -> Unit) {
        this.onItemClick = onItemClickListener
    }

    fun setOnOptionsClickListener(onOptionClickListener: (String,FolderEntity) -> Unit) {
        this.onOptionsClick = onOptionClickListener
    }

    inner class ViewHolder() : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bindData(folder: FolderEntity,position: Int) {
            binding.apply {
                //no option for operation item
                if (position in dFolderOperation) {
                    folderOption.isShown(false)
                }else{
                    folderOption.isShown(true)
                }
                //set img and title to view
                iconFolderItemImg.setImageResource(folder.img)
                titleFolderItemTxt.text = folder.title
                //show menu delete update
                folderOption.setOnClickListener {
                    val popupMenu = PopupMenu(context, it as ImageView)
                    popupMenu.menuInflater.inflate(R.menu.folder_menu_option, popupMenu.menu)
                    popupMenu.show()
                    //Select
                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.edit_folder -> {
                                onOptionsClick?.let {
                                    it(EDIT_FOLDER,folder)
                                }
                            }
                            R.id.delete_folder -> {
                                onOptionsClick?.let {
                                    it(DELETE_FOLDER,folder)
                                }
                            }
                        }
                        return@setOnMenuItemClickListener true
                    }
                }
                //select folder and filtering according by click
                folderItem.setOnClickListener {
                    itemSelected?.setBackgroundResource(R.drawable.item_folder_bg)
                    itemSelected = folderItem
                    itemSelected?.setBackgroundResource(R.drawable.item_folder_bg_selected)
                    onItemClick?.let { it(folder) }
                }
            }
        }
    }

    fun setData(data: MutableList<FolderEntity>) {
        val callback = DiffUtilsCallBack(data, foldersList.toMutableList())
        val differ = DiffUtil.calculateDiff(callback)
        foldersList = data
        differ.dispatchUpdatesTo(this)
    }


    class DiffUtilsCallBack(
        private val oldItem: MutableList<FolderEntity>,
        private val newItem: MutableList<FolderEntity>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldItem.size

        override fun getNewListSize(): Int = newItem.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItem[oldItemPosition] === newItem[newItemPosition]

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItem[oldItemPosition] === newItem[newItemPosition]
    }
}