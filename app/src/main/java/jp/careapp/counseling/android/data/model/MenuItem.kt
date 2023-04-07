package jp.careapp.counseling.android.data.model

import jp.careapp.counseling.android.data.model.TypeField.*

enum class MenuItem(val label: String, val typeField: TypeField, val isTitle: Boolean = false) {
    SETTING("各種設定", NONE, true),
    CHANGE_NICKNAME("ニックネーム変更", TOP),
    PROFILE_MSG("プロフィールメッセージ", CENTER),
    PUSH_NOTIFICATION("プッシュ通知", CENTER),
    MICROPHONE_CAMERA_SETTINGS("マイク・カメラ設定", BOTTOM),
    ACCOUNT("アカウント", NONE, true),
    BLOCK_LIST("ブロックリスト", TOP),
    WITHDRAWAL("退会", CENTER),
    DELETE_ACCOUNT("アカウントの削除", BOTTOM),
    SERVICE("サービス", NONE, true),
    TERMS_OF_SERVICE("利用規約", TOP),
    PRIVACY_POLICY("プライバシーポリシー", BOTTOM)
}

enum class TypeField {
    NONE,
    TOP,
    CENTER,
    BOTTOM
}