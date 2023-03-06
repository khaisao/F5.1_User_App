package jp.careapp.counseling.android.ui.my_page.privacy_policy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class PrivacyPolicyViewModel @Inject constructor() : BaseViewModel() {

    private val _dataLiveData = MutableLiveData<ArrayList<PrivacyPolicyModelRecyclerView>>()
    val dataLiveData: LiveData<ArrayList<PrivacyPolicyModelRecyclerView>>
        get() = _dataLiveData

    init {
        getDataPrivacyPolicy()
    }

    private fun getDataPrivacyPolicy() {
        val data = arrayListOf<PrivacyPolicyModelRecyclerView>()
        data.add(
            PrivacyPolicyModelRecyclerView(
                "プライバシーポリシー（個人情報保護方針）",
                "当サービスは、アプリ利用者についての個人情報を以下の基準にて取り扱います。"
            )
        )
        data.add(
            PrivacyPolicyModelRecyclerView(
                "1．個人情報の収集について", "当サービスでは、アプリ利用者にサービスをご利用いただくために、メールアドレス等の個人情報をお尋ねいたします。\n" +
                        "それらの個人情報はアプリ利用者個人にサービスを提供するために必要な情報ですので、その旨をご理解いただいたうえで、アプリ利用者の個人情報をご登録いただいております。\n" +
                        "アプリ利用者から収集させていただいた個人情報は、不正アクセスや紛失、漏洩等が起きぬよう厳重に管理いたします。"
            )
        )
        _dataLiveData.value = data
    }
}