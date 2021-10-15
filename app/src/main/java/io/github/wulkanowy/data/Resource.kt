package io.github.wulkanowy.data

sealed class Resource<T> {
    companion object {
        fun <T> success(data: T): Resource<T> = Success(data)

        fun <T> error(error: Throwable): Resource<T> = Error(error)

        fun <T> loading(): Resource<T> = Loading()

        fun <T> loading(data: T): Resource<T> = Intermediate(data)
    }

    open class Loading<T> : Resource<T>() {
        val dataOrNull: T?
            get() = when (this) {
                is Intermediate -> data
                else -> null
            }
    }

    data class Intermediate<T>(val data: T) : Loading<T>()

    data class Success<T>(val data: T) : Resource<T>()

    data class Error<T>(val error: Throwable) : Resource<T>()
}

val <T> Resource<T>.dataOrNull: T?
    get() = when (this) {
        is Resource.Success -> this.data
        is Resource.Intermediate -> this.data
        is Resource.Loading -> null
        is Resource.Error -> null
    }

val <T> Resource<T>.errorOrNull: Throwable?
    get() = when (this) {
        is Resource.Error -> this.error
        else -> null
    }

val <T> Resource<T>.isLoading: Boolean
    get() = when (this) {
        is Resource.Loading -> true
        else -> false
    }

fun <T, U> Resource<T>.mapData(block: (T) -> U) = when (this) {
    is Resource.Success -> Resource.success(block(this.data))
    is Resource.Intermediate -> Resource.Intermediate(block(this.data))
    is Resource.Loading -> Resource.loading()
    is Resource.Error -> Resource.error(this.error)
}
