package io.github.wulkanowy.ui.modules.account.accountedit

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.databinding.ItemAccountEditColorBinding
import javax.inject.Inject

class AccountEditColorAdapter @Inject constructor() :
    RecyclerView.Adapter<AccountEditColorAdapter.ViewHolder>() {

    var items = listOf<Int>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemAccountEditColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            accountEditItemColor.setImageDrawable(GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(item)
            })
        }
    }

    class ViewHolder(val binding: ItemAccountEditColorBinding) :
        RecyclerView.ViewHolder(binding.root)
}
