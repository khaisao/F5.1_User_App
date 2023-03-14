package jp.careapp.counseling.android.ui.my_page

import jp.careapp.counseling.android.data.model.NMTypeField

sealed class NMMenuItem {
    data class NMMenuItemField(
        val key: Int,
        val resourceImage: Int,
        val label: String,
        val typeField: NMTypeField
    ) : NMMenuItem()

    object NMMenuItemSpace : NMMenuItem()
}

object NMMenuItemKey {
    const val BUY_POINTS = 1
    const val EDIT_PROFILE = 2
    const val BLOCK_LIST = 3
    const val SETTING_PUSH = 4
    const val USE_POINTS_GUIDE = 5
    const val TERMS_OF_SERVICE = 6
    const val PRIVACY_POLICY = 7
    const val FAQ = 8
    const val CONTACT_US = 9
}