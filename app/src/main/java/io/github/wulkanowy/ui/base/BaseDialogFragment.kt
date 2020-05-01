package io.github.wulkanowy.ui.base

import android.widget.Toast
import androidx.viewbinding.ViewBinding
import dagger.android.support.DaggerAppCompatDialogFragment

abstract class BaseDialogFragment<DB : ViewBinding> : DaggerAppCompatDialogFragment(), BaseView {

    protected open var _binding: DB? = null

    protected val binding get() = _binding!!

    override fun showError(text: String, error: Throwable) {
        showMessage(text)
    }

    override fun showMessage(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    override fun showExpiredDialog() {
        (activity as? BaseActivity<*, *>)?.showExpiredDialog()
    }

    override fun openClearLoginView() {
        (activity as? BaseActivity<*, *>)?.openClearLoginView()
    }

    override fun showErrorDetailsDialog(error: Throwable) {
        ErrorDialog.newInstance(error).show(childFragmentManager, error.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
