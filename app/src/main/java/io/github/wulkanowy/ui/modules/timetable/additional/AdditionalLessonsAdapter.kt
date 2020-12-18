package io.github.wulkanowy.ui.modules.timetable.additional

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.data.db.entities.TimetableAdditional
import io.github.wulkanowy.databinding.ItemTimetableAdditionalBinding
import io.github.wulkanowy.utils.toFormattedString
import javax.inject.Inject

class AdditionalLessonsAdapter @Inject constructor() :
    RecyclerView.Adapter<AdditionalLessonsAdapter.ItemViewHolder>() {

    var items = emptyList<TimetableAdditional>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemTimetableAdditionalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            additionalLessonItemTimeStart.text = item.start.toFormattedString("HH:mm")
            additionalLessonItemTimeFinish.text = item.end.toFormattedString("HH:mm")
            additionalLessonItemSubject.text = item.subject
        }
    }

    class ItemViewHolder(val binding: ItemTimetableAdditionalBinding) :
        RecyclerView.ViewHolder(binding.root)
}
