package jp.careapp.counseling.android.ui.profile.list_user_profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.ui.profile.detail_user.DetailUserProfileFragment

class UserProfileAdapter(
    listFragment: List<Fragment>,
    fm: FragmentManager
) :
    FragmentPagerAdapter(fm) {

    private var fragments: List<Fragment> = listFragment

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}
