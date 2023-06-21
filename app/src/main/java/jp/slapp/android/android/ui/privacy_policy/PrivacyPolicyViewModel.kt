package jp.slapp.android.android.ui.privacy_policy

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
        data.addAll(
            listOf(
                PrivacyPolicyModelRecyclerView(
                    "プライバシーポリシーについて",
                    "SPARK LIVE（以下「本アプリ」といいます）では、登録会員（以下「ユーザー」といいます。）の皆様に安心してご利用いただくため、プライバシー及び個人情報（以下、個人情報）に関して細心の注意を払い厳重に管理いたします。本アプリのプライバシーポリシーについては以下の通りです。"
                ),
                PrivacyPolicyModelRecyclerView(
                    "1．個人情報の収集について",
                    "本アプリでは、ユーザーにコンテンツをご利用いただくために、お名前、メールアドレス等の個人情報をお尋ねいたします。それらの個人情報は会員様個人にサービスを提供するために必要な情報ですので、その旨をご理解いただいたうえで、会員様の個人情報をご登録頂いております。"
                ),
                PrivacyPolicyModelRecyclerView(
                    "2．個人情報の使用目的について",
                    "本アプリが取得する個人情報は、以下の目的のために使います。\n" +
                            "   ・ サービスのご提供およびご案内\n" +
                            "   ・ サービスのご利用料金のご請求\n" +
                            "   ・ 規約違反行為に対する対応\n" +
                            "   ・ サービスの開園、調査分析、マーケティング・お問い合わせの返信等ご本人へのご連絡\n" +
                            "   ・ 上記の利用目的に付随する目的のための利用"
                ),
                PrivacyPolicyModelRecyclerView(
                    "3．Cookie（クッキー）について",
                    "本アプリではユーザーが本アプリのサービスを快適に利用するために、『Cookie（クッキー）』を使用して必要な情報を受け取り保管しております。 ログイン認証や報酬の管理等を行うためにCookieを使用いたします。 配信者のプライバシーを侵害するものではありません。 また配信者のデバイスに悪影響を及ぼすこともありません。"
                ),
                PrivacyPolicyModelRecyclerView(
                    "4．情報の開示・提供について",
                    "本アプリにおける個人情報の利用は、サービスの目的の範囲内に応じて行います。また、個人情報の利用は適法かつ公正な手段によって行います。\n" +
                            "本アプリはユーザーの同意なく、個人情報を下記の場合を除き、第三者へ開示または提供することは一切ありません。\n" +
                            "   ・ 業務委託先への委託\n" +
                            "   ・ 本アプリの協力会社・提携先への委託・提供\n" +
                            "   ・ 警察など官公署からの要請\n" +
                            "   ・ 法令等に基づく提供"
                ),
                PrivacyPolicyModelRecyclerView(
                    "5．免疫",
                    "以下の場合は、本アプリは何ら責任を負いません。\n" +
                            "   ・ ユーザーが本アプリの機能または別の手段を用いて第三者に個人情報を明らかにした場合。\n" +
                            "   ・ ユーザーが本アプリ上にて開示した情報等により個人を識別できてしまった場合。"
                ),
                PrivacyPolicyModelRecyclerView(
                    "6．本プライバシーポリシーの変更",
                    "本プライバシーポリシーの内容は変更されることがあります。変更後のプライバシーポリシーについては、本アプリが別途定める場合を除いて、本アプリに掲載した時から効力を生じるものとします。本ページを都度ご確認の上、本アプリのプライバシーポリシーをご理解いただくようお願い致します。\n" +
                            "\n" +
                            "制定日: 2023年5月1日"
                )
            )
        )
        _dataLiveData.value = data
    }
}