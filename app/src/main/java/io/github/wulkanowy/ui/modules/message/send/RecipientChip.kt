package io.github.wulkanowy.ui.modules.message.send

import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.materialchipsinput.MaterialChipItem

class RecipientChip(var recipient: Recipient) : MaterialChipItem {

    override val title: String
        get() = recipient.name

    override val summary: String
        get() = recipient.realName.run {
            substringBeforeLast("-").let { sub ->
                when {
                    (sub == this) -> this
                    (sub.indexOf('(') != -1) -> indexOf("(").let { substring(if (it != -1) it else 0) }
                    (sub.indexOf('[') != -1) -> indexOf("[").let { substring(if (it != -1) it else 0) }
                    else -> substringAfter('-')
                }
            }.trim()
        }
}
