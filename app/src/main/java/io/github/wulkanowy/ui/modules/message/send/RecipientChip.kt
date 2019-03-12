package io.github.wulkanowy.ui.modules.message.send

import android.graphics.drawable.Drawable
import android.net.Uri
import com.pchmn.materialchips.model.ChipInterface
import io.github.wulkanowy.data.db.entities.Recipient

class RecipientChip(var recipient: Recipient) : ChipInterface {

    override fun getAvatarDrawable(): Drawable? = null

    override fun getAvatarUri(): Uri? = null

    override fun getId(): Any = recipient.id

    override fun getLabel(): String = recipient.name

    override fun getInfo(): String = recipient.realName.getRecipientInfo()

    private fun String.getRecipientInfo(): String {
        return substringBeforeLast('-').let {
            when {
                (it == this) -> this
                (it.indexOf('(') != -1) -> this.substringFrom('(')
                (it.indexOf('[') != -1) -> this.substringFrom('[')
                else -> this.substringAfter('-')
            }
        }.trim()
    }

    private fun String.substringFrom(char: Char): String {
        indexOf(char).let {
            return substring(if (it != -1) it else 0)
        }
    }
}
