package jp.careapp.counseling.android.ui.search_lab

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.CategoryResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.ORDER
import jp.careapp.counseling.android.utils.SORT
import jp.careapp.counseling.databinding.FragmentSelectionCategoryBinding
import javax.inject.Inject

@AndroidEntryPoint
@Suppress("CAST_NEVER_SUCCEEDS")
class SelectionCategoryFragment :
    BaseFragment<FragmentSelectionCategoryBinding, SearchLabViewModel>() {

    override val layoutId: Int = R.layout.fragment_selection_category
    private val viewModels: SearchLabViewModel by activityViewModels()
    override fun getVM(): SearchLabViewModel = viewModels
    private lateinit var selectionAdapter: SelectionAdapter
    private lateinit var tracker: SelectionTracker<Long>
    private lateinit var criteriaSearch: String

    @Inject
    lateinit var rxPreferences: RxPreferences

    @Inject
    lateinit var appNavigation: AppNavigation
    private val listCategory: ArrayList<CategoryResponse> =
        arrayListOf(CategoryResponse(id = 0, defaultContent = "", name = SORT.DEFAULT))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            criteriaSearch = it.getString(BUNDLE_KEY.CRITERIA).toString()
        }
        if (criteriaSearch == CRITERIA_SEARCH.CATEGORY.toString())
            rxPreferences.getListCategory()?.let {
                val list = it.filter { it1 ->
                    it1.registEnable
                }
                listCategory.addAll(list)
            }
        else if (criteriaSearch == CRITERIA_SEARCH.STATUS.toString())
            listCategory.addAll(
                listOf(
                    CategoryResponse(
                        id = 1,
                        defaultContent = "",
                        name = resources.getString(R.string.accepting_answers)
                    ),
                    CategoryResponse(
                        id = 2,
                        defaultContent = "",
                        name = resources.getString(R.string.solved_and_waiting_for_the_best_answer)
                    ),
                )
            )
        else
            listCategory.addAll(
                listOf(
                    CategoryResponse(
                        id = 1,
                        defaultContent = "",
                        name = resources.getString(R.string.new_consultation)
                    ),
                    CategoryResponse(
                        id = 2,
                        defaultContent = "",
                        name = resources.getString(R.string.old_consultation)
                    ),
                    CategoryResponse(
                        id = 3,
                        defaultContent = "",
                        name = resources.getString(R.string.answer_desc)
                    ),
                    CategoryResponse(
                        id = 4,
                        defaultContent = "",
                        name = resources.getString(R.string.answer_asc)
                    ),
                    CategoryResponse(
                        id = 5,
                        defaultContent = "",
                        name = resources.getString(R.string.new_answer)
                    ),
                )
            )
    }

    override fun initView() {
        super.initView()
        selectionAdapter = SelectionAdapter()
        binding.toolbar.apply {
            btnLeft.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_left))
            btnLeft.setOnClickListener {
                appNavigation.navigateUp()
            }
            viewStatusBar.visibility = View.GONE
            when (criteriaSearch) {
                CRITERIA_SEARCH.CATEGORY.toString() -> {
                    tvTitle.text = resources.getString(R.string.category)
                }
                CRITERIA_SEARCH.STATUS.toString() -> {
                    tvTitle.text = resources.getString(R.string.status)
                }
                CRITERIA_SEARCH.SORT.toString() -> {
                    tvTitle.text = resources.getString(R.string.sort)
                }
            }
        }
        binding.recyclerview.apply {
            setHasFixedSize(true)
            adapter = selectionAdapter
        }
        selectionAdapter.submitList(listCategory)
        tracker = SelectionTracker.Builder(
            "mySelection",
            binding.recyclerview,
            CheckedKeyProvider(binding.recyclerview),
            CheckedItemDetailsLookup(binding.recyclerview),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            if (criteriaSearch == CRITERIA_SEARCH.CATEGORY.toString())
                SelectionPredicates.createSelectAnything()
            else
                SelectionPredicates.createSelectSingleAnything()
        ).build()
        selectionAdapter.tracker = tracker
        when(criteriaSearch){
            CRITERIA_SEARCH.CATEGORY.toString() ->{
                tracker.setItemsSelected(viewModels.genre.itemSelect,true)
            }
            CRITERIA_SEARCH.STATUS.toString() ->{
                tracker.setItemsSelected(viewModels.status.itemSelect,true)
            }
            CRITERIA_SEARCH.SORT.toString() ->{
                tracker.setItemsSelected(viewModels.sort.itemSelect,true)
            }
        }
        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    tracker.selection.map {
                        when (criteriaSearch) {
                            CRITERIA_SEARCH.CATEGORY.toString() -> {
                                val items =
                                    tracker.selection.map { selectionAdapter.currentList[it.toInt()] }
                                if (items.any { it.id == 0 }){
                                    viewModels.labRequest.genre = arrayListOf()
                                    viewModels.genre.content = SORT.DEFAULT
                                    viewModels.genre.itemSelect = listOf(0)
                                }
                                else{
                                    var string = ""
                                    viewModels.labRequest.genre = ArrayList(items)
                                    items.forEach { string+="${it.name}," }
                                    viewModels.genre.content = string.dropLast(1)
                                    viewModels.genre.itemSelect = tracker.selection.map { it }
                                }
                            }
                            CRITERIA_SEARCH.STATUS.toString() -> {
                                val items =
                                    tracker.selection.map { selectionAdapter.currentList[it.toInt()] }
                                if (items.any { it.id == 0 }) {
                                    viewModels.labRequest.statuses = arrayListOf()
                                    viewModels.status.content = SORT.DEFAULT
                                    viewModels.status.itemSelect = listOf(0)
                                } else if (items.any { it.id == 1 }) {
                                    viewModels.status.content =
                                        resources.getString(R.string.accepting_answers)
                                    viewModels.labRequest.statuses = arrayListOf(1)
                                    viewModels.status.itemSelect = listOf(1)
                                } else {
                                    viewModels.labRequest.statuses = arrayListOf(2, 3)
                                    viewModels.status.content =
                                        resources.getString(R.string.solved_and_waiting_for_the_best_answer)
                                    viewModels.status.itemSelect = listOf(2)
                                }
                            }
                            else -> {
                                val items =
                                    tracker.selection.map { selectionAdapter.currentList[it.toInt()] }
                                when (items[0].id) {
                                    0 -> {
                                        viewModels.labRequest.sort = ""
                                        viewModels.labRequest.order = ""
                                        viewModels.sort.content = SORT.DEFAULT
                                        viewModels.sort.itemSelect = listOf(0)
                                    }
                                    1 -> {
                                        viewModels.labRequest.sort = SORT.CREATE_AT
                                        viewModels.labRequest.order = ORDER.DESC
                                        viewModels.sort.content =
                                            resources.getString(R.string.new_consultation)
                                        viewModels.sort.itemSelect = listOf(1)
                                    }
                                    2 -> {
                                        viewModels.labRequest.sort = SORT.CREATE_AT
                                        viewModels.labRequest.order = ORDER.ASC
                                        viewModels.sort.content =
                                            resources.getString(R.string.old_consultation)
                                        viewModels.sort.itemSelect = listOf(2)
                                    }
                                    3 -> {
                                        viewModels.labRequest.sort = SORT.ANSWER_ACOUNT
                                        viewModels.labRequest.order = ORDER.DESC
                                        viewModels.sort.content = resources.getString(R.string.answer_desc)
                                        viewModels.sort.itemSelect = listOf(3)
                                    }
                                    4 -> {
                                        viewModels.labRequest.sort = SORT.ANSWER_ACOUNT
                                        viewModels.labRequest.order = ORDER.ASC
                                        viewModels.sort.content = resources.getString(R.string.answer_asc)
                                        viewModels.sort.itemSelect = listOf(4)
                                    }
                                    5 -> {
                                        viewModels.labRequest.sort = SORT.LAST_ANSWERED_AT
                                        viewModels.labRequest.order = ORDER.DESC
                                        viewModels.sort.content = resources.getString(R.string.new_answer)
                                        viewModels.sort.itemSelect = listOf(5)
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onItemStateChanged(key: Long, selected: Boolean) {
                    super.onItemStateChanged(key, selected)
                    if (criteriaSearch == CRITERIA_SEARCH.CATEGORY.toString()) {
                        if (key.toInt() == 0 && selected || tracker.selection.isEmpty && key.toInt() != 0) {
                            tracker.setItemsSelected(
                                tracker.selection.filter { it.toInt() != 0 },
                                !selected
                            )
                        }
                        if (tracker.selection.isEmpty) {
                            tracker.select(0)
                        }
                        if (key.toInt() != 0 && selected) {
                            tracker.deselect(0)
                        }
                    } else {
                        if (selected || !selected && tracker.selection.isEmpty) {
                            tracker.setItemsSelected(
                                tracker.selection.filter { it.toInt() != key.toInt() },
                                !selected
                            )
                            tracker.select(key)
                        }
                    }
                }
            })
    }
}
