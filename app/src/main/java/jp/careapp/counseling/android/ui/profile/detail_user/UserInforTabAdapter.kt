package jp.careapp.counseling.android.ui.profile.detail_user

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.android.model.user_profile.UserProfileTab

class UserInforTabAdapter(
    fm: FragmentManager?,
    tabFragments: List<UserProfileTab>
) :
    FragmentPagerAdapter(fm!!) {
    private var tabFragments: List<UserProfileTab>

    init {
        this.tabFragments = tabFragments
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabFragments[position].title
    }

    override fun getItem(position: Int): BaseFragment<*, *> {
        return tabFragments.get(position).fragment
    }

    override fun getCount(): Int {
        if (tabFragments.isNullOrEmpty()) {
            return 0
        } else {
            return tabFragments.size
        }
    }
}
