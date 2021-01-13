package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate

@Entity(tableName = "StudentInfo")
data class StudentInfo(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    val name: String,

    @ColumnInfo(name = "middle_name")
    val middleName: String,

    @ColumnInfo(name = "id_number")
    val idNumber: Int?,

    @ColumnInfo(name = "last_name")
    val lastName: String,

    @ColumnInfo(name = "birth_date")
    val birthDate: LocalDate,

    @ColumnInfo(name = "birth_place")
    val birthPlace: String,

    @ColumnInfo(name = "family_name")
    val familyName: String,

    @ColumnInfo(name = "polish_citizenship")
    val polishCitizenship: Int,

    @ColumnInfo(name = "mother_name")
    val motherName: String,

    @ColumnInfo(name = "father_name")
    val fatherName: String,

    val gender: Boolean,

    val address: String,

    @ColumnInfo(name = "registered_address")
    val registeredAddress: String,

    @ColumnInfo(name = "correspondence_address")
    val correspondenceAddress: String,

    @ColumnInfo(name = "home_phone")
    val homePhone: String?,

    @ColumnInfo(name = "cell_phone")
    val cellPhone: String?,

    val email: String,

    @ColumnInfo(name = "is_visible_pesel")
    val isVisiblePesel: Boolean,

    @ColumnInfo(name = "hide_address")
    val hideAddress: Boolean,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    @ColumnInfo(name = "has_pesel")
    val hasPesel: Boolean,

    @ColumnInfo(name = "is_pole")
    val isPole: Boolean,

    @ColumnInfo(name = "mother_and_father_names")
    val motherAndFatherNames: String
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}