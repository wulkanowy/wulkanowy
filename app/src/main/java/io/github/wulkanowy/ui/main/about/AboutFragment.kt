package io.github.wulkanowy.ui.main.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.LibsFragmentCompat
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.main.MainView
import javax.inject.Inject

class AboutFragment : BaseFragment(), MainView.TitledView {

    @Inject
    lateinit var fragmentCompat: LibsFragmentCompat

    companion object {
        fun newInstance() = AboutFragment()
    }

    override val titleStringId: Int
        get() = R.string.about_title

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return Bundle().apply {
            putSerializable("data", LibsBuilder()
                    .withAboutAppName(getString(R.string.app_name))
                    .withAboutVersionShown(true)
                    .withAboutIconShown(true)
                    .withLicenseShown(true))
        }.let {
            fragmentCompat.onCreateView(inflater.context, inflater, container, savedInstanceState, it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentCompat.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        fragmentCompat.onDestroyView()
        super.onDestroyView()
    }
}

