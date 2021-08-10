package io.github.wulkanowy.utils

import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.ui.modules.main.MainActivity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InAppReviewHelper @Inject constructor(
    @ApplicationContext private val context: Context
)  {

    fun showInAppReview(activity: MainActivity) {
        val manager = ReviewManagerFactory.create(context)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                }
            } else {
                Timber.e(task.exception)
            }
        }
    }
}