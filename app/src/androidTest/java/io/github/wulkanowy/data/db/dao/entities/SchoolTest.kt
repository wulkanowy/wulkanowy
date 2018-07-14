package io.github.wulkanowy.data.db.dao.entities

import org.greenrobot.greendao.test.AbstractDaoTestLongPk

class SchoolTest : AbstractDaoTestLongPk<SchoolDao, School>(SchoolDao::class.java) {

    override fun createEntity(key: Long?): School {
        val entity = School()
        entity.id = key
        entity.current = false
        return entity
    }

}
