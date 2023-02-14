package jp.careapp.counseling.android.ui.new_question

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.SimpleItemAnimator
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.SearchCategoryItem
import jp.careapp.counseling.android.data.network.CategoryResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.profile.trouble_sheet.ChooseCategoryAdapter
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.POSITION_TYPE_TROUBLE
import jp.careapp.counseling.android.utils.Define.Companion.RESPONSE
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentChooseTypeCategoryBinding
import javax.inject.Inject

@AndroidEntryPoint
class ChooseTypeCategoryFragment :
    BaseFragment<FragmentChooseTypeCategoryBinding, ChooseTypeCategoryViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    override val layoutId = R.layout.fragment_choose_type_category

    private val viewModel: ChooseTypeCategoryViewModel by viewModels()

    override fun getVM(): ChooseTypeCategoryViewModel = viewModel

    private val listCategory: MutableList<SearchCategoryItem> by lazy {
        val temp = mutableListOf<SearchCategoryItem>()
        val listCategoryResponse = rxPreferences.getListCategory()
        listCategoryResponse?.let {
            for (i in it) {
                if (i.registEnable) temp.add(SearchCategoryItem(i.name, false))
            }
        }
        temp
    }

    private var chooseItem = false
    private var position = -1
    private val mAdapter: ChooseCategoryAdapter by lazy {
        ChooseCategoryAdapter(
            requireContext(),
            onClickListener = { position, isChecked ->
                this.position = position + 1
                chooseItem = true
                appNavigation.navController?.previousBackStackEntry?.savedStateHandle?.set(
                    POSITION_TYPE_TROUBLE,
                    // value from server start form 1
                    position + 1
                )
                binding.itemSearchRv.post { mAdapter.notifyDataSetChanged() }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!chooseItem) {
            appNavigation.navController?.previousBackStackEntry?.savedStateHandle?.set(
                POSITION_TYPE_TROUBLE,
                position
            )
        }
    }

    override fun initView() {
        super.initView()
        val bundle = arguments
        if (bundle != null) {
            val title = bundle.getString(BUNDLE_KEY.TITLE, "")
            binding.toolbar.setTvTitle(title)
            position = bundle.getInt(BUNDLE_KEY.ITEM_SELECT, -1)
            // value of server from 1
            mAdapter.setPositionSelect(position - 1)
            mAdapter.run {
                submitList(
                    listCategory
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
                    appNavigation.navController?.previousBackStackEntry?.savedStateHandle?.set(
                        POSITION_TYPE_TROUBLE,
                        position
                    )
                    appNavigation.navController!!.popBackStack()
                }
            }
        )
    }
}
