package jp.careapp.counseling.android.ui.mypage

import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.TypeField

enum class NMMenuItem(
    val resourceImage: Int,
    val label: String,
    val typeField: TypeField,
    val isSpace: Boolean = false
) {
    BUY_POINTS(R.drawable.ic_cell_buy_points, "ポイント購入", TypeField.TOP),
    PROFILE(R.drawable.ic_cell_profile, "プロフィール", TypeField.CENTER),
    BLOCK_LIST(R.drawable.ic_cell_block, "ブロック一覧", TypeField.CENTER),
    SETTING_PUSH(R.drawable.ic_cell_notifications, "通知設定", TypeField.BOTTOM),
    SPACE(0, "", TypeField.NONE, true),
    USE_POINTS_GUIDE(R.drawable.ic_cell_document, "料金説明", TypeField.TOP),
    TERMS_OF_SERVICE(R.drawable.ic_cell_document, "利用規約", TypeField.CENTER),
    PRIVACY_POLICY(R.drawable.ic_cell_document, "プライバシーポリシー", TypeField.CENTER),
    FAQ(R.drawable.ic_cell_questions, "よくある質問", TypeField.CENTER),
    CONTACT_US(R.drawable.ic_cell_mail, "お問い合わせ", TypeField.BOTTOM)
}