package jp.careapp.counseling.android.network

import android.os.Build
import jp.careapp.counseling.android.data.network.ApiObjectResponse
import jp.careapp.counseling.android.data.network.LoginResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.utils.event.NetworkEvent
import jp.careapp.counseling.android.utils.event.NetworkState
import jp.careapp.counseling.android.utils.result.Result
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlin.jvm.Throws

class TokenAuthenticator @Inject constructor(
    private val rxPreferences: RxPreferences,
    private val gson: Gson,
    private val networkEvent: NetworkEvent
) : Authenticator {

    @ExperimentalCoroutinesApi
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshResult = refreshToken("${jp.careapp.counseling.BuildConfig.BASE_URL}api/login")
        if (refreshResult)
            return response.request.newBuilder()
                .header("Authorization", rxPreferences.getToken() ?: "")
                .build()
        else {
            networkEvent.publish(NetworkState.UNAUTHORIZED)
            return null
        }
    }

    @Throws(IOException::class)
    fun refreshToken(url: String): Boolean {
        val refreshUrl = URL(url)
        val urlConnection = refreshUrl.openConnection() as HttpURLConnection
        urlConnection.apply {
            doInput = true
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Content-Type", "application/json")
            requestMethod = "POST"
            useCaches = false
        }
        val postData = JsonObject().apply {
            addProperty("email", rxPreferences.getEmail())
            addProperty("password", rxPreferences.getPassword())
            addProperty("device_name", Build.MODEL)
        }

        urlConnection.doOutput = true
        DataOutputStream(urlConnection.outputStream).apply {
            writeBytes(postData.toString())
            flush()
            close()
        }
        val responseCode = urlConnection.responseCode
        if (responseCode == 200) {
            val input = BufferedReader(InputStreamReader(urlConnection.inputStream))
            val response = StringBuffer()
            while (true) {
                val inputLine: String? = input.readLine() ?: break
                response.append(inputLine)
            }
            input.close()
            val typeResponse =
                object : TypeToken<ApiObjectResponse<LoginResponse>>() {}.type
            var refreshTokenResult: ApiObjectResponse<LoginResponse>? = null
            try {
                refreshTokenResult =
                    Gson().fromJson(response.toString(), typeResponse) as ApiObjectResponse<LoginResponse>?
                Result.Success(response)
            } catch (e: java.lang.Exception) {
                return false
            }
            if (refreshTokenResult != null) {
                rxPreferences.saveUserInfor(
                    refreshTokenResult.dataResponse.token,
                    refreshTokenResult.dataResponse.tokenExpire,
                    rxPreferences.getPassword() ?: "",
                    refreshTokenResult.dataResponse.memberCode
                )
            }
            return true
        } else
            return false
    }
}
