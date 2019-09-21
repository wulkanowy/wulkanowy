package io.github.wulkanowy.ui.modules.timetable

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.util.TypedValue
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.ContextCompat
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_timetable.*

class TimetableItem(val lesson: Timetable, private val roomText: String) :
    AbstractFlexibleItem<TimetableItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_timetable

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.apply {
            timetableItemNumber.text = lesson.number.toString()
            timetableItemSubject.text = lesson.subject
            timetableItemRoom.text = if (lesson.room.isNotBlank()) lesson.room else ""
            timetableItemTeacher.text = if (lesson.teacher.isNotBlank()) lesson.teacher else ""
            timetableItemTimeStart.text = lesson.start.toFormattedString("HH:mm")
            timetableItemTimeFinish.text = lesson.end.toFormattedString("HH:mm")
            timetableItemSubject.paintFlags =
                if (lesson.canceled) timetableItemSubject.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else timetableItemSubject.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            if (lesson.info.isNotBlank() && !lesson.changes) {
                timetableItemDescription.visibility = VISIBLE
                timetableItemDescription.text = lesson.info

                timetableItemRoom.visibility = GONE
                timetableItemTeacher.visibility = GONE

                if (lesson.canceled) {
                    timetableItemDescription.setTextColor(holder.view.context.getThemeAttrColor(R.attr.colorPrimary))
                } else {
                    timetableItemDescription.setTextColor(holder.view.context.getThemeAttrColor(R.attr.colorTimetableChange))
                }
            } else {
                timetableItemDescription.visibility = GONE

                timetableItemRoom.visibility = VISIBLE
                timetableItemTeacher.visibility = VISIBLE
            }

            if (lesson.canceled) {
                timetableItemNumber.setTextColor(holder.view.context.getThemeAttrColor(R.attr.colorPrimary))
                timetableItemSubject.setTextColor(holder.view.context.getThemeAttrColor(R.attr.colorPrimary))
            } else {
                if (lesson.changes || lesson.info.isNotBlank()) {
                    timetableItemNumber.setTextColor(holder.view.context.getThemeAttrColor(R.attr.colorTimetableChange))
                } else {
                    timetableItemNumber.setTextColor(holder.view.context.getThemeAttrColor(android.R.attr.textColorPrimary))
                }

                if (lesson.subjectOld.isNotBlank() && lesson.subjectOld != lesson.subject) {
                    timetableItemSubject.setTextColor(holder.view.context.getThemeAttrColor(R.attr.colorTimetableChange))
                } else {
                    timetableItemSubject.setTextColor(holder.view.context.getThemeAttrColor(android.R.attr.textColorPrimary))
                }

                if (lesson.roomOld.isNotBlank() && lesson.roomOld != lesson.room) {
                    timetableItemRoom.setTextColor(holder.view.context.getThemeAttrColor(R.attr.colorTimetableChange))
                } else {
                    timetableItemRoom.setTextColor(holder.view.context.getThemeAttrColor(android.R.attr.textColorSecondary))
                }


                if (lesson.teacherOld.isNotBlank() && lesson.teacherOld != lesson.teacher) {
                    timetableItemTeacher.setTextColor(holder.view.context.getThemeAttrColor(R.attr.colorTimetableChange))
                } else {
                    timetableItemTeacher.setTextColor(holder.view.context.getThemeAttrColor(android.R.attr.textColorPrimary))
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TimetableItem

        if (lesson != other.lesson) return false
        return true
    }

    override fun hashCode(): Int {
        var result = lesson.hashCode()
        result = 31 * result + lesson.id.toInt()
        return result
    }

    class ViewHolder(val view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter), LayoutContainer {
        override val containerView: View
            get() = contentView
    }
}
