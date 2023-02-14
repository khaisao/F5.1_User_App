package jp.careapp.counseling.android.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BannerPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    private val types: ArrayList<Int> = ArrayList()
    private val numberOfPage = Int.MAX_VALUE

    fun setListBannerType(types: ArrayList<Int>) {
        this.types.apply {
            clear()
            addAll(types)
        }
    }

    private val numberOfTypeFragment: Int
        get() = types.size

    private val isMultiplePage: Boolean
        get() = types.size > 1

    val centerIndex: Int
        get() = if (isMultiplePage) numberOfPage / 2 else 0

    override fun getItemCount(): Int =
        if (isMultiplePage) numberOfPage else 1

    override fun createFragment(position: Int): Fragment =
        if (isMultiplePage) {
            BannerFragment.newInstance(types[position.rem(numberOfTypeFragment)])
        } else BannerFragment.newInstance(BannerFragment.BANNER_TYPE_REUNION)
}
