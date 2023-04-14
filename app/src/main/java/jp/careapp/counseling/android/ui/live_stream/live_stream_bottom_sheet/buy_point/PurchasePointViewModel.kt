package jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.buy_point

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.model.CreditItem
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PurchasePointViewModel @Inject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {

    private val _listPointItem = MutableLiveData<List<CreditItem>?>()
    val listPointItem: LiveData<List<CreditItem>?> get() = _listPointItem

    fun getCreditPrices() {
        viewModelScope.launch {
            try {
                apiInterface.getCreditPrices(firstBuyCredit = rxPreferences.getFirstBuyCredit())
                    .let {
                        if (it.errors.isEmpty()) {
                            _listPointItem.postValue(it.dataResponse)
                        }
                    }
            } catch (e: Exception) {
                Timber.tag(jp.careapp.counseling.android.data.shareData.TAG).d(e)
            }
        }
    }
}