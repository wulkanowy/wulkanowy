package io.github.wulkanowy.utils

import io.github.wulkanowy.Resource
import io.github.wulkanowy.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

inline fun <ResultType> networkBoundResource(
    showSavedOnLoading: Boolean = true,
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> ResultType,
    crossinline saveFetchResult: suspend (old: ResultType, new: ResultType) -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit = { Unit },
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline filterResult: (ResultType) -> ResultType = { it }
) = flow {
    emit(Resource.loading())

    val data = query().first()
    emitAll(if (shouldFetch(data)) {
        if (showSavedOnLoading) emit(Resource.loading(filterResult(data)))

        try {
            saveFetchResult(data, fetch())
            query().map { Resource.success(filterResult(it)) }
        } catch (throwable: Throwable) {
            onFetchFailed(throwable)
            query().map { Resource.error(throwable, it) }
        }
    } else {
        query().map { Resource.success(filterResult(it)) }
    })
}

fun <T> flowWithResource(block: suspend () -> T) = flow {
    emit(Resource.loading())
    try {
        emit(Resource.success(block()))
    } catch (e: Throwable) {
        emit(Resource.error(e))
    }
}

fun <T> Flow<Resource<T>>.afterLoading(callback: () -> Unit) = onEach {
    if (it.status != Status.LOADING) callback()
}
