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

    override val homeworkAddSuccess: String
        get() = getString(R.string.homework_success)

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

    override fun checkFields() {
        with(binding) {
            var subject: String? = null
            var teacher: String? = null
            var date: LocalDate? = null
            var content: String? = null

            with(homeworkDialogSubject) {
                if (editText?.text.isNullOrBlank()) {
                    isErrorEnabled = true
                    error = getString(R.string.all_no_data)
                } else {
                    subject = editText?.text.toString()
                    isErrorEnabled = false
                }
            }
            with(homeworkDialogTeacher) {
                if (editText?.text.isNullOrBlank()) {
                    isErrorEnabled = true
                    error = getString(R.string.all_no_data)
                } else {
                    teacher = editText?.text.toString()
                    isErrorEnabled = false
                }
            }
            with(homeworkDialogDate) {
                if (editText?.text.isNullOrBlank()) {
                    isErrorEnabled = true
                    error = getString(R.string.all_no_data)
                } else {
                    date = editText?.text.toString().toLocalDate()
                    isErrorEnabled = false
                }
            }
            with(homeworkDialogContent) {
                if (editText?.text.isNullOrBlank()) {
                    isErrorEnabled = true
                    error = getString(R.string.all_no_data)
                } else {
                    content = editText?.text.toString()
                    isErrorEnabled = false
                }
            }
            if (subject != null && teacher != null && date != null && content != null) {
                presenter.addHomework(subject!!, teacher!!, date!!, content!!)
            }
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
