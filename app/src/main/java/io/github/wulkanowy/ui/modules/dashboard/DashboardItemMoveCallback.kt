package io.github.wulkanowy.ui.modules.dashboard

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

class DashboardItemMoveCallback(private val dashboardAdapter: DashboardAdapter) :
    ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled() = true

    override fun isItemViewSwipeEnabled() = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        //Not implemented
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) = makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)

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
}
