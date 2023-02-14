package jp.careapp.counseling.android.ui.labo

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.paging.LoadState
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.core.utils.loadImageAndBlur
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.labo.LaboResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.labo.detail.LaboStateAdapter
import jp.careapp.counseling.android.ui.search_lab.SearchLabViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.LAB
import jp.careapp.counseling.android.utils.adapter.LoadingStateAdapter
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentLaboBinding
import javax.inject.Inject

@AndroidEntryPoint
class LaboFragment : BaseFragment<FragmentLaboBinding, LaboViewModel>(), LabEvent {
    @Inject
    lateinit var appNavigation: AppNavigation
    override val layoutId: Int = R.layout.fragment_labo
    private val viewModels: LaboViewModel by viewModels()
    private val searchLabViewModel: SearchLabViewModel by activityViewModels()
    override fun getVM(): LaboViewModel = viewModels

    @Inject
    lateinit var rxPreferences: RxPreferences
    private lateinit var labAdapter: LaboAdapter
    private lateinit var loadingStateAdapter: LoadingStateAdapter
    private val shareViewModel: ShareViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showDialogFirstUseLabo(requireContext(), rxPreferences.isFirstTimeUseLab())
        labAdapter = LaboAdapter(this, requireContext(), rxPreferences)
        loadingStateAdapter = LoadingStateAdapter()
    }

    private var handleBackStack: Observer<Boolean> = Observer { result ->
        if (result) {
            appNavigation.navController?.currentBackStackEntry?.savedStateHandle?.remove<Boolean>(
                BUNDLE_KEY
                    .NEW_QUESTION_SUCCESS
            )
            showDialogSuccess()
            return@Observer
        }
    }

    override fun initView() {
        super.initView()
        handleBackStack()
        val title = listOf(
            resources.getString(R.string.labo_all),
            resources.getString(R.string.labo_resolved),
            resources.getString(R.string.labo_my_question)
        )

        binding.apply {
            binding.toolbar.setOnToolBarClickListener(object :
                ToolBarCommon.OnToolBarClickListener() {
                override fun onClickLeft() {
                    super.onClickLeft()
                    if (!isDoubleClick) {
                        appNavigation.navigateUp()
                    }
                }
            })
            tabOne.setOnClickListener {
                searchLabViewModel.setIsResultSearch(false)
                tabLayout.getTabAt(0)?.select()
            }
            tabTwo.setOnClickListener {
                searchLabViewModel.setIsResultSearch(false)
                tabLayout.getTabAt(1)?.select()
            }
            tabThree.setOnClickListener {
                searchLabViewModel.setIsResultSearch(false)
                tabLayout.getTabAt(2)?.select()
            }
            viewPagger.offscreenPageLimit = 3
            viewPagger.adapter = LaboStateAdapter(
                childFragmentManager, listOf(
                    LaboAllFragment(),
                    LaboResolvedFragment(),
                    LaboMyQuestionFragment()
                ), title
            )
            btnSearch.setOnClickListener {
                appNavigation.openSearchLabScreen()
            }
            tabLayout.setupWithViewPager(viewPagger)
        }

        binding.tabLayout.apply {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    shareViewModel.saveFocusTab = tab?.position ?: 0
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    //TODO
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    //TODO
                }

            })
            getTabAt(shareViewModel.saveFocusTab)?.select()
        }
        searchLabViewModel.isResultSearch.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.apply {
                    viewPagger.visibility = View.GONE
                    tabLayout.setSelectedTabIndicatorHeight(0)
                    rvSearchLabResult.visibility = View.VISIBLE
                    tabClick.visibility = View.VISIBLE
                    swipeRefreshLayout.visibility = View.VISIBLE
                    showDataSearch(labAdapter, loadingStateAdapter)
                    binding.tabLayout.setTabTextColors(
                        resources.getColor(R.color.color_6D5D9A),
                        resources.getColor(R.color.color_6D5D9A)
                    )
                    viewModels.setParamsSearch(searchLabViewModel.paramsSearch)
                }
            } else {
                binding.apply {
                    viewPagger.visibility = View.VISIBLE
                    tabLayout.setSelectedTabIndicatorHeight(5)
                    rvSearchLabResult.visibility = View.GONE
                    tabClick.visibility = View.GONE
                    swipeRefreshLayout.visibility = View.GONE
                    binding.tabLayout.setTabTextColors(
                        resources.getColor(R.color.color_6D5D9A),
                        resources.getColor(R.color.color_text_E3DFEF)
                    )
                }
            }
        })
        shareViewModel.isScrollToTop.observe(viewLifecycleOwner) {
            if (it) {
                viewModels.setScrollTab(it)
                shareViewModel.doneScrollView()
            }
        }

        shareViewModel.laboTabSelected.observe(viewLifecycleOwner) {
            if (it != shareViewModel.saveFocusTab) {
                shareViewModel.saveFocusTab = it
                binding.tabLayout.getTabAt(it)?.select()
            }
        }
    }

    override fun labOnClick(lab: LaboResponse) {
        lab.let {
            val bundle = Bundle()
            bundle.putSerializable(LAB, it)
            appNavigation.openLabToLabDetailScreen(bundle)
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.newQuestionBtn.setOnClickListener {
            if (!isDoubleClick) {
//                appNavigation.openLaboToNewQuestionScreen()
                showAlertDialog(R.string.can_not_add_new_question, R.string.text_OK)
            }
        }
    }

    private fun showDataSearch(adapters: LaboAdapter, loadingStateAdapter: LoadingStateAdapter) {
        binding.apply {
            rvSearchLabResult.apply {
                setHasFixedSize(true)
                adapter = adapters
                adapter = adapters.withLoadStateFooter(
                    footer = loadingStateAdapter
                )
            }
            adapters.addLoadStateListener { loadStates ->
                swipeRefreshLayout.isRefreshing = loadStates.refresh is LoadState.Loading
                if (adapters.itemCount == 0 && loadStates.refresh !is LoadState.Loading) {
                    rvSearchLabResult.visibility = View.GONE
                    tvEmpty.visibility = View.VISIBLE
                } else {
                    rvSearchLabResult.visibility = View.VISIBLE
                    tvEmpty.visibility = View.GONE
                    if (labAdapter.itemCount <= 10)
                        rvSearchLabResult.smoothScrollToPosition(0)
                }
            }
            swipeRefreshLayout.setOnRefreshListener { adapters.refresh() }
        }
        viewModels.pagingLaboSearch.observe(viewLifecycleOwner, Observer {
            adapters.submitData(lifecycle, it)
        })
    }

    private fun handleBackStack() {
        appNavigation.navController?.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            BUNDLE_KEY.NEW_QUESTION_SUCCESS
        )?.observe(viewLifecycleOwner, handleBackStack)
    }

    private fun showDialogSuccess() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.dialog_title_new_question_success)
            .setTextPositiveButton(R.string.text_OK)
    }

    private fun showDialogFirstUseLabo(context: Context, isShow: Boolean) {
        val view = View.inflate(context, R.layout.dialog_first_use_lab, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        val dialog = builder.create()
        if (isShow)
            dialog.show()
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        view.findViewById<AppCompatButton>(R.id.btn_ok).setOnClickListener {
            rxPreferences.setIsFirstTimeUseLab(false)
            dialog.dismiss()
        }
    }
}
