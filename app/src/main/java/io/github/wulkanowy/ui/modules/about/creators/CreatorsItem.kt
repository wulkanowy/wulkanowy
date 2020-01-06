package io.github.wulkanowy.ui.modules.about.creators

import android.view.View
import com.mikepenz.aboutlibraries.entity.Library
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_creators.*

class CreatorsItem(val creator: Creator) : AbstractFlexibleItem<CreatorsItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_creators

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) = ViewHolder(view, adapter)

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        with(holder) {
            creatorsItemName.text = creator.name
            creatorsItemSummary.text = creator.summary
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreatorsItem

        if (creator != other.creator) return false

        return true
    }

    override fun hashCode() = creator.hashCode()

    class ViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) : FlexibleViewHolder(view, adapter),
        LayoutContainer {

        override val containerView: View? get() = contentView
    }
}
