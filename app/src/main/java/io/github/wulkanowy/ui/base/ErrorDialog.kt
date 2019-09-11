package io.github.wulkanowy.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.github.wulkanowy.R

class ErrorDialog : DialogFragment() {

    private lateinit var error: Throwable

    companion object {
        private const val ARGUMENT_KEY = "Data"

        fun newInstance(error: Throwable): ErrorDialog {
            return ErrorDialog().apply {
                arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, error) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
        arguments?.run {
            error = getSerializable(ARGUMENT_KEY) as Throwable
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_error, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /*StringWriter().let { writer ->
            error.printStackTrace(PrintWriter(writer))

            errorDialogContent.text = writer.toString()
            errorDialogCopy.setOnClickListener {
                ClipData.newPlainText("wulkanowyError", writer.toString()).let { clip ->
                    activity?.getSystemService<ClipboardManager>()?.primaryClip = clip
                }
                Toast.makeText(context, R.string.all_copied, LENGTH_LONG).show()
            }
        }
        errorDialogCancel.setOnClickListener { dismiss() }*/
    }
}

