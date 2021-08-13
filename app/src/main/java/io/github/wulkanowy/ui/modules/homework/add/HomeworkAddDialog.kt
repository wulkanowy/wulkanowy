package io.github.wulkanowy.ui.modules.homework.add

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.databinding.DialogHomeworkAddBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import javax.inject.Inject

@AndroidEntryPoint
class HomeworkAddDialog : BaseDialogFragment<DialogHomeworkAddBinding>(), HomeworkAddView {

    @Inject
    lateinit var presenter: HomeworkAddPresenter

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
        with(binding) {
//            homeworkDialogRead.text =
//                view?.context?.getString(if (homework.isDone) R.string.homework_mark_as_undone else R.string.homework_mark_as_done)
//            homeworkDialogRead.setOnClickListener { presenter.toggleDone(homework) }
            homeworkDialogClose.setOnClickListener { dismiss() }
        }

        if (presenter.isHomeworkFullscreen) {
            dialog?.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
        } else {
            dialog?.window?.setLayout(WRAP_CONTENT, WRAP_CONTENT)
        }

        with(binding) {
//            homeworkDialogDate.text = homework?.date?.toFormattedString()
//            homeworkDialogSubject.text = homework?.subject
//            homeworkDialogTeacher.text = homework?.teacher
//            homeworkDialogContent.text = homework?.content
            homeworkDialogFullScreen.visibility =
                if (presenter.isHomeworkFullscreen) android.view.View.GONE else android.view.View.VISIBLE
            homeworkDialogFullScreenExit.visibility =
                if (presenter.isHomeworkFullscreen) android.view.View.VISIBLE else android.view.View.GONE
            homeworkDialogFullScreen.setOnClickListener {
                homeworkDialogFullScreen.visibility = android.view.View.GONE
                homeworkDialogFullScreenExit.visibility = android.view.View.VISIBLE
                dialog?.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
                presenter.isHomeworkFullscreen = true

            }
            homeworkDialogFullScreenExit.setOnClickListener {
                homeworkDialogFullScreen.visibility = android.view.View.VISIBLE
                homeworkDialogFullScreenExit.visibility = android.view.View.GONE
                dialog?.window?.setLayout(WRAP_CONTENT, WRAP_CONTENT)
                presenter.isHomeworkFullscreen = false

            }
        }

//        with(binding.homeworkDialogRecycler) {
//            layoutManager = LinearLayoutManager(context)
//            adapter = addAdapter.apply {
//                //onAttachmentClickListener = { context.openInternetBrowser(it, ::showMessage) }
//                onFullScreenClickListener = {
//                    dialog?.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
//                    presenter.isHomeworkFullscreen = true
//                }
//                onFullScreenExitClickListener = {
//                    dialog?.window?.setLayout(WRAP_CONTENT, WRAP_CONTENT)
//                    presenter.isHomeworkFullscreen = false
//                }
//                isHomeworkFullscreen = presenter.isHomeworkFullscreen
//            }
//        }
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
