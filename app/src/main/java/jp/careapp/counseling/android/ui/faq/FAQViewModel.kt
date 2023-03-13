package jp.careapp.counseling.android.ui.faq

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class FAQViewModel @Inject constructor(private val mRepository: FAQRepository) : BaseViewModel() {

    private val _faqList = MutableLiveData<ArrayList<FAQModelRecyclerView>>()
    val faqList: LiveData<ArrayList<FAQModelRecyclerView>>
        get() = _faqList

    private val dataList = arrayListOf<FAQModelRecyclerView>()

    val mActionState = SingleLiveEvent<FAQActionState>()

    init {
        initFAQList()
    }

    private fun initFAQList() {
        dataList.add(FAQModelRecyclerView.ItemHeader("はじめに"))
        dataList.add(
            FAQModelRecyclerView.ItemContent(
                "はじめての方へ",
                "スパークライブはオンラインコミュニケーションサービスです。\nライブ配信やメッセージを楽しむことが出来ます。",
                NMTypeField.ONLY
            )
        )
        dataList.add(FAQModelRecyclerView.ItemHeader("使い方"))
        dataList.add(
            FAQModelRecyclerView.ItemContent(
                "料金について",
                "メッセージ　100pts／通\nライブ視聴　100pts／分〜\nのぞき　100pts／分〜\nプライベート　250pts／分〜\nプレミアムプライベート 350pts／分〜",
                NMTypeField.ONLY
            )
        )
        dataList.add(FAQModelRecyclerView.ItemHeader("トラブルシューティング"))
        dataList.add(
            FAQModelRecyclerView.ItemContent(
                "ログインできない", "認証コードが正しくない\n" +
                        "認証コードの有効期限は10分間です。期限を過ぎた場合は認証コードを再取得してください。\n" +
                        "認証コードが届かない\n" +
                        "メールアドレスが間違っていないか、迷惑メールフォルダに届いていないかご確認下さい。\n" +
                        "また、以下のドメインの受信許可をお願い致します。\n" +
                        "@mail.sparklive.jp\n" +
                        "アカウントが停止中と表示される\n" +
                        "何らかの理由でアカウントがご利用頂けない状態となります。\n" +
                        "お問い合わせフォームより事務局へご連絡下さい。", NMTypeField.TOP
            )
        )
        dataList.add(
            FAQModelRecyclerView.ItemContent(
                "退会したい",
                "サービスから退会するにはこちらの退会フォームから申請をお願いいたします。",
                NMTypeField.BOTTOM,
                12,
                15
            ) {
                mActionState.value = FAQActionState.NavigateToWithdrawal
            }
        )
        _faqList.value = dataList
    }

    fun handleOnClickItemContent(position: Int) {
        val item = dataList[position]
        if (item is FAQModelRecyclerView.ItemContent) {
            item.isCollapse = !item.isCollapse
        }
        _faqList.value = dataList
    }
}

sealed class FAQActionState {
    object NavigateToWithdrawal : FAQActionState()
}