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
                "スパクラ利用規約",
                "この利用規約は、スパクラ(以下「本アプリ」とする)の提供するサービスの利用に関し、全ての本サービス利用者(以下「ユーザー」とする)に対し、適用されます。"
                        + "\n\n"
                        + "ユーザーは、以下の条項を全て承諾の上、本アプリを利用するものとします。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "1. 本規約の範囲と変更",
                "1.1 本アプリは必要に応じて、法令に基づき、本アプリの目的の範囲内で、本規約を変更することがあります。その場合、弊社は、変更後の本規約の内容および効力発生日を、本アプリもしくは当社ウェブサイトに表示します。"
                        + "\n\n"
                        + "1.2 変更後の本規約は、効力発生日から効力を生じるものとします。"
                        + "\n\n"
                        + "1.3 本アプリが当社ウェブサイトを通じて随時発表する諸規定は、本規約の一部を構成するものとします。"

            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "2. 本アプリの内容",
                "2.1 本アプリは、アプリ内のみにおいてライブ配信やメッセージを通してのアドバイザーにアドバイスを受けるサービスです。実際にアドバイザーと会うことは禁止しています。"
                        + "\n\n"
                        + "2.2 アドバイザーに対しては、本アプリ利用前に公的身分証明書を提出してもらい、満18歳以上であることを確認済みです。"
                        + "\n\n"
                        + "2.3 アドバイザーとは、会員様へのサービスを提供する会員のことを指します。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "3. 利用資格および承認",
                "3.1 ユーザーは本規約の定めに沿って本アプリを利用するものとします。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "4. アカウント",
                "4.1 ユーザーは本アプリで得た自己のIDおよびパスワードの管理・使用について一切の責任を負い、その使用に係る一切の債務を支払うものとします。"
                        + "\n\n"
                        + "4.2 IDまたはパスワードの第三者の使用、ハッキング行為により情報が漏洩した際、当該ユーザーが損害を被った場合にもその帰責事由の有無に関わらず、弊社は一切責任を負いません。"
                        + "\n\n"
                        + "4.3 アカウントは不正に利用されないようご自身の責任で厳重に管理しなければなりません。弊社はユーザーアカウントで操作が行われた場合、お客様ご本人の行為として本サービスを提供します。"
                        + "\n\n"
                        + "4.4 アカウントは登録をした本人のみが使用できるものとし、いかなる形でも、他人に対して、譲渡、贈与、貸与、その他の方式により、当該アカウントの使用を許可してはなりません。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "5. 退会手続き",
                "5.1 ユーザーが退会を希望する場合は、本アプリまでその旨を届け出るものとします。"
                        + "\n\n"
                        + "5.2 退会手続き後のキャンセルはできません。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "6. サービスに対する保証と責任",
                "6.1 弊社は、本サービスが円滑に提供できるよう十分な注意を払うものとしますが、コンピュータウィルス、ハッキング行為、機器の故障、回線の異常、電力供給の不安定、天災、事変、火災等の偶発事故、その他不可抗力により、利用者が本サービスを利用することができなかった場合は、弊社は一切その責任を負いません。"
                        + "\n\n"
                        + "6.2 ユーザーが本アプリを利用される際は、内容の信頼性、正確性、完成度、有益性等についてご自身で判断され、利用者自身の責任とリスク負担のもとで行うことに同意するものと致します。弊社は配信者が提供する配信内容については一切の責任を負いません。"
                        + "\n\n"
                        + "6.3 本アプリを通じて提供する情報、商品、懸賞、景品等の品質、正確性等を保証するものではありません。"
                        + "\n\n"
                        + "6.4 本アプリのサービスの提供が休止された場合、本アプリは迅速な対応をもってその復旧に努めますが、復旧時間についてはその保証を一切行いません。"
                        + "\n\n"
                        + "6.5 本アプリは、ユーザー毎にサービスの提供範囲の制限を設定し、または変更することがあります。"
                        + "\n\n"
                        + "6.6 サービスの保守管理・セキュリティに関して最大限努力は致しますが、不可抗力によるウィルス感染やハッキングによってユーザーに生じた被害、損害に関して、本アプリは一切その責任を負いません。"
                        + "\n\n"
                        + "6.7 ユーザーが本アプリを経由して他ネットにアクセスした場合であっても、当該利用者と他ネットの主催者、当該他ネットの利用者、またはその他の第三者との間に発生したトラブルについて本アプリは責任を負わないものとし、これに起因して本アプリまたは第三者が損害を被った場合、利用者はその損害を賠償するものとします。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "7. 提供サービスの変更、中断または終了",
                "7.1 弊社は会員に予告なく、本アプリの改名、ドメイン名の変更や追加を行うことができるものとします。"
                        + "\n\n"
                        + "7.2 弊社は自由な裁量によって、本アプリの修正、変更、中断、中止、終了することができるものとします。"
                        + "\n\n"
                        + "7.3 弊社は経営上の目的を達成するため、会員が投稿したコンテンツの全部または一部を削除、変更する権利を有します。"
                        + "\n\n"
                        + "7.4 弊社は予測不可能なサーバーダウン、災害などによって不具合が生じた際、事前に予告することなく、サービスをシャットダウンすることができます。"
                        + "\n\n"
                        + "7.5 弊社は不具合によるサーバーダウン、サーバメンテナンスやサービスの更新によって滅失、破損したことによる損害において一切の責任を負いません。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "8. サービスの転用の禁止",
                "8.1 ユーザーは本アプリの全部または一部もしくは本アプリにかかるデ-タをいかなる方法であれ、他に転用し第三者に提供もしくは使用させることまたは自己もしくは第三者の営業のために利用することはできません。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "9. 著作権",
                "9.1 本アプリのユーザーは、いかなる方法においても、本アプリを通じて提供されるいかなる情報も、著作権法で定めるユーザー個人の私的使用の範囲外で使用することはできません。"
                        + "\n\n"
                        + "9.2 本条の規定に違反して問題が発生した場合、本アプリ並びに弊社は、自己の費用と責任においてかかる問題を解決するとともに、弊社に何等の迷惑または損害を与えないものとします。"

            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "10. 情報の取扱い",
                "10.1 本アプリよりユーザーに提供される情報や広告、各種コンテンツの著作権は、全て弊社に帰属するものとします。"
                        + "\n\n"
                        + "10.2 ユーザーが本アプリに登録した情報は、弊社に帰属します。"
                        + "\n\n"
                        + "10.3 弊社は、本アプリの実施に関連して知り得たユーザーの個人情報を利用者の事前の承認なしには、第三者に提供しないものとします。但し、ユーザーに関するアプリにおける情報(ID、ハンドルネーム)は本項でいうユーザーの個人情報とは扱わないものとし、本アプリ運営の必要に応じて本サービス関係者に確認できる場合があるものとします。"
                        + "\n\n"
                        + "10.4 前項の規定に関わらず、次の各号に定める限度において、本アプリはユーザーに関する情報、および弊社が全部または一部のユーザーに対し弊社が自らまたは第三者からの委託を受けて実施する調査等により得た情報を第三者に提供することができるものとします。"
                        + "\n\n" +
                        "·登録情報を集計・分析して得られたユーザーの個人情報を特定できない二次的データを第三者に提供する場合。"
                        + "\n\n" +
                        "·ユーザーへの書類、商品の送付等、本サービス実施に必要な行為を行う場合。"
                        + "\n\n" +
                        "·その他、ユーザーが任意で同意した個別の情報を利用する場合。"
                        + "\n\n" +
                        "·他の条項・付記により別途定められた行為を行なう場合。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "11. 禁止事項",
                "11.1 ユーザー、第三者もしくは弊社の著作権、営業秘密、パブリシティーその他の権利を侵害する行為、また侵害する恐れのある行為。\n" +
                        "\n" +
                        "11.2 ユーザー、第三者もしくは弊社の財産もしくはプライバシー、人権、名誉を侵害する行為、または侵害する恐れのある行為。\n" +
                        "\n" +
                        "11.3 ユーザー、第三者もしくは弊社に不利益もしくは損害を与える行為、またはそれらの恐れのある行為。\n" +
                        "\n" +
                        "11.4 他アプリや外部サービスへ誘導する行為。\n" +
                        "\n" +
                        "11.5 個人の連絡先を伝達、交換する行為。\n" +
                        "\n" +
                        "11.6 アドバイザーと直接会う事を目的として利用する行為。\n" +
                        "\n" +
                        "11.7 本アプリの運営を妨げる行為。\n" +
                        "\n" +
                        "11.8 本アプリの信用を貶める行為。\n" +
                        "\n" +
                        "11.9 IDおよびパスワード、その他の認証情報を不正に使用する行為。\n" +
                        "\n" +
                        "11.10公序良俗に反する行為もしくはその恐れのある行為。\n" +
                        "\n" +
                        "11.11 犯罪行為もしくは犯罪行為に結びつく行為、またはその恐れのある行為。\n" +
                        "\n" +
                        "11.12 その他、法令に違反する、または違反する恐れのある行為。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "12. 不適当な登録情報の削除",
                "12.1 弊社が、本アプリに登録された文章などの内容が前条に該当もしくは該当する恐れがあると判断した場合、弊社はユーザーに通知することなく、該当する内容または情報を削除することができるものとします。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "13. アカウントの使用停止、削除",
                "13.1 ユーザーが次のいずれかの事由に該当すると弊社が認めた場合、本アプリは当該ユーザーに対し事前の催告・通知なく、アカウントの使用を停止または削除することができます。\n" +
                        "\n" +
                        "·弊社への申告、届け出内容に虚偽があった場合。\n" +
                        "\n" +
                        "·同一人物が重複してユーザー登録をした場合。\n" +
                        "\n" +
                        "·メールアドレス登録時の連絡先を解約または変更等により送信不能となった場合。\n" +
                        "\n" +
                        "·IDまたはパスワード、その他の認証情報を不正に使用した場合。\n" +
                        "\n" +
                        "·本アプリの配信者に対し弊社が不適切と判断した行為があった場合。\n" +
                        "\n" +
                        "·本アプリを使用して違法な行為を行った場合。\n" +
                        "\n" +
                        "·本規約のいずれかに違反した場合。\n" +
                        "\n" +
                        "·その他、弊社がユーザーとして不適当、もしくはユーザー利用の継続が困難であると判断した場合。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "14. 免責事項",
                "14.1 弊社は、本アプリの利用により発生したユーザーの損害全てに対し、いかなる責任も負わないものとし、当該損害の賠償をする義務もないものとします。\n" +
                        "\n" +
                        "14.2 ユーザーが本アプリの利用によって第三者に対して損害を与えた場合、ユーザーは自己の責任と費用をもって解決し、弊社に損害を与えることのないものとします。\n" +
                        "\n" +
                        "14.3 ユーザーが本規約に反した行為、または不正もしくは違法な行為によって弊社に損害を与えた場合、弊社は関係機関と協議の上、当該ユーザーに対して相応の損害賠償の請求を行なうことができるものとします。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "15. 義務",
                "15.1 本アプリの信頼性・イメージを損なう投稿等、運営維持の妨げとなる行為を発見した場合は、書面をもって裁判所等を通じ損害賠償請求を行う場合があります。また公序良俗法に反し、警察・裁判所等にデータ開示を求められた場合、速やかに応じます。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "16. 準拠法および管轄",
                "16.1 弊社と利用者との間の法律問題については、日本国法が適用されるものとします。\n" +
                        "\n" +
                        "16.2 本契約書または本契約書に関係して発生した紛争やクレーム、契約違反等は、大阪地方裁判所を第一審の専属管轄裁判所とするものとします。"
            )
        )
        data.add(
            RMPrivacyPolicyModelRecyclerView(
                "17. 本規約の有効期間",
                "17.1 本規約は、弊社が本サービスを行う期間、常に有効とします。\n" +
                        "\n" +
                        "2023年08月31日制定・施行"
            )
        )

        _dataLiveData.value = data
    }
}