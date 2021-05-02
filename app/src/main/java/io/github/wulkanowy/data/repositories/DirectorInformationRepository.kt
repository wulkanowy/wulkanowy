package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.DirectorInformationDao
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DirectorInformationRepository @Inject constructor(
    private val directorInformationDb: DirectorInformationDao,
    private val sdk: Sdk,
    private val refreshHelper: AutoRefreshHelper,
) {

    private val saveFetchResultMutex = Mutex()

    private val cacheKey = "director_information"

    fun getDirectorInformationList(student: Student, forceRefresh: Boolean) = networkBoundResource(
        mutex = saveFetchResultMutex,
        shouldFetch = {
            it.isEmpty() || forceRefresh || refreshHelper.isShouldBeRefreshed(
                getRefreshKey(cacheKey, student)
            )
        },
        query = { directorInformationDb.loadAll(student.studentId) },
        fetch = { sdk.init(student).getDirectorInformation().mapToEntities(student) },
        saveFetchResult = { old, new ->
            directorInformationDb.deleteAll(old uniqueSubtract new)
            directorInformationDb.insertAll(new uniqueSubtract old)

            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, student))
        }
    )
}
