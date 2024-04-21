package io.github.wulkanowy.data.exceptions

class NoSuchStudentException(id: Long) :
    Exception("There is no student with id $id in database")
