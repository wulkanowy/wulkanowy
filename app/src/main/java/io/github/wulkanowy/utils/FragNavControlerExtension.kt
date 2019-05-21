package io.github.wulkanowy.utils

import androidx.fragment.app.Fragment
import com.ncapdevi.fragnav.FragNavController

inline fun FragNavController.setOnViewChangeListener(crossinline listener: (index: Int) -> Unit) {
    transactionListener = object : FragNavController.TransactionListener {
        override fun onFragmentTransaction(fragment: Fragment?, transactionType: FragNavController.TransactionType) {
            listener(-1)
        }

        override fun onTabTransaction(fragment: Fragment?, index: Int) {
            listener(index)
        }
    }
}

fun FragNavController.safelyPopFragment() {
    if (!isRootFragment) popFragment()
}
