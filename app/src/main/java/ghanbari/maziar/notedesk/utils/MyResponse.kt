package ghanbari.maziar.notedesk.utils

data class MyResponse<out T>(
    val state: DataState,
    val data: T? = null,
    var errorMessage: String? = null,
    val isEmpty: Boolean = false
) {
    enum class DataState {
        LOADING,
        SUCCESS,
        EMPTY,
        ERROR,
    }

    companion object{
        fun<T> loading() = MyResponse<T>(DataState.LOADING)
        fun<T> success(data : T) = MyResponse<T>(DataState.SUCCESS, data = data)
        fun<T> empty() = MyResponse<T>(DataState.EMPTY, isEmpty = true)
        fun<T> error(message : String) = MyResponse<T>(DataState.ERROR, errorMessage = message)
    }
}