package jp.slapp.android.android.model.user_profile

import jp.careapp.core.base.BaseFragment

data class UserProfileTab(
    val title: String,
    val fragment: BaseFragment<*, *>
)
