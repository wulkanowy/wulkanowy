package io.github.wulkanowy.ui.modules.account.accountdetails

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.view.get
import com.avatarfirst.avatargenlib.AvatarConstants
import com.avatarfirst.avatargenlib.AvatarGenerator
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.databinding.FragmentAccountDetailsBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

@AndroidEntryPoint
class AccountDetailsFragment :
    BaseFragment<FragmentAccountDetailsBinding>(R.layout.fragment_account_details),
    AccountDetailsView, MainView.TitledView {

    @Inject
    lateinit var presenter: AccountDetailsPresenter

    override val titleStringId = R.string.account_details_title

    override var subtitleString = ""

    companion object {

        private const val ARGUMENT_KEY = "Data"

        fun newInstance(studentWithSemesters: StudentWithSemesters) =
            AccountDetailsFragment().apply {
                arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, studentWithSemesters) }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            presenter.studentWithSemesters =
                it.getSerializable(ARGUMENT_KEY) as StudentWithSemesters
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccountDetailsBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu[0].isVisible = false
    }

    override fun showAccountData(studentWithSemesters: StudentWithSemesters) {
        binding.accountDetailsName.text = studentWithSemesters.student.studentName
        binding.accountDetailsSchool.text = studentWithSemesters.student.schoolName


        binding.accountDetailsAvatar.setImageDrawable(
            AvatarGenerator.avatarImage(
                requireContext(),
                200,
                AvatarConstants.CIRCLE,
                studentWithSemesters.student.studentName
            )
        )
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}