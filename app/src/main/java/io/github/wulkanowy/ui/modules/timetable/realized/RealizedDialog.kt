package io.github.wulkanowy.ui.modules.timetable.realized

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Realized
import kotlinx.android.synthetic.main.dialog_realized.*

class RealizedDialog : DialogFragment() {

    private lateinit var realized: Realized

    companion object {
        private const val ARGUMENT_KEY = "Item"

        fun newInstance(exam: Realized): RealizedDialog {
            return RealizedDialog().apply {
                arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, exam) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
        arguments?.run {
            realized = getSerializable(RealizedDialog.ARGUMENT_KEY) as Realized
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_realized, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        realizedDialogSubject.text = realized.subject
        realizedDialogTopic.text = realized.topic
        realizedDialogTeacher.text = realized.teacher
        realizedDialogAbsence.text = realized.absence
        realizedDialogChanges.text = realized.substitution
        realizedDialogResources.text = realized.resources

        realized.substitution.let {
            if (it.isBlank()) {
                realizedDialogChangesTitle.visibility = View.GONE
                realizedDialogChanges.visibility = View.GONE
            } else realizedDialogChanges.text = it
        }

        realized.absence.let {
            if (it.isBlank()) {
                realizedDialogAbsenceTitle.visibility = View.GONE
                realizedDialogAbsence.visibility = View.GONE
            } else realizedDialogAbsence.text = it
        }

        realized.resources.let {
            if (it.isBlank()) {
                realizedDialogResourcesTitle.visibility = View.GONE
                realizedDialogResources.visibility = View.GONE
            } else realizedDialogResources.text = it
        }

        realizedDialogClose.setOnClickListener { dismiss() }
    }
}
