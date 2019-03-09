package io.github.wulkanowy.ui.modules.message.send

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Recipient

@SuppressLint("ViewConstructor", "PrivateResource")
class RecipientChip(context: Context, var recipient: Recipient) :
    Chip(ContextThemeWrapper(context, R.style.Theme_MaterialComponents_Light)) {

    val id: Long = recipient.id

    init {
        text = recipient.name
        chipIcon = ContextCompat.getDrawable(context, R.drawable.ic_all_account_24dp)
        setTextAppearance(R.style.TextAppearance_AppCompat_Small)
    }
}
