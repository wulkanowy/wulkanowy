package io.github.wulkanowy.ui.modules.grade.details

import android.content.Context
import android.graphics.Canvas
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

class GradeDetailsHeaderItemDecoration(ctx: Context) : DividerItemDecoration(ctx, VERTICAL) {

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.save()
        val dividerLeft = parent.paddingLeft
        val dividerRight = parent.width - parent.paddingRight
        val childCount = parent.childCount

        for (i in 1 until childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            if (position == RecyclerView.NO_POSITION) return
            val viewType = parent.adapter?.getItemViewType(position) ?: GradeDetailsItem.ViewType.ITEM.id

            if (viewType == GradeDetailsItem.ViewType.HEADER.id) {
                val params = child.layoutParams as RecyclerView.LayoutParams
                val dividerTop = child.top + params.bottomMargin
                val dividerBottom = dividerTop + drawable!!.intrinsicHeight
                drawable?.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                drawable?.draw(canvas)
            }
        }
        canvas.restore()
    }
}
