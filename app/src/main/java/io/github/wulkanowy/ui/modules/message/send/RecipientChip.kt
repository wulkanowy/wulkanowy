package io.github.wulkanowy.ui.modules.message.send

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Recipient

class RecipientChip(context: Context, var recipient: Recipient) :
    Chip(context, null, R.style.Widget_MaterialComponents_Chip_Entry) {

    val id: Long = recipient.id

    init {
        text = recipient.name
        chipIcon = ContextCompat.getDrawable(context, R.drawable.ic_all_account_24dp)
        setTextAppearance(R.style.TextAppearance_AppCompat_Small)
    }
}
