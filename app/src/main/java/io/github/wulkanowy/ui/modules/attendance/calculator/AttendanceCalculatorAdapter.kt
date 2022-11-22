package io.github.wulkanowy.ui.modules.attendance.calculator

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.databinding.ItemAttendanceCalculatorHeaderBinding
import io.github.wulkanowy.data.pojos.AttendanceData
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class AttendanceCalculatorAdapter @Inject constructor() :
    RecyclerView.Adapter<AttendanceCalculatorAdapter.ViewHolder>() {

    var targetFreq: Double = 0.5
    var items = emptyList<AttendanceData>()

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
            val (name, presences, absences) = item
            attendanceCalculatorPercentage.text = "${item.presencePercentage.roundToInt()}"
            // The `+ 1` is to avoid false positives in close cases. Eg.:
            // target frequency 99%, 1 presence. Without the `+ 1` this would be reported shown as
            // a positive balance of +1, however that is not actually true as skipping one class
            // would make it so that the balance would actually be negative (-98). The `+ 1`
            // fixes this and makes sure that in situations like these, it's not reporting incorrect
            // balances
            if (presences / (item.total + 1f) >= targetFreq) {
                attendanceCalculatorBalancePositive.isVisible = true
                attendanceCalculatorBalanceNeutral.isVisible = false
                attendanceCalculatorBalanceNegative.isVisible = false
                attendanceCalculatorBalancePositive.text =
                    "+${calcMissingAbsences(targetFreq, absences, presences)}"
            } else if (presences / (item.total + 0f) < targetFreq) {
                attendanceCalculatorBalancePositive.isVisible = false
                attendanceCalculatorBalanceNeutral.isVisible = false
                attendanceCalculatorBalanceNegative.isVisible = true
                attendanceCalculatorBalanceNegative.text =
                    "-${calcMissingPresences(targetFreq, absences, presences)}"
            } else {
                attendanceCalculatorBalancePositive.isVisible = false
                attendanceCalculatorBalanceNeutral.isVisible = true
                attendanceCalculatorBalanceNegative.isVisible = false
            }
            attendanceCalculatorTitle.text = name
            attendanceCalculatorTotal.text = "${item.total}"
            attendanceCalculatorPresence.text = "$presences"
        }
    }

    override fun getItemCount() = items.size

    private fun calcMissingPresences(targetFreq: Double, absences: Int, presences: Int) =
        calcMinRequiredPresencesFor(targetFreq, absences) - presences

    private fun calcMinRequiredPresencesFor(targetFreq: Double, absences: Int) =
        ceil((targetFreq / (1 - targetFreq)) * absences).toInt()

    private fun calcMissingAbsences(targetFreq: Double, absences: Int, presences: Int) =
        calcMinRequiredAbsencesFor(targetFreq, presences) - absences

    private fun calcMinRequiredAbsencesFor(targetFreq: Double, presences: Int) =
        floor((presences * (1 - targetFreq)) / targetFreq).toInt()

    class ViewHolder(val binding: ItemAttendanceCalculatorHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)
}
