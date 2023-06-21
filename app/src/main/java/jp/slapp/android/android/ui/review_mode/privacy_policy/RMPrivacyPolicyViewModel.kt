package jp.slapp.android.android.ui.review_mode.privacy_policy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class RMPrivacyPolicyViewModel @Inject constructor() : BaseViewModel() {

    private val _dataLiveData = MutableLiveData<ArrayList<RMPrivacyPolicyModelRecyclerView>>()
    val dataLiveData: LiveData<ArrayList<RMPrivacyPolicyModelRecyclerView>>
        get() = _dataLiveData

    init {
        getDataPrivacyPolicy()
    }

    private fun getDataPrivacyPolicy() {
        val data = arrayListOf<RMPrivacyPolicyModelRecyclerView>()
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "1. プライバシーポリシー",
                "SL（以下「本アプリ」といいます）では、登録会員（以下「会員」といいます。）の皆様に安心してご利用いただくため、プライバシー及び個人情報（以下、個人情報）に関して細心の注意を払い厳重に管理いたします。当サイトのプライバシーポリシーについては以下の通りです。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "2. 個人情報の収集",
                "本アプリでは、会員に弊社提供のサービスをご利用いただくために、その使用目的をご確認の上、個人情報をご登録いただいております。収集した個人情報は、不正アクセスや紛失、破壊、及び漏洩等が起きないよう弊社にて厳重に管理いたします。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "3. 個人情報の利用目的",
                "本アプリでは、個人情報を下記の目的の範囲内で利用いたします。\n" +
                        "\n" +
                        "3.1. サービスの提供\n" +
                        "3.2. お知らせの通知\n" +
                        "3.3. サービスの案内\n" +
                        "3.4. お問い合わせの対応"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "4. Cookie（クッキー）について",
                "本アプリでは会員が本アプリのサービスを快適に利用するために、『Cookie（クッキー）』を使用して必要な情報を受け取り保管しております。 ログイン認証等の管理を行うためにCookieを使用いたします。 会員のプライバシーを侵害するものではありません。 また会員のデバイスに悪影響を及ぼすこともありません。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "5. 個人情報の開示・提供",
                "本アプリにおける個人情報の利用は、サービスの目的の範囲内に応じて行います。また、個人情報の利用は適法かつ公正な手段によって行います。\n" +
                        "本アプリで収集された個人情報は下記のいずれかに該当する場合を除き、第三者に提供することはありません。\n" +
                        "\n" +
                        "5.1. 会員ご本人から同意を得た場合\n" +
                        "\n" +
                        "5.2. 人の生命、財産、安全を保護するため、情報の開示が必要であると認められた場合\n" +
                        "\n" +
                        "5.3. 当事務局と機密保持契約を締結している組織への委託\n" +
                        "\n" +
                        "5.4. 警察など官公署からの要請\n" +
                        "\n" +
                        "5.5. 法律の適用を受ける場合"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "6. 第三者への委託",
                "個人情報の処理を外部に委託する場合は、適正な委託先を選定するとともに個人情報保護に関する契約を委託先と取り交わし、委託先に対して必要かつ適切な監督を行います。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "7. 規約と各種規定及びその改訂",
                "会員が弊社サービスをご利用される場合、ご利用及びプライバシーに関する様々な紛争については本文書及び会員規約が適用されます。 今後、本アプリは本文書や会員規約の内容を改訂する場合がありますが、重要な変更がある場合には適当であると判断出来得る方法にてお知らせいたします。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "",
                "制定日: 2023年2月16日\n" +
                        "最終改定日：2023年3月7日"
            )
        )
        _dataLiveData.value = data
    }
}