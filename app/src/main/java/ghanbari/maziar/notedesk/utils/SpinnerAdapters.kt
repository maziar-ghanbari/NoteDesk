package ghanbari.maziar.notedesk.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import ghanbari.maziar.notedesk.databinding.SpinnerItemIconBinding

sealed class SpinnerAdapters{

    class IconAdapter(private val icons : MutableList<Int>, private val context :Context) : BaseAdapter(){

        private lateinit var binding: SpinnerItemIconBinding

        override fun getCount(): Int = icons.size

        override fun getItem(p0: Int) = p0
        override fun getItemId(p0: Int): Long = p0.toLong()

        @SuppressLint("ViewHolder")
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            binding = SpinnerItemIconBinding.inflate(LayoutInflater.from(context),p2,false)
            binding.root.setBackgroundResource(icons[p0])
            return binding.root
        }
    }

}
