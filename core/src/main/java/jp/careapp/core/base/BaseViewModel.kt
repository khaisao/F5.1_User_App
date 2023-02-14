package jp.careapp.core.base

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.careapp.core.R
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.core.utils.dialog.CommonAlertDialog
import kotlinx.coroutines.cancel
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.HttpException

abstract class BaseViewModel : ViewModel() {

    var messageError = SingleLiveEvent<Any>()
    var isLoading = MutableLiveData(false)
    val _activity = MutableLiveData<Activity>()
    fun setActivityViewModel(activity: Activity) {
        _activity.value = activity
    }

    fun showError(throwable: Throwable, activity: Activity) {
        if (throwable is HttpException) {
            val httpException = throwable
            try {
                val errorBody = httpException.response()!!.errorBody()!!.string()
                val jsonResult = JSONObject(errorBody)
                val errors: JSONArray =
                    jsonResult.getJSONArray(activity.getString(R.string.errors))
                var errorMessage = StringBuilder()
                if (errors != null) {
                    for (i in 0 until errors.length()) {
                        errorMessage.append(errors.get(i).toString())
                        if (i < errors.length() - 1) {
                            errorMessage.append(",")
                        }
                    }
                }
                showAlertDialog(activity, errorMessage.toString())
            } catch (e: java.lang.Exception) {
                showAlertDialog(activity, e.message.toString())
            }
        } else {
            showAlertDialog(activity, throwable.message.toString())
        }
    }

    fun onHandleError(activity: Activity, errors: List<String>) {
        val errorMessage = StringBuilder()
        for (i in errors.indices) {
            errorMessage.append(errors[i])
            if (i < errors.size - 1) {
                errorMessage.append(",")
            }
        }
        showAlertDialog(activity, errorMessage.toString())
    }

    fun showAlertDialog(activity: Activity, errorMessage: String) {
        CommonAlertDialog.getInstanceCommonAlertdialog(activity)
            .showDialog()
            .setDialogTitleWithString(errorMessage)
            .setTextPositiveButton(R.string.text_OK)
            .setOnPositivePressed {
                it.dismiss()
            }
    }

    fun handleThowable(activity: Activity, throwable: Throwable, reloadData: () -> Unit) {
//        if (throwable is NetworkException) {
//            CommonAlertDialog.getInstanceCommonAlertdialog(activity)
//                .showDialog()
//                .setDialogTitle(R.string.no_internet)
//                .setTextPositiveButton(R.string.retry)
//                .setOnPositivePressed {
//                    it.dismiss()
//                    reloadData.invoke()
//                }
//                .setTextNegativeButton(R.string.cancel)
//                .setOnNegativePressed {
//                    it.dismiss()
//                }
//        }
    }

    fun showAlertDialog(message: String, onPositive: (() -> Unit)? = null) {
        _activity.value?.let { activity ->
            CommonAlertDialog.getInstanceCommonAlertdialog(activity)
                .showDialog()
                .setContent(message)
                .setTextPositiveButton(R.string.text_OK)
                .setOnPositivePressed {
                    onPositive?.invoke()
                    it.dismiss()
                }
        }
    }

    open fun clearAllApi() {
        viewModelScope.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        clearAllApi()
    }
}
