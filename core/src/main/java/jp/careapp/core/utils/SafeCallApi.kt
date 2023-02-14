package jp.careapp.core.utils

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object SafeCallApi {

    val gson = Gson()

    inline fun <reified T> fromJson(json: String): T? {
        return try {
            gson.fromJson(json, object : TypeToken<T>() {}.type)
        } catch (ex: Exception) {
            null
        }
    }

    inline fun <reified T : Any> callApi(
        context: Context? = null,
        crossinline call: () -> Call<T>
    ): MutableLiveData<Result<T>> {
        val resultLiveData = MutableLiveData<Result<T>>()
        var result: Result<T>
        CoroutineScope(Dispatchers.IO).launch {
            delay(200)
            call().enqueue(
                object : Callback<T> {
                    override fun onResponse(call: Call<T>, response: Response<T>) {
                        result = if (response.isSuccessful) Result.Success(response.body())
                        else {
                            val errorBody = response.errorBody()!!.string()
                            val converted = fromJson<T>(errorBody)
                            if (converted != null) {
                                Result.Success(converted)
                            } else {
                                Result.Error(ConvertError())
                            }
                        }
                        resultLiveData.postValue(result)
                    }

                    override fun onFailure(call: Call<T>, t: Throwable) {
                        resultLiveData.postValue(Result.Error(t))
                    }
                }
            )
        }
        return resultLiveData
    }
}

sealed class Result<T> {
    data class Success<T>(val data: T?) : Result<T>()

    data class Error<T>(val exception: Throwable) : Result<T>()
}

class ConvertError : Throwable() {
    override val message: String?
        get() = "Can not cast into passed class!!!"
}
