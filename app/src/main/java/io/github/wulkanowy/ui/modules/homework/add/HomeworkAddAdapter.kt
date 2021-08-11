package io.github.wulkanowy.ui.modules.homework.add

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.databinding.DialogHomeworkAddBinding
import javax.inject.Inject

class HomeworkAddAdapter @Inject constructor() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isHomeworkFullscreen = false

    var onFullScreenClickListener = {}

    var onFullScreenExitClickListener = {}

    override fun getItemCount() = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return AddViewHolder(DialogHomeworkAddBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bindAddViewHolder(holder as AddViewHolder)
    }

    private fun bindAddViewHolder(holder: AddViewHolder) {
        with(holder.binding) {
//            homeworkDialogDate.text = homework?.date?.toFormattedString()
//            homeworkDialogSubject.text = homework?.subject
//            homeworkDialogTeacher.text = homework?.teacher
//            homeworkDialogContent.text = homework?.content
            homeworkDialogFullScreen.visibility = if (isHomeworkFullscreen) GONE else VISIBLE
            homeworkDialogFullScreenExit.visibility = if (isHomeworkFullscreen) VISIBLE else GONE
            homeworkDialogFullScreen.setOnClickListener {
                homeworkDialogFullScreen.visibility = GONE
                homeworkDialogFullScreenExit.visibility = VISIBLE
                onFullScreenClickListener()
            }
            homeworkDialogFullScreenExit.setOnClickListener {
                homeworkDialogFullScreen.visibility = VISIBLE
                homeworkDialogFullScreenExit.visibility = GONE
                onFullScreenExitClickListener()
            }
        }
    }

    class AddViewHolder(val binding: DialogHomeworkAddBinding) :
        RecyclerView.ViewHolder(binding.root)
}
