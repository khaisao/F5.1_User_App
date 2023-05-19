package jp.careapp.counseling.android.network

import android.os.Build
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import jp.careapp.counseling.android.data.network.ApiObjectResponse
import jp.careapp.counseling.android.data.network.LoginResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.utils.event.NetworkEvent
import jp.careapp.counseling.android.utils.event.NetworkState
import jp.careapp.counseling.android.utils.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val rxPreferences: RxPreferences,
    private val gson: Gson,
    private val networkEvent: NetworkEvent,
) : Authenticator {

    private val lock = Object()

    @ExperimentalCoroutinesApi
    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(lock) {
            val refreshResult =
                refreshToken("${jp.careapp.counseling.BuildConfig.BASE_URL}api/login")
            return if (refreshResult)
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${rxPreferences.getToken().toString()}")
                    .build()
            else {
                networkEvent.publish(NetworkState.UNAUTHORIZED)
                null
            }
        }
    }

    @Throws(IOException::class)
    fun refreshToken(url: String): Boolean {
        Timber.d("Refresh token")
        val refreshUrl = URL(url)
        val urlConnection = refreshUrl.openConnection() as HttpURLConnection
        urlConnection.apply {
            doInput = true
            doOutput = true
            setRequestProperty("Accept", "application/json")
            requestMethod = "POST"
            useCaches = false
        }

        val urlParameters =
            "email=${rxPreferences.getEmail()}&password=${rxPreferences.getPassword()}&device_name=${Build.MODEL}"

        DataOutputStream(urlConnection.outputStream).apply {
            writeBytes(urlParameters)
            flush()
            close()
        }
        val responseCode = urlConnection.responseCode
        if (responseCode == 200) {
            val input = BufferedReader(InputStreamReader(urlConnection.inputStream))
            val response = StringBuffer()
            while (true) {
                val inputLine: String = input.readLine() ?: break
                response.append(inputLine)
            }
            input.close()
            val typeResponse =
                object : TypeToken<ApiObjectResponse<LoginResponse>>() {}.type
            val refreshTokenResult: ApiObjectResponse<LoginResponse>?
            try {
                refreshTokenResult =
                    Gson().fromJson(response.toString(), typeResponse) as ApiObjectResponse<LoginResponse>?
                Result.Success(response)
            } catch (e: java.lang.Exception) {
                return false
            }
            if (refreshTokenResult != null) {
                with(refreshTokenResult.dataResponse) {
                    rxPreferences.updateUserInformation(
                        token,
                        tokenExpire,
                        memberCode
                    )
                }
            }
            return true
        } else
            return false
    }
}
