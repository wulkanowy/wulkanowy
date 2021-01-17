package io.github.wulkanowy.ui.modules.studentinfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.databinding.ItemStudentInfoBinding
import javax.inject.Inject

class StudentInfoAdapter @Inject constructor() :
    RecyclerView.Adapter<StudentInfoAdapter.ViewHolder>() {

    var items = listOf<Pair<String, String>>()

    var onItemClickListener: (position: Int) -> Unit = {}

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemStudentInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            studentInfoItemTitle.setText(item.first)
            studentInfoItemSubtitle.text = item.second

            root.setOnClickListener { onItemClickListener(position) }
        }
    }

    class ViewHolder(val binding: ItemStudentInfoBinding) : RecyclerView.ViewHolder(binding.root)
}