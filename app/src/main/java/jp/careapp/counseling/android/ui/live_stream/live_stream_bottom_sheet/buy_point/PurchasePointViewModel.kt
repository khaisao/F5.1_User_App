package jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.buy_point

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.network.FssPurchasePointResponse
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.ui.calling.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PurchasePointViewModel @Inject constructor(
    private val apiInterface: ApiInterface
) : BaseViewModel() {

    private val _fssPurchasePoint = MutableLiveData<FssPurchasePointResponse>(null)
    val fssPurchasePoint: MutableLiveData<FssPurchasePointResponse> get() = _fssPurchasePoint

    fun getPurchasePointConfig(purchasePointUrl: String) {
        viewModelScope.launch(NonCancellable + Dispatchers.IO) {
            try {
                _fssPurchasePoint.value = apiInterface.fssPurchasePoint(purchasePointUrl)
            } catch (e: Exception) {
                Timber.tag(TAG).e(e)
            }
        }
    }
}