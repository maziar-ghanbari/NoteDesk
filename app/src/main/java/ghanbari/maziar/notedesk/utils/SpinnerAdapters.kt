package ghanbari.maziar.notedesk.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import ghanbari.maziar.notedesk.data.model.FolderEntity
import ghanbari.maziar.notedesk.databinding.*

object SpinnerAdapters {
    //selection priority for filtering on notes
    class PriorityFilterNoteAdapter(private val context: Context, private val tintIcon : MutableList<Pair<Int, PriorityNote?>>) : BaseAdapter() {

        private lateinit var bindingSelectedItem: SpinnerItemPrioritySelectedBinding
        private lateinit var bindingItem: SpinnerItemPriorityBinding
        override fun getCount(): Int = tintIcon.size


        override fun getItem(p0: Int) = p0

        override fun getItemId(p0: Int): Long = p0.toLong()

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
            bindingItem =
                SpinnerItemPriorityBinding.inflate(LayoutInflater.from(context), parent, false)
            bindingItem.root.setColorFilter(
                ContextCompat.getColor(context, tintIcon[position].first),
                android.graphics.PorterDuff.Mode.SRC_IN
            )

            return bindingItem.root
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            bindingSelectedItem = SpinnerItemPrioritySelectedBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            bindingSelectedItem.prioritySpinnerImg.setColorFilter(
                ContextCompat.getColor(context, tintIcon[position].first),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            return bindingSelectedItem.root
        }
    }

    //icon when creating new folder for selection
    class IconAdapter(private val icons: MutableList<Int>, private val context: Context) :
        BaseAdapter() {

        private lateinit var binding: SpinnerItemIconBinding

        override fun getCount(): Int = icons.size

        override fun getItem(p0: Int) = p0
        override fun getItemId(p0: Int): Long = p0.toLong()

        @SuppressLint("ViewHolder")
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            binding = SpinnerItemIconBinding.inflate(LayoutInflater.from(context), p2, false)
            binding.root.setBackgroundResource(icons[p0])
            return binding.root
        }
    }

    //spinner adapter for bottom sheet folder selection
    class FolderSelectionAdapter(
        private val context: Context,
        private val folders: MutableList<FolderEntity>
    ) : BaseAdapter() {

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

}
