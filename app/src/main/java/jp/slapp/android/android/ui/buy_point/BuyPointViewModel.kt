package jp.slapp.android.android.ui.buy_point

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
class BuyPointViewModel @Inject constructor(
    billingManager: BillingManager,
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {

    private var billingManager: BillingManager
    var statusBuyPoint = MutableLiveData<Boolean>()
    val memberInFoResult = MutableLiveData<MemberResponse?>()
    val listCreditItem = MutableLiveData<List<CreditItem>>()
    var itemSelected: ItemPoint? = null

    init {
        this.billingManager = billingManager
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
        billingManager.startConnectionBilling()
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
        billingManager.startBilling(item, activity)
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
        billingManager.onDestroy()
        super.onCleared()
    }
}
