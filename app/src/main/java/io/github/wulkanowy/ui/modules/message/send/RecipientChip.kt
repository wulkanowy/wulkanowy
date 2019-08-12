package io.github.wulkanowy.ui.modules.message.send

import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.materialchipsinput.MaterialChipItem

class RecipientChip(var recipient: Recipient) : MaterialChipItem {

    override val title get() = recipient.name

    override val summary: String
        get() {
            val name = recipient.realName
            val substring = name.substringBeforeLast("-")

            return when {
                substring == name -> name
                substring.indexOf("(") != -1 -> name.indexOf("(").let { name.substring(if (it != -1) it else 0) }
                substring.indexOf("[") != -1 -> name.indexOf("[").let { name.substring(if (it != -1) it else 0) }
                else -> name.substringAfter("-")
            }.trim()
        }
}
