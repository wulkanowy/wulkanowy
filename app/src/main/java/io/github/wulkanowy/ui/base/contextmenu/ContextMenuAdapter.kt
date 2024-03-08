package io.github.wulkanowy.ui.base.contextmenu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.databinding.ItemContextMenuBinding
import javax.inject.Inject

class ContextMenuAdapter @Inject constructor() :
    RecyclerView.Adapter<ContextMenuAdapter.ItemViewHolder>() {

    var items = mutableListOf<ContextMenuItem>()

    var onClickListener: (ContextMenuItem) -> Unit = {}

    class ItemViewHolder(val binding: ItemContextMenuBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(ItemContextMenuBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            itemImage.setImageDrawable(item.drawable)
            itemTitle.text = item.title
            root.setOnClickListener {
                onClickListener(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
