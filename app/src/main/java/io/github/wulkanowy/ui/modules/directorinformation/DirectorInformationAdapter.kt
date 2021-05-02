package io.github.wulkanowy.ui.modules.directorinformation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.data.db.entities.DirectorInformation
import io.github.wulkanowy.databinding.ItemDirectorInformationBinding
import io.github.wulkanowy.utils.toFormattedString
import javax.inject.Inject

class DirectorInformationAdapter @Inject constructor() :
    RecyclerView.Adapter<DirectorInformationAdapter.ViewHolder>() {

    var items = emptyList<DirectorInformation>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemDirectorInformationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            directorInformationItemDate.text = item.date.toFormattedString()
            directorInformationItemType.text = item.subject
            directorInformationItemContent.text = HtmlCompat.fromHtml(
                item.content, HtmlCompat.FROM_HTML_MODE_COMPACT
            )
        }
    }

    class ViewHolder(val binding: ItemDirectorInformationBinding) :
        RecyclerView.ViewHolder(binding.root)
}
