package jp.slapp.android.android.ui.review_mode.top

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.databinding.FragmentRmTopBinding

@AndroidEntryPoint
class RMTopFragment : BaseFragment<FragmentRmTopBinding, RMTopViewModel>() {
    private val viewModel: RMTopViewModel by viewModels()

    override fun getVM() = viewModel

    override val layoutId = R.layout.fragment_rm_top

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.connectSocket()
    }

    override fun initView() {
        super.initView()

        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {
        val navController =
            Navigation.findNavController(activity as Activity, R.id.fragmentContainerView)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateTitle(destination.id)
        }
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun updateTitle(id: Int) {
        binding.tvTitle.text = when (id) {
            R.id.tabOnlineList -> getString(R.string.rv_title_online_list)
            R.id.tabFavoriteList -> getString(R.string.rv_title_favorite_list)
            R.id.tabMyMenu -> getString(R.string.rv_title_my_menu)
            else -> getString(R.string.rv_title_online_list)
        }
    }

    override fun onDestroy() {
        viewModel.closeSocket()
        super.onDestroy()
    }
}