package io.github.wulkanowy.ui.modules.message.tab

import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.utils.toFormattedString
import me.xdrop.fuzzywuzzy.FuzzySearch
import java.util.Locale
import kotlin.math.pow

data class MessageSearchMatch(val message: Message, val query: String?) {
    val subjectRatio: Int?

    val senderOrRecipientRatio: Int?

    val dateRatio: Int?

    val totalRatioWeighted: Int?

    init {
        subjectRatio = query?.let {
            FuzzySearch.tokenSortPartialRatio(
                it.toLowerCase(Locale.getDefault()),
                message.subject
            )
        }

        senderOrRecipientRatio = query?.let {
            FuzzySearch.tokenSortPartialRatio(
                it.toLowerCase(Locale.getDefault()),
                if (message.sender.isNotEmpty()) message.sender.toLowerCase(Locale.getDefault())
                else message.recipient.toLowerCase(Locale.getDefault())
            )
        }

        dateRatio = query?.let {
            listOf(
                FuzzySearch.ratio(
                    it.toLowerCase(Locale.getDefault()),
                    message.date.toFormattedString("dd.MM").toLowerCase(Locale.getDefault())
                ),
                FuzzySearch.ratio(
                    it.toLowerCase(Locale.getDefault()),
                    message.date.toFormattedString("dd.MM.yyyy").toLowerCase(Locale.getDefault())
                ),
                FuzzySearch.ratio(
                    it.toLowerCase(Locale.getDefault()),
                    message.date.toFormattedString("d MMMMM").toLowerCase(Locale.getDefault())
                ),
                FuzzySearch.ratio(
                    it.toLowerCase(Locale.getDefault()),
                    message.date.toFormattedString("d MMMMM yyyy").toLowerCase(Locale.getDefault())
                )
            ).max()
        }

        if (subjectRatio != null && senderOrRecipientRatio != null && dateRatio != null) {
            totalRatioWeighted = (subjectRatio.toDouble().pow(2)
                + senderOrRecipientRatio.toDouble().pow(2)
                + dateRatio.toDouble().pow(2) * 2
                ).toInt()
        } else {
            totalRatioWeighted = null
        }
    }
}