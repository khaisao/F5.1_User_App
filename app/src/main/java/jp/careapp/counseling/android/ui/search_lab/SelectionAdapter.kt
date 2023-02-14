package jp.careapp.counseling.android.ui.search_lab

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.android.data.network.CategoryDiffCallBack
import jp.careapp.counseling.android.data.network.CategoryResponse
import jp.careapp.counseling.databinding.ItemSelectionCategoryBinding

class SelectionAdapter : ListAdapter<CategoryResponse,SelectionViewHolder >(CategoryDiffCallBack) {

    init {
        setHasStableIds(true)
    }
    var tracker: SelectionTracker<Long>? = null

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSelectionCategoryBinding.inflate(layoutInflater, parent, false)
        return SelectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectionViewHolder, position: Int) {
        getItem(position).let {
            holder.bind(
                it,
                tracker?.isSelected(
                    position.toLong()
                ) ?: false
            )
        }
    }
}

class SelectionViewHolder(
    private val binding: ItemSelectionCategoryBinding,
) : RecyclerView.ViewHolder(binding.root){

    fun bind(item: CategoryResponse, checked: Boolean = false) {
        binding.run {
            text.text = item.name
            check.isSelected = checked
        }
    }

    fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
        object : ItemDetailsLookup.ItemDetails<Long>() {
            override fun getPosition(): Int = adapterPosition
            override fun getSelectionKey(): Long = itemId
            override fun inSelectionHotspot(e: MotionEvent): Boolean  = true
        }
}

class CheckedItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {

    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as SelectionViewHolder).getItemDetails()
        }
        return null
    }
}

class CheckedKeyProvider(private val recyclerView: RecyclerView) :
    ItemKeyProvider<Long>(SCOPE_MAPPED) {
    override fun getKey(position: Int): Long? {
        return recyclerView.adapter?.getItemId(position)
    }

    override fun getPosition(key: Long): Int {
        return recyclerView.findViewHolderForItemId(key)?.layoutPosition ?: RecyclerView.NO_POSITION
    }
}