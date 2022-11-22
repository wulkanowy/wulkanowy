package io.github.wulkanowy.data.pojos

data class AttendanceData(
    val subjectName: String,
    val allPresences: Int,
    val allAbsences: Int,
) {
    val total: Int
        get() = allPresences + allAbsences

    val presencePercentage: Double
        get() = if (total == 0) 0.0
        else (allPresences.toDouble() / total) * 100
}
