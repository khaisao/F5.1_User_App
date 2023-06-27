package jp.slapp.android.android.ui.review_mode.terms_of_service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.slapp.android.android.ui.review_mode.privacy_policy.RMPrivacyPolicyModelRecyclerView
import javax.inject.Inject

@HiltViewModel
class RMTermsOfServiceViewModel @Inject constructor() : BaseViewModel() {

    private val _dataLiveData = MutableLiveData<ArrayList<RMPrivacyPolicyModelRecyclerView>>()
    val dataLiveData: LiveData<ArrayList<RMPrivacyPolicyModelRecyclerView>>
        get() = _dataLiveData

    init {
        getDataTermsOfService()
    }

    private fun getDataTermsOfService() {
        val data = arrayListOf<RMPrivacyPolicyModelRecyclerView>()
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "1. 規約への同意",
                "本利用規約は登録会員（以下「会員」といいます。）が弊社の運営するSL（以下「本アプリ」といいます）を利用するにあたって、本アプリ利用に関わる全ての行為に適用するものとします。本規約に同意いただけない場合、本アプリをご利用いただくことはできません。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "2. 規約の変更",
                "弊社は法令に基づいた上で必要に応じて本規約を変更することがあります。その場合は、変更後の内容と発生日を本アプリもしくは弊社のウェブサイトに表示し、会員に通知することで周知します。変更後の規約は効力発生日から効力が生じるものとします。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "3. アカウント",
                "3.1. 本アプリの利用のために用いるアカウントは、会員が本アプリを利用する際の認証に用いられます。\n" +
                        "\n" +
                        "3.2. 会員は利用に際して情報を登録する場合、真実に基づいて正確な情報を登録しなければなりません。\n" +
                        "\n" +
                        "3.3. 弊社は会員のアカウントで操作が行われた場合、ご本人の操作として本アプリのサービスを提供します。\n" +
                        "\n" +
                        "3.4. 本アプリに登録した会員は、いつでもアプリ上でアカウントを削除し、退会することができます。\n" +
                        "\n" +
                        "3.5. 会員がアカウントを削除した場合は、アカウントの復旧はできず、本アプリの利用権や利用履歴は消滅します。\n" +
                        "\n" +
                        "3.6. 会員の意思によるアカウントの削除によって会員が被った損失については、弊社は責任を追わないこととします。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "4. 利用条件",
                "本アプリの利用対象者は、以下の条件を満たしている方とします。\n" +
                        "\n" +
                        "4.1. 年齢が満18歳以上の方\n" +
                        "\n" +
                        "4.2. 日本国籍を保有、または日本国における永住権を保有している方"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "5. プライバシー",
                "弊社は会員のプライバシーを最大限に尊重し、収集した情報を安全に管理します。\n" +
                        "個人情報の取り扱いに関しては、弊社が定めるプライバシーポリシーに従います。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "6. 会員への提供サービス",
                "本アプリは、法令及び本規約に準じて会員に対して以下のサービスを提供します。\n" +
                        "\n" +
                        "6.1. ユーザー間でのチャットによるメッセージの送受信ができます。\n" +
                        "\n" +
                        "6.2. 提供サービスはアプリのバージョンアップに伴って追加、または削除されることがあります。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "7. 禁止事項",
                "本アプリを利用する上で、以下の行為を固く禁止します。禁止行為を行なった場合、アカウントの停止を行うことがあります。\n" +
                        "\n" +
                        "7.1. 法律、法令、公序良俗に反する行為\n" +
                        "\n" +
                        "7.2 登録にあたっての虚偽の情報の申請\n" +
                        "\n" +
                        "7.3 自身または第三者の個人情報の漏洩\n" +
                        "\n" +
                        "7.4. 他のユーザーへの個人情報の要求\n" +
                        "\n" +
                        "7.5. アプリの利用上知り得た弊社及び他のユーザーに関する情報の漏洩\n" +
                        "\n" +
                        "7.6. 人に対する侮辱、嫌がらせや虐待を含む言動\n" +
                        "\n" +
                        "7.7. 宗教活動または宗教団体への勧誘\n" +
                        "\n" +
                        "7.8. 政治活動または政治団体への勧誘\n" +
                        "\n" +
                        "7.9. 反社会勢力に対する協力行為\n" +
                        "\n" +
                        "7.10. 第三者になりすました配信や投稿\n" +
                        "\n" +
                        "7.11. 本アプリが提供するサービス以外の営利活動\n" +
                        "\n" +
                        "7.12. アカウントの貸与、譲渡、売買\n" +
                        "\n" +
                        "7.13. 本アプリのサーバやネットワークシステムに支障を与える行為\n" +
                        "\n" +
                        "7.1から7.13までに定めるものに含め、弊社が不適当と合理的に判断した行為"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "8. 権利帰属",
                "8.1. 本アプリの知的財産権その他権利は、弊社に帰属します。\n" +
                        "\n" +
                        "8.2. 本コンテンツの一部の知的財産権その他権利は、弊社と提携する事業者の権利者に帰属します。\n" +
                        "\n" +
                        "8.3. 会員は本アプリにて配信、投稿したデータについての適法な権利を有していること、及びそのデータが第三者の権利を侵害していないことについて、保証するものとします。\n" +
                        "\n" +
                        "8.4. 会員は本アプリを通じて提供された情報を個人の私的使用の範囲外で使用することができません。\n" +
                        "\n" +
                        "8.5. 会員が本アプリを通じて提供するサービス（テキスト）の著作権は弊社に帰属し、使用権を弊社に付与されます。\n" +
                        "\n" +
                        "8.6. 弊社は本アプリで提供するサービス（テキスト）を必要に応じて活用することができるものとします。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "9. 規約違反の処理",
                "9.1. 会員が本規約または法令に違反した場合、弊社は会員の利用資格の停止、違反したコンテンツの削除、アカウントの削除、機能の制限を行うことができます。\n" +
                        "\n" +
                        "9.2. 規約違反によって実行した措置の全部または一部の利用を回復させるかは弊社が決定できるものとします。\n" +
                        "\n" +
                        "9.3. 規約違反によって削除されたコンテンツの復元は行いません。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "10. サービスの変更・中断",
                "10.1. 弊社は会員に予告なく、本アプリの改名、ドメイン名の変更や追加を行うことができるものとします。\n" +
                        "\n" +
                        "10.2. 弊社は自由な裁量によって、アプリの修正、変更、中断、中止、終了することができるものとします。\n" +
                        "\n" +
                        "10.3. 弊社は経営上の目的を達成するため、会員が投稿したコンテンツの全部または一部を削除、変更する権利を有します。\n" +
                        "\n" +
                        "10.4. 弊社は予測不可能なサーバーダウン、災害などによって不具合が生じた際、事前に予告することなく、サービスをシャットダウンすることができます。\n" +
                        "\n" +
                        "10.5. 弊社は不具合によるサーバーダウン、サーバメンテナンスやサービスの更新によって滅失、破損したことによる損害において一切の責任を負いません。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "11. 会員と利用者間の紛争",
                "会員のコンテンツ（テキスト等）によって利用者と紛争があった場合、会員と利用者で解決を図るものとし、それによって生じた損害・問題について、 弊社は一切の責任を負わないものとします。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "12. サービスの提供者およびサービスの改正",
                "12.1. 弊社は本アプリの運営の必要性に応じて本アプリの指定する第三者に対し、サービスの全部あるいは一部を承継し、継続して運営させることができるものとします。\n" +
                        "\n" +
                        "12.2. 弊社はいつでも本アプリのサービスの内容を改正することができるものとします。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "13. 免責事項",
                "13.1. 弊社は本アプリの利用により発生した会員の損害全てに対し、いかなる責任も負わないものとし、当該損害の賠償をする義務はないものとします。\n" +
                        "\n" +
                        "13.2. 会員が本サービスの利用によって第三者に対して損害を与えた場合、会員は自己の責任と費用をもって解決し、弊社に損害を与えることのないものとします。\n" +
                        "\n" +
                        "13.3. 会員が本規約に反した行為、または不正もしくは違法な行為によって弊社に損害を与えた場合、弊社は関係機関と協議の上、当該会員に対して相応の損害賠償の請求を行なうことができるものとします。"
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