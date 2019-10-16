package io.github.wulkanowy.ui.modules.schoolandteachers.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersChildView
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersFragment

class SchoolFragment : BaseFragment(), MainView.TitledView, SchoolAndTeachersChildView {

    companion object {
        fun newInstance() = SchoolFragment()
    }

    override val titleStringId get() = R.string.school_title

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_school, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        notifyParentDataLoaded() //
//        presenter.onAttachView(this)
    }

    override fun notifyParentDataLoaded() {
        (parentFragment as? SchoolAndTeachersFragment)?.onChildFragmentLoaded()
    }

    override fun onParentLoadData(forceRefresh: Boolean) {
//        presenter.onParentViewLoadData(forceRefresh)
    }
}
