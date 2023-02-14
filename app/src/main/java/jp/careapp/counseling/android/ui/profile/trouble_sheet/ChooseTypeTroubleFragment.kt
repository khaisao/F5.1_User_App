package jp.careapp.counseling.android.ui.profile.trouble_sheet

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.SimpleItemAnimator
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.SearchCategoryItem
import jp.careapp.counseling.databinding.FragmentChooseTypeTroubleBinding
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.POSITION_TYPE_TROUBLE
import jp.careapp.counseling.android.utils.Define.Companion.RESPONSE
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChooseTypeTroubleFragment :
    BaseFragment<FragmentChooseTypeTroubleBinding, ChooseTypeTroubleViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId = R.layout.fragment_choose_type_trouble

    private val viewModel: ChooseTypeTroubleViewModel by viewModels()

    override fun getVM(): ChooseTypeTroubleViewModel = viewModel

    private var chooseItem = false
    private val mAdapter: ChooseCategoryAdapter by lazy {
        ChooseCategoryAdapter(
            requireContext(),
            onClickListener = { position, isChecked ->
                chooseItem = true
                appNavigation.navController?.previousBackStackEntry?.savedStateHandle?.set(
                    POSITION_TYPE_TROUBLE,
                    // value from server start form 1
                    position + 1
                )
                binding.itemSearchRv.post(
                    object : Runnable {
                        override fun run() {
                            mAdapter.notifyDataSetChanged()
                        }
                    }
                )
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!chooseItem) {
            appNavigation.navController?.previousBackStackEntry?.savedStateHandle?.set(
                POSITION_TYPE_TROUBLE,
                // reset postion when don't choose
                -1
            )
        }
    }

    private lateinit var listData: List<SearchCategoryItem>
    override fun initView() {
        super.initView()
        val bundle = arguments
        if (bundle != null) {
            val title = bundle.getString(BUNDLE_KEY.TITLE, "")
            binding.toolbar.setTvTitle(title)
            val type = bundle.getInt(BUNDLE_KEY.TYPE_SEARCH, RESPONSE)
            if (type == RESPONSE) {
                listData = listOf(
                    SearchCategoryItem(getString(R.string.talk_to_me), false),
                    SearchCategoryItem(getString(R.string.advice), false)
                )
            } else {
                listData = listOf(
                    SearchCategoryItem(getString(R.string.answer_soon), false),
                    SearchCategoryItem(getString(R.string.answer_slowly), false)
                )
            }
            val position = bundle.getInt(BUNDLE_KEY.ITEM_SELECT, -1)

            // value of server from 1
            mAdapter.setPositionSelect(position - 1)
            mAdapter.run {
                submitList(
                    listData
                )
            }
        }
        (binding.itemSearchRv.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding.itemSearchRv.adapter = mAdapter
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.toolbar.setOnToolBarClickListener(
            object :
                ToolBarCommon.OnToolBarClickListener() {
                override fun onClickLeft() {
                    super.onClickLeft()
                    appNavigation.navController!!.popBackStack()
                }
            }
        )
    }
}
