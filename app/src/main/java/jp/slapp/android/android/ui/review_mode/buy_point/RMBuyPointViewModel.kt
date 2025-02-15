package jp.slapp.android.android.ui.review_mode.buy_point

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.slapp.android.android.data.model.CreditItem
import jp.slapp.android.android.data.network.MemberResponse
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.handle.BillingManager
import jp.slapp.android.android.model.buy_point.ItemPoint
import jp.slapp.android.android.network.ApiInterface
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RMBuyPointViewModel @Inject constructor(
    billingManager: BillingManager,
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {

    private var mBillingManager: BillingManager = billingManager
    var statusBuyPoint = MutableLiveData<Boolean>()
    val memberInFoResult = MutableLiveData<MemberResponse?>()
    private val listCreditItem = MutableLiveData<List<CreditItem>>()
    var itemSelected: ItemPoint? = null

    init {
        billingManager.setHandleAction(
            object : BillingManager.HandleActionBilling {
                override fun statusBilling(status: Int) {
                    when (status) {
                        BillingManager.SUCCESS -> {
                            statusBuyPoint.postValue(true)
                        }
                        BillingManager.LOADING -> {
                            isLoading.postValue(true)
                        }
                        else -> {
                            isLoading.postValue(false)
                        }
                    }
                }
            }
        )
        connectBilling()
    }

    fun connectBilling() {
        isLoading.value = true
        mBillingManager.startConnectionBilling()
    }

    fun loadMemberInfo() {
        viewModelScope.launch {
            try {
                val response = apiInterface.getMember()
                response.let {
                    if (it.errors.isEmpty()) {
                        it.dataResponse.let { data ->
                            memberInFoResult.value = data
                            rxPreferences.saveMemberInfo(data)
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    fun startBilling(item: ItemPoint, activity: Activity) {
        this.itemSelected = item
        isLoading.value = true
        mBillingManager.startBilling(item, activity)
    }

    fun getCreditPrices(firstBuyCredit: Boolean) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                apiInterface.getCreditPrices(firstBuyCredit = firstBuyCredit).let {
                    isLoading.value = false
                    if (it.errors.isEmpty()) {
                        it.dataResponse.let { data ->
                            listCreditItem.value = data
                        }
                    } else {
                        messageError.value = it.errors.joinToString()
                    }
                }
            } catch (e: Exception) {
                isLoading.value = false
                messageError.value = e.message ?: ""
            }
        }
    }

    override fun onCleared() {
        mBillingManager.onDestroy()
        super.onCleared()
    }
}