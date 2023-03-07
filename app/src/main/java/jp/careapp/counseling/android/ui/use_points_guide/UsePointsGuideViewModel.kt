package jp.careapp.counseling.android.ui.use_points_guide

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.TypeField
import javax.inject.Inject

@HiltViewModel
class UsePointsGuideViewModel @Inject constructor() : BaseViewModel() {

    private val _usePointsGuideList = MutableLiveData<ArrayList<UsePointsModelRecyclerView>>()
    val usePointsGuideList: LiveData<ArrayList<UsePointsModelRecyclerView>>
        get() = _usePointsGuideList

    init {
        initUsePointsGuideList()
    }

    private fun initUsePointsGuideList() {
        val itemList = arrayListOf<UsePointsModelRecyclerView>()
        itemList.add(UsePointsModelRecyclerView(R.drawable.ic_price_message, "メッセージ", "100pts", "/通", TypeField.TOP))
        itemList.add(UsePointsModelRecyclerView(R.drawable.ic_price_live, "ライブ視聴", "100pts", "/分〜", TypeField.CENTER))
        itemList.add(UsePointsModelRecyclerView(R.drawable.ic_price_peeping, "のぞき", "100pts", "/分〜", TypeField.CENTER))
        itemList.add(UsePointsModelRecyclerView(R.drawable.ic_price_private, "プライベート", "250pts", "/分〜", TypeField.CENTER))
        itemList.add(UsePointsModelRecyclerView(R.drawable.ic_price_premium, "プレミアムプライベート", "350pts", "/分〜", TypeField.BOTTOM))
        _usePointsGuideList.value = itemList
    }
}