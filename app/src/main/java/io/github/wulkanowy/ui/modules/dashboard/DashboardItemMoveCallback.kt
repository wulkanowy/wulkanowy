package io.github.wulkanowy.ui.modules.dashboard

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

class DashboardItemMoveCallback(
    private val dashboardAdapter: DashboardAdapter,
    private var onUserInteractionEndListener: (List<DashboardItem>) -> Unit = {}
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
        val viewType = dashboardAdapter.getItemViewType(viewHolder.bindingAdapterPosition)
        val isAdminMessageItem = viewType == DashboardItem.Type.ADMIN_MESSAGE.ordinal
        val isAccountItem = viewType == DashboardItem.Type.ACCOUNT.ordinal

        val dragFlags = if (!isAccountItem && !isAdminMessageItem) {
            ItemTouchHelper.UP or ItemTouchHelper.DOWN
        } else 0

        return makeMovementFlags(dragFlags, 0)
    }

    override fun canDropOver(
        recyclerView: RecyclerView,
        current: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val viewType = dashboardAdapter.getItemViewType(target.bindingAdapterPosition)
        val isAdminMessageItem = viewType == DashboardItem.Type.ADMIN_MESSAGE.ordinal
        val isAccountItem = viewType == DashboardItem.Type.ACCOUNT.ordinal

        return !isAccountItem && !isAdminMessageItem
    }

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
}
