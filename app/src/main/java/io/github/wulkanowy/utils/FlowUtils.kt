package io.github.wulkanowy.utils

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.mapData
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

inline fun <ResultType, RequestType> networkBoundResource(
    mutex: Mutex = Mutex(),
    showSavedOnLoading: Boolean = true,
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend (ResultType) -> RequestType,
    crossinline saveFetchResult: suspend (old: ResultType, new: RequestType) -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit = { },
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline filterResult: (ResultType) -> ResultType = { it }
) = flow {
    emit(Resource.Loading())

    val data = query().first()
    emitAll(if (shouldFetch(data)) {
        if (showSavedOnLoading) emit(Resource.Intermediate(filterResult(data)))

        try {
            val newData = fetch(data)
            mutex.withLock { saveFetchResult(query().first(), newData) }
            query().map { Resource.Success(filterResult(it)) }
        } catch (throwable: Throwable) {
            onFetchFailed(throwable)
            query().map { Resource.Error(throwable) }
        }
    } else {
        query().map { Resource.Success(filterResult(it)) }
    })
}

@JvmName("networkBoundResourceWithMap")
inline fun <ResultType, RequestType, T> networkBoundResource(
    mutex: Mutex = Mutex(),
    showSavedOnLoading: Boolean = true,
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend (ResultType) -> RequestType,
    crossinline saveFetchResult: suspend (old: ResultType, new: RequestType) -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit = { },
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline mapResult: (ResultType) -> T
) = flow {
    emit(Resource.Loading())

    val data = query().first()
    emitAll(if (shouldFetch(data)) {
        if (showSavedOnLoading) emit(Resource.Intermediate(mapResult(data)))

        try {
            val newData = fetch(data)
            mutex.withLock { saveFetchResult(query().first(), newData) }
            query().map { Resource.Success(mapResult(it)) }
        } catch (throwable: Throwable) {
            onFetchFailed(throwable)
            query().map { Resource.Error(throwable) }
        }
    } else {
        query().map { Resource.Success(mapResult(it)) }
    })
}

fun <T> resourceFlow(block: suspend () -> T) = flow {
    emit(Resource.Loading())
    emit(Resource.Success(block()))
}.catch { emit(Resource.Error(it)) }

@OptIn(FlowPreview::class)
fun <T> flatResourceFlow(block: suspend () -> Flow<Resource<T>>) = flow {
    emit(Resource.Loading())
    emitAll(block().filter { it is Resource.Intermediate || it !is Resource.Loading })
}.catch { emit(Resource.Error(it)) }

fun <T> Flow<Resource<T>>.logResourceStatus(name: String, showData: Boolean = false) = onEach {
    val description = when (it) {
        is Resource.Loading -> "started"
        is Resource.Intermediate -> "intermediate data received" + if (showData) " (data: `${it.data}`)" else ""
        is Resource.Success -> "success" + if (showData) " (data: `${it.data}`)" else ""
        is Resource.Error -> "exception occurred: ${it.error}"
    }
    Timber.i("$name: $description")
}

fun <T, U> Flow<Resource<T>>.mapResourceData(block: (T) -> U) = map {
    it.mapData(block)
}

fun <T> Flow<Resource<T>>.onResourceData(block: (T) -> Unit) = onEach {
    if (it is Resource.Success) {
        block(it.data)
    } else if (it is Resource.Intermediate) {
        block(it.data)
    }
}

fun <T> Flow<Resource<T>>.onResourceLoading(block: suspend () -> Unit) = onEach {
    if (it is Resource.Loading) {
        block()
    }
}

fun <T> Flow<Resource<T>>.onResourceSuccess(block: suspend (T) -> Unit) = onEach {
    if (it is Resource.Success) {
        block(it.data)
    }
}

fun <T> Flow<Resource<T>>.onResourceError(block: (Throwable) -> Unit) = onEach {
    if (it is Resource.Error) {
        block(it.error)
    }
}

fun <T> Flow<Resource<T>>.onResourceNotLoading(block: () -> Unit) = onEach {
    if (it !is Resource.Loading) {
        block()
    }
}

suspend fun <T> Flow<Resource<T>>.toFirstResult() =
    filter { it !is Resource.Loading }.first()

suspend fun <T> Flow<Resource<T>>.waitForResult() =
    takeWhile { it is Resource.Loading }.collect()
