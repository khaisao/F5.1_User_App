package jp.careapp.counseling.android.ui.blocked

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.* // ktlint-disable no-wildcard-imports
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.network.ApiObjectResponse
import jp.careapp.counseling.android.data.network.FavoriteResponse
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.ui.favourite.EventFavoriteAction
import jp.careapp.counseling.android.utils.event.Event
import jp.careapp.counseling.android.utils.result.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BlockedViewModel @ViewModelInject constructor(
    private val apiService: ApiInterface
) : BaseViewModel(), EventFavoriteAction {

    private val _refreshBlocked = MutableLiveData<Unit>()
    fun forceRefresh() {
        _refreshBlocked.value = Unit
    }
    private val _codeDelete = MutableLiveData<String>()
    fun setCodeBlocked(code: String) {
        _codeDelete.value = code
    }
    var isShowNoData = MutableLiveData(false)

    private val _deleteBlocked: LiveData<Result<ApiObjectResponse<Any>>> =
        _codeDelete.switchMap { code ->
            liveData {
                emit(Result.Loading)
                emit(deleteBlocked(code))
            }
        }
    val deleteBlockedLoading: LiveData<Boolean> = _deleteBlocked.map {
        it == Result.Loading
    }
    private suspend fun deleteBlocked(code: String): Result<ApiObjectResponse<Any>> {
        return try {
            withContext(Dispatchers.IO) {
                apiService.deleteBlocked(code = code).let {
                    Result.Success(it)
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    private val _blockedResult: LiveData<Result<ApiObjectResponse<List<FavoriteResponse>>>> = _refreshBlocked.switchMap {
        liveData {
            emit(Result.Loading)
            emit(getFavoriteBlocked())
        }
    }

    val blockedLoading: LiveData<Boolean> = _blockedResult.map {
        it == Result.Loading
    }

    val uiBlocked: LiveData<List<FavoriteResponse>> = _blockedResult.map {
        (it as? Result.Success)?.data?.dataResponse ?: listOf()
    }

    private suspend fun getFavoriteBlocked(): Result<ApiObjectResponse<List<FavoriteResponse>>> {
        return try {
            withContext(Dispatchers.IO) {
                apiService.getMemberBlocked().let {
                    val typeResponse =
                        object : TypeToken<ApiObjectResponse<List<FavoriteResponse>>>() {}.type
                    var response: ApiObjectResponse<List<FavoriteResponse>>? = null
                    try {
                        isShowNoData.postValue(false)
                        response =
                            Gson().fromJson(it.toString(), typeResponse) as ApiObjectResponse<List<FavoriteResponse>>?
                        Result.Success(response)
                    } catch (e: java.lang.Exception) {
                        isShowNoData.postValue(true)
                        val typeResponse =
                            object : TypeToken<ApiObjectResponse<FavoriteResponse>>() {}.type
                        val data = Gson().fromJson(it, typeResponse) as ApiObjectResponse<FavoriteResponse>
                        var response: ApiObjectResponse<List<FavoriteResponse>>? = ApiObjectResponse<List<FavoriteResponse>>(
                            data.errors,
                            listOf(),
                            data.pagination
                        )
                        Result.Success(response)
                    }
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    private val _error = MediatorLiveData<Event<String>>().apply {
        addSource(_deleteBlocked) { rs ->
            if (rs is Result.Error)
                value = Event(content = rs.throwable.message ?: "")
        }
        addSource(_blockedResult) { rs ->
            if (rs is Result.Error)
                value = Event(content = rs.throwable.message ?: "")
        }
    }
    val error: LiveData<Event<String>> = _error
    override fun onclickItem(item: FavoriteResponse) {
        // TODO
    }
    private val _showDialogAction = MutableLiveData<Event<FavoriteResponse>>()
    val showDialogAction: LiveData<Event<FavoriteResponse>> = _showDialogAction
    override fun onClickRelease(item: FavoriteResponse) {
        _showDialogAction.value = Event(item)
    }
}
