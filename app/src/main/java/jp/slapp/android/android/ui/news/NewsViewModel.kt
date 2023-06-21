package jp.slapp.android.android.ui.news

import androidx.lifecycle.* // ktlint-disable no-wildcard-imports
import jp.careapp.core.base.BaseViewModel
import jp.slapp.android.android.data.network.ApiObjectResponse
import jp.slapp.android.android.data.network.NewsResponse
import jp.slapp.android.android.network.ApiInterface
import jp.slapp.android.android.utils.event.Event
import jp.slapp.android.android.utils.result.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val apiService: ApiInterface
) : BaseViewModel(), NewsEvent {

    private val _getNotices: LiveData<Result<ApiObjectResponse<List<NewsResponse>>>> =
        liveData {
            emit(Result.Loading)
            emit(getNotices())
        }
    val uiNotices: LiveData<List<NewsResponse>> = _getNotices.map {
        (it as? Result.Success)?.data?.dataResponse ?: listOf()
    }
    val loadingNotices: LiveData<Boolean> = _getNotices.map {
        it == Result.Loading
    }

    private suspend fun getNotices(): Result<ApiObjectResponse<List<NewsResponse>>> {
        return try {
            withContext(Dispatchers.IO) {
                apiService.getNotices().let {
                    val typeResponse =
                        object : TypeToken<ApiObjectResponse<List<NewsResponse>>>() {}.type
                    var response: ApiObjectResponse<List<NewsResponse>>? = null
                    try {
                        response =
                            Gson().fromJson(it.toString(), typeResponse) as ApiObjectResponse<List<NewsResponse>>?
                        Result.Success(response)
                    } catch (e: java.lang.Exception) {
                        val typeResponse =
                            object : TypeToken<ApiObjectResponse<NewsResponse>>() {}.type
                        val data =
                            Gson().fromJson(it, typeResponse) as ApiObjectResponse<NewsResponse>
                        var response: ApiObjectResponse<List<NewsResponse>>? =
                            ApiObjectResponse(data.errors, listOf(), data.pagination)
                        Result.Success(response)
                    }
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private val _refreshNotice = MutableLiveData<Unit>()
    fun forceRefresh() {
        _refreshNotice.value = Unit
    }

    private val _updateLastTimeNotice: LiveData<Result<ApiObjectResponse<Any>>> =
        _refreshNotice.switchMap {
            liveData {
                emit(Result.Loading)
                emit(
                    updateLastTimeNotice(
                        LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString()
                    )
                )
            }
        }

    private suspend fun updateLastTimeNotice(time: String): Result<ApiObjectResponse<Any>> {
        return try {
            withContext(Dispatchers.IO) {
                apiService.updateNoticeLast(time).let {
                    Result.Success(it)
                }
            }
        } catch (e: java.lang.Exception) {
            Result.Error(e)
        }
    }

    private val _error = MediatorLiveData<Event<String>>().apply {
        addSource(_getNotices) { rs ->
            if (rs is Result.Error)
                value = Event(content = rs.throwable.message ?: "")
        }
        addSource(_updateLastTimeNotice) { rs ->
            if (rs is Result.Error)
                value = Event(content = rs.throwable.message ?: "")
        }
    }
    val error: LiveData<Event<String>> = _error

    override fun newsClick(item: NewsResponse) {
        // TODO
    }
}
