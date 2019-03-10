package io.github.wulkanowy.ui.modules.timetable

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.android.synthetic.main.dialog_timetable.*

class TimetableDialog : DialogFragment() {

    private lateinit var lesson: Timetable

    companion object {
        private const val ARGUMENT_KEY = "Item"

        fun newInstance(exam: Timetable): TimetableDialog {
            return TimetableDialog().apply {
                arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, exam) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
        arguments?.run {
            lesson = getSerializable(ARGUMENT_KEY) as Timetable
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_timetable, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        timetableDialogTime.text = "${lesson.start.toFormattedString("HH:mm")} - ${lesson.end.toFormattedString("HH:mm")}"

        lesson.run {
            timetableDialogSubject.text = subject

            if (subjectOld.isNotBlank() && subjectOld != subject) {
                timetableDialogSubject.run {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    text = subjectOld
                }
                timetableDialogSubjectNew.run {
                    visibility = VISIBLE
                    text = subject
                }
            }
        }

        lesson.run {
            when {
                teacherOld.isNotBlank() && teacherOld != teacher -> {
                    timetableDialogTeacher.run {
                        visibility = VISIBLE
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        text = teacherOld
                    }
                    if (teacher.isNotBlank()) {
                        timetableDialogTeacherNew.run {
                            visibility = VISIBLE
                            text = teacher
                        }
                    }
                }
                teacher.isNotBlank() -> timetableDialogTeacher.text = teacher
                else -> {
                    timetableDialogTeacherTitle.visibility = GONE
                    timetableDialogTeacher.visibility = GONE
                }
            }
        }

        lesson.group.let {
            if (it.isBlank()) {
                timetableDialogGroupTitle.visibility = GONE
                timetableDialogGroup.visibility = GONE
            } else timetableDialogGroup.text = it
        }

        lesson.run {
            when {
                roomOld.isNotBlank() && roomOld != room -> {
                    timetableDialogRoom.run {
                        visibility = VISIBLE
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        text = roomOld
                    }
                    if (room.isNotBlank()) {
                        timetableDialogRoomNew.run {
                            visibility = VISIBLE
                            text = room
                        }
                    }
                }
                room.isNotBlank() -> timetableDialogRoom.text = room
                else -> {
                    timetableDialogRoomTitle.visibility = GONE
                    timetableDialogRoom.visibility = GONE
                }
            }
        }

        lesson.let {
            if (it.info.isBlank()) {
                timetableDialogChangesTitle.visibility = GONE
                timetableDialogChanges.visibility = GONE
            } else timetableDialogChanges.text = when (true) {
                it.canceled && !it.changes -> "Lekcja odwołana: ${it.info}"
                it.changes && it.teacher.isNotBlank() -> "Zastępstwo: ${it.teacher}"
                else -> it.info.capitalize()
            }
        }

        timetableDialogClose.setOnClickListener { dismiss() }
    }
}
