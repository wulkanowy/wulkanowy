package io.github.wulkanowy.ui.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class BaseFragmentPagerAdapter : FragmentStateAdapter, TabLayoutMediator.TabConfigurationStrategy {

    constructor(fragment: Fragment) : super(fragment)
    constructor(activity: FragmentActivity) : super(activity)

    private val pages = mutableListOf<Pair<Fragment, String?>>()

    fun addFragments(fragments: List<Fragment>) {
        val size = pages.size
        pages.addAll(fragments.map { it to null })
        notifyItemRangeInserted(size, fragments.size)
    }

    fun addFragmentsWithTitle(pages: Map<Fragment, String>) {
        val size = pages.size
        this.pages.addAll(pages.map { it.key to it.value })
        notifyItemRangeInserted(size, pages.size)
    }

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        pages[position].second?.let {
            tab.text = it
        }
    }

    fun getFragmentInstance(position: Int) = pages[position].first

    override fun getItemCount() = pages.size

    override fun createFragment(position: Int): Fragment = pages[position].first
}
