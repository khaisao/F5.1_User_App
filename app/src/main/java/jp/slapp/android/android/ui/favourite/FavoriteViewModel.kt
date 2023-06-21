package jp.slapp.android.android.ui.favourite

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import jp.careapp.core.base.BaseViewModel
import jp.slapp.android.android.data.network.ApiObjectResponse
import jp.slapp.android.android.data.network.ConsultantResponse
import jp.slapp.android.android.data.network.FavoriteResponse
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.network.ApiInterface
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.android.utils.event.Event
import jp.slapp.android.android.utils.extensions.combine
import jp.slapp.android.android.utils.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoriteViewModel @ViewModelInject constructor(
    private val apiService: ApiInterface,
) : BaseViewModel() {

    private val _refreshFavorite = MutableLiveData<Unit>()
    fun forceRefresh() {
        _refreshFavorite.value = Unit
    }

    private val _codeDelete = MutableLiveData<String>()

    var isShowNoData = MutableLiveData(false)
    private val _deleteFavorite: LiveData<Result<ApiObjectResponse<Any>>> =
        _codeDelete.switchMap { code ->
            liveData {
                emit(Result.Loading)
                emit(deleteFavorite(code))
            }
        }
    val deleteFavoriteLoading: LiveData<Boolean> = _deleteFavorite.map {
        it == Result.Loading
    }
    fun setCodeFavarite(code: String) {
        _codeDelete.value = code
    }

    private suspend fun deleteFavorite(code: String): Result<ApiObjectResponse<Any>> {
        return try {
            withContext(Dispatchers.IO) {
                apiService.deleteFavorite(code = code).let {
                    Result.Success(it)
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private val _favoriteResult: LiveData<Result<ApiObjectResponse<List<FavoriteResponse>>>> =
        _refreshFavorite.switchMap {
            liveData {
                emit(Result.Loading)
                emit(getFavoriteResult())
            }
        }

    val favoriteLoading: LiveData<Boolean> = _favoriteResult.map {
        it == Result.Loading
    }
    val uifavorite: LiveData<List<FavoriteResponse>> = _favoriteResult.map {
        (it as? Result.Success)?.data?.dataResponse ?: listOf()
    }

    private suspend fun getFavoriteResult(): Result<ApiObjectResponse<List<FavoriteResponse>>> {
        return try {
            withContext(Dispatchers.IO) {
                apiService.getMemberFavorite().let {
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
                        val data =
                            Gson().fromJson(it, typeResponse) as ApiObjectResponse<FavoriteResponse>
                        var response: ApiObjectResponse<List<FavoriteResponse>>? =
                            ApiObjectResponse(data.errors, listOf(), data.pagination)
                        Result.Success(response)
                    }
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    private val _blockedResult: LiveData<Result<ApiObjectResponse<List<FavoriteResponse>>>> =
        _refreshFavorite.switchMap {
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
                        response =
                            Gson().fromJson(it.toString(), typeResponse) as ApiObjectResponse<List<FavoriteResponse>>?
                        Result.Success(response)
                    } catch (e: java.lang.Exception) {
                        val typeResponse =
                            object : TypeToken<ApiObjectResponse<FavoriteResponse>>() {}.type
                        val data =
                            Gson().fromJson(it, typeResponse) as ApiObjectResponse<FavoriteResponse>
                        var response: ApiObjectResponse<List<FavoriteResponse>>? =
                            ApiObjectResponse<List<FavoriteResponse>>(data.errors, listOf(), data.pagination)
                        Result.Success(response)
                    }
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    val uiDataResult: LiveData<List<FavoriteResponse>> =
        uiBlocked.combine(uifavorite) { dataA, dataB ->
            if (dataA.isEmpty())
                dataB
            else if (dataB.isEmpty())
                listOf()
            else {
                val a = dataA.map { it.code }
                val b = dataB.toMutableList()
                dataB.toMutableList().forEach {
                    if (a.contains(it.code))
                        b.remove(it)
                }
                if (b.isEmpty())
                    isShowNoData.postValue(true)
                b.toList()
            }
        }

    private val _error = MediatorLiveData<Event<String>>().apply {
        addSource(_favoriteResult) { rs ->
            if (rs is Result.Error)
                value = Event(content = rs.throwable.message ?: "")
        }
        addSource(_deleteFavorite) { rs ->
            if (rs is Result.Error)
                value = Event(content = rs.throwable.message ?: "")
        }
        addSource(_blockedResult) { rs ->
            if (rs is Result.Error)
                value = Event(content = rs.throwable.message ?: "")
        }
    }
    val error: LiveData<Event<String>> = _error

    private val _showDialogAction = MutableLiveData<Event<FavoriteResponse>>()
    val showDialogAction: LiveData<Event<FavoriteResponse>> = _showDialogAction

}
