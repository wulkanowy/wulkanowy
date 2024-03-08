package io.github.wulkanowy.ui.modules.grade.details

import io.github.wulkanowy.data.db.entities.Grade

class SerializableGradleList(var list: List<Grade>) : java.io.Serializable,
    Iterable<Grade> {

    override fun iterator(): Iterator<Grade> = list.iterator()
}
