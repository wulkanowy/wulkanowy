package io.github.wulkanowy.utils

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.mapData
import io.github.wulkanowy.ui.base.ErrorHandler
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
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
    emit(Resource.loading())

    val data = query().first()
    emitAll(if (shouldFetch(data)) {
        if (showSavedOnLoading) emit(Resource.loading(filterResult(data)))

        try {
            val newData = fetch(data)
            mutex.withLock { saveFetchResult(query().first(), newData) }
            query().map { Resource.success(filterResult(it)) }
        } catch (throwable: Throwable) {
            onFetchFailed(throwable)
            query().map { Resource.error(throwable) }
        }
    } else {
        query().map { Resource.success(filterResult(it)) }
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
    emit(Resource.loading())

    val data = query().first()
    emitAll(if (shouldFetch(data)) {
        if (showSavedOnLoading) emit(Resource.loading(mapResult(data)))

        try {
            val newData = fetch(data)
            mutex.withLock { saveFetchResult(query().first(), newData) }
            query().map { Resource.success(mapResult(it)) }
        } catch (throwable: Throwable) {
            onFetchFailed(throwable)
            query().map { Resource.error(throwable) }
        }
    } else {
        query().map { Resource.success(mapResult(it)) }
    })
}

fun <T> flowWithResource(block: suspend () -> T) = flow {
    emit(Resource.loading())
    emit(Resource.success(block()))
}.catch { emit(Resource.error(it)) }

@OptIn(FlowPreview::class)
fun <T> flowWithResourceIn(block: suspend () -> Flow<Resource<T>>) = flow {
    emit(Resource.loading())
    emitAll(block().filter { it is Resource.Intermediate || it !is Resource.Loading})
}.catch { emit(Resource.error(it)) }

fun <T> Flow<Resource<T>>.afterLoading(callback: () -> Unit) = onEach {
    if (it !is Resource.Loading) callback()
}

fun <T> Flow<Resource<T>>.logStatus(name: String, showData: Boolean = false) = onEach {
    val desc = when(it) {
        is Resource.Loading -> "started"
        is Resource.Intermediate -> "intermediate data received" + if (showData) " (data: `${it.data}`)" else ""
        is Resource.Success -> "success" + if (showData) " (data: `${it.data}`)" else ""
        is Resource.Error -> "exception occurred: ${it.error}"
    }
    Timber.i("$name: $desc")
}

fun <T, U> Flow<Resource<T>>.mapData(block: (T) -> U) = map {
    it.mapData(block)
}

fun <T> Flow<Resource<T>>.onData(block: (T) -> Unit) = onEach {
    it.mapData { data ->
        block(data)
        data
    }
}

fun <T> Flow<Resource<T>>.onLoading(block: suspend () -> Unit) = onEach {
    if (it is Resource.Loading) {
        block()
    }
}

fun <T> Flow<Resource<T>>.onSuccess(block: suspend (T) -> Unit) = onEach {
    if (it is Resource.Success) {
        block(it.data)
    }
}

fun <T> Flow<Resource<T>>.onError(block: (Throwable) -> Unit) = onEach {
    if (it is Resource.Error) {
        block(it.error)
    }
}

fun <T> Flow<Resource<T>>.withErrorHandler(handler: ErrorHandler) = onError {
    handler.dispatch(it)
}

// TODO throw exception on Resource.Error? Otherwise potentially infinite loading for no
//  apparent reason can occur
suspend fun <T> Flow<Resource<T>>.toSuccess() = filterIsInstance<Resource.Success<T>>().first()

suspend fun <T> Flow<Resource<T>>.toFirstResult() = filter { it !is Resource.Loading }.first()

suspend fun <T> Flow<Resource<T>>.waitForResult() =
    takeWhile { it is Resource.Loading }.collect()
