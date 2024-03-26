package io.github.wulkanowy.ui.modules.dashboard

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import io.github.wulkanowy.ui.modules.dashboard.DashboardItem.Reorderable
import io.github.wulkanowy.ui.modules.dashboard.adapters.DashboardAdapter
import java.util.Collections

class DashboardItemMoveCallback(
    private val dashboardAdapter: DashboardAdapter,
    private val onUserInteractionEndListener: (List<DashboardItem>) -> Unit = {}
) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled() = true

    override fun isItemViewSwipeEnabled() = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        //Not implemented
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags =
            if (recyclerView.isReorderable(viewHolder)) ItemTouchHelper.UP or ItemTouchHelper.DOWN else 0

        return makeMovementFlags(dragFlags, 0)
    }

    override fun canDropOver(
        recyclerView: RecyclerView,
        current: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = recyclerView.isReorderable(target)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val list = dashboardAdapter.items.toMutableList()

        Collections.swap(list, viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)

        dashboardAdapter.submitList(list)
        return true
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        onUserInteractionEndListener(dashboardAdapter.items.toList())
    }

    private fun RecyclerView.isReorderable(viewHolder: RecyclerView.ViewHolder): Boolean {
        val pos =
            if (adapter == dashboardAdapter) viewHolder.absoluteAdapterPosition else viewHolder.bindingAdapterPosition
        return if (pos != NO_POSITION) {
            val item = dashboardAdapter.items[pos]
            when (item.type.reorderable) {
                Reorderable.Yes -> true
                Reorderable.No -> false
            }
        } else {
            false
        }
    }
}
