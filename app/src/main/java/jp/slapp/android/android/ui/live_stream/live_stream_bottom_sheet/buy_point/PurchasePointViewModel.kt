package jp.slapp.android.android.ui.live_stream.live_stream_bottom_sheet.buy_point

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.slapp.android.android.data.model.CreditItem
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.network.ApiInterface
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
                Timber.tag(jp.slapp.android.android.data.shareData.TAG).d(e)
            }
        }
    }
}