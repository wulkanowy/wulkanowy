package io.github.wulkanowy.ui.modules.homework.add

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.DialogHomeworkAddBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import io.github.wulkanowy.utils.toFormattedString
import io.github.wulkanowy.utils.toLocalDate
import io.github.wulkanowy.utils.toLocalDateTime
import io.github.wulkanowy.utils.toTimestamp
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class HomeworkAddDialog : BaseDialogFragment<DialogHomeworkAddBinding>(), HomeworkAddView {

    @Inject
    lateinit var presenter: HomeworkAddPresenter

    private var date: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogHomeworkAddBinding.inflate(inflater).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttachView(this)
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        if (presenter.isHomeworkFullscreen) {
            dialog?.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
        } else {
            dialog?.window?.setLayout(WRAP_CONTENT, WRAP_CONTENT)
        }

        with(binding) {
            homeworkDialogAdd.setOnClickListener { presenter.onAddHomeworkClicked() }
            homeworkDialogClose.setOnClickListener { dismiss() }
            homeworkDialogDate.editText?.setOnClickListener { presenter.showDatePicker(date) }
            homeworkDialogFullScreen.visibility =
                if (presenter.isHomeworkFullscreen) View.GONE else View.VISIBLE
            homeworkDialogFullScreenExit.visibility =
                if (presenter.isHomeworkFullscreen) View.VISIBLE else View.GONE
            homeworkDialogFullScreen.setOnClickListener {
                homeworkDialogFullScreen.visibility = View.GONE
                homeworkDialogFullScreenExit.visibility = View.VISIBLE
                dialog?.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
                presenter.isHomeworkFullscreen = true

            }
            homeworkDialogFullScreenExit.setOnClickListener {
                homeworkDialogFullScreen.visibility = View.VISIBLE
                homeworkDialogFullScreenExit.visibility = View.GONE
                dialog?.window?.setLayout(WRAP_CONTENT, WRAP_CONTENT)
                presenter.isHomeworkFullscreen = false

            }
        }
    }

    override fun onAddClicked() {
        with(binding) {
            val subject: String = homeworkDialogSubject.editText?.text.toString()
            val teacher: String = homeworkDialogTeacher.editText?.text.toString()
            val date: String = homeworkDialogDate.editText?.text.toString()
            val content: String = homeworkDialogContent.editText?.text.toString()
            presenter.checkFields(subject, teacher, date, content)
            if (presenter.checkFields(subject, teacher, date, content)) {
                presenter.addHomework(subject, teacher, date.toLocalDate(), content)
            }
        }
    }

    override fun showSuccessMessage() {
        showMessage(getString(R.string.homework_add_success))
    }

    override fun setErrorSubjectRequired() {
        with(binding.homeworkDialogSubject) {
            isErrorEnabled = true
            error = getString(R.string.all_no_data)
        }
    }

    override fun setErrorTeacherRequired() {
        with(binding.homeworkDialogTeacher) {
            isErrorEnabled = true
            error = getString(R.string.all_no_data)
        }
    }

    override fun setErrorDateRequired() {
        with(binding.homeworkDialogDate) {
            isErrorEnabled = true
            error = getString(R.string.all_no_data)
        }
    }

    override fun setErrorContentRequired() {
        with(binding.homeworkDialogContent) {
            isErrorEnabled = true
            error = getString(R.string.all_no_data)
        }
    }

    override fun clearErrors() {
        with(binding.homeworkDialogSubject) {
            isErrorEnabled = false
            error = ""
        }
        with(binding.homeworkDialogTeacher) {
            isErrorEnabled = false
            error = ""
        }
        with(binding.homeworkDialogDate) {
            isErrorEnabled = false
            error = ""
        }
        with(binding.homeworkDialogContent) {
            isErrorEnabled = false
            error = ""
        }
    }

    override fun closeDialog() {
        dismiss()
    }

    override fun showDatePickerDialog(currentDate: LocalDate) {

        val constraintsBuilder = CalendarConstraints.Builder().apply {
            setStart(LocalDate.now().toEpochDay())
        }
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(constraintsBuilder.build())
                .setSelection(currentDate.toTimestamp())
                .build()

        datePicker.addOnPositiveButtonClickListener {
            date = it.toLocalDateTime().toLocalDate()
            binding.homeworkDialogDate.editText?.setText(date!!.toFormattedString())
        }

        datePicker.show(this.parentFragmentManager, "null")
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
