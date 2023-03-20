package jp.careapp.counseling.android.ui.terms_of_service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.ui.privacy_policy.PrivacyPolicyModelRecyclerView
import javax.inject.Inject

@HiltViewModel
class TermsOfServiceViewModel @Inject constructor() : BaseViewModel() {

    private val _dataLiveData = MutableLiveData<ArrayList<PrivacyPolicyModelRecyclerView>>()
    val dataLiveData: LiveData<ArrayList<PrivacyPolicyModelRecyclerView>>
        get() = _dataLiveData

    init {
        getDataTermsOfService()
    }

    private fun getDataTermsOfService() {
        val data = arrayListOf<PrivacyPolicyModelRecyclerView>()
        data.add(
            PrivacyPolicyModelRecyclerView(
                "SPRAK LIVE 利用規約",
                "本規約は、SPARK LIVE（以下「弊社」とする）が提供するサービスであるアプリ「PEAK LIVE」（以下「本アプリ」とする）におけるビデオチャットシステムの提供や本アプリが配信・受信する各種コンテンツのインターネット通信サービス（以下「本サービス」という）の利用に関し、全ての本サービス利用者（以下「利用者」とする）に対し、適用されます。\n" +
                        "利用者は、以下の条項を全て承諾の上、本サービスを利用するものとします。\n" +
                        "なお、本サービスはサービスの性質上一時的に不適切コンテンツが表示される場合が含まれる為、18歳未満の方の閲覧、サービス利用を固く禁止します。\n" +
                        "本サービスには本規約の他、Apple.inc及びGoogle.incが別途定めるサービス規約等が適用され、利用者はこれらの規約に同意、遵守するものとします。"
            )
        )
        _dataLiveData.value = data
    }
}