package io.github.wulkanowy.ui.modules.homework.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.ui.base.BaseDialogFragment
import io.github.wulkanowy.ui.modules.homework.HomeworkFragment
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.android.synthetic.main.dialog_homework.*
import javax.inject.Inject

class HomeworkDetailsDialog : BaseDialogFragment(), HomeworkDetailsView {

    @Inject
    lateinit var presenter: HomeworkDetailsPresenter

    private lateinit var homework: Homework

    companion object {
        private const val ARGUMENT_KEY = "Item"

        fun newInstance(homework: Homework): HomeworkDetailsDialog {
            return HomeworkDetailsDialog().apply {
                arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, homework) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
        arguments?.run {
            homework = getSerializable(ARGUMENT_KEY) as Homework
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_homework, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        homeworkDialogDate.text = homework.date.toFormattedString()
        homeworkDialogEntryDate.text = homework.entryDate.toFormattedString()
        homeworkDialogSubject.text = homework.subject
        homeworkDialogTeacher.text = homework.teacher
        homeworkDialogContent.text = homework.content
        homeworkDialogRead.text = view?.context?.getString(if (homework.isDone) R.string.homework_mark_as_undone else R.string.homework_mark_as_done)
        homeworkDialogRead.setOnClickListener { presenter.toggleDone(homework) }
        homeworkDialogClose.setOnClickListener { dismiss() }
    }

    override fun updateMarkAsDoneLabel(isDone: Boolean) {
        (parentFragment as? HomeworkFragment)?.onReloadList()
        homeworkDialogRead.text = view?.context?.getString(if (isDone) R.string.homework_mark_as_undone else R.string.homework_mark_as_done)
    }
}
