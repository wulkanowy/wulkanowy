package io.github.wulkanowy.ui.modules.attendance.calculator

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.data.pojos.AttendanceData
import io.github.wulkanowy.databinding.ItemAttendanceCalculatorHeaderBinding
import javax.inject.Inject
import kotlin.math.roundToInt

class AttendanceCalculatorAdapter @Inject constructor() :
    RecyclerView.Adapter<AttendanceCalculatorAdapter.ViewHolder>() {

    var items = emptyList<AttendanceData>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) = ViewHolder(
        ItemAttendanceCalculatorHeaderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(parent: ViewHolder, position: Int) {
        with(parent.binding) {
            val item = items[position]
            attendanceCalculatorPercentage.text = "${item.presencePercentage.roundToInt()}"

            if (item.lessonBalance > 0) {
                attendanceCalculatorBalancePositive.isVisible = true
                attendanceCalculatorBalanceNeutral.isVisible = false
                attendanceCalculatorBalanceNegative.isVisible = false
                attendanceCalculatorBalancePositive.text = "+${item.lessonBalance}"
            } else if (item.lessonBalance < 0) {
                attendanceCalculatorBalancePositive.isVisible = false
                attendanceCalculatorBalanceNeutral.isVisible = false
                attendanceCalculatorBalanceNegative.isVisible = true
                attendanceCalculatorBalanceNegative.text = "${item.lessonBalance}"
            } else {
                attendanceCalculatorBalancePositive.isVisible = false
                attendanceCalculatorBalanceNeutral.isVisible = true
                attendanceCalculatorBalanceNegative.isVisible = false
            }
            attendanceCalculatorTitle.text = item.subjectName
            attendanceCalculatorTotal.text = "${item.total}"
            attendanceCalculatorPresence.text = "${item.presences}"
        }
    }

    class ViewHolder(val binding: ItemAttendanceCalculatorHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)
}
