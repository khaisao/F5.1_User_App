package jp.careapp.counseling.android.ui.search_lab

import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.HistorySelection
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.databinding.FragmentSearchLabBinding
import javax.inject.Inject

@Suppress("UNREACHABLE_CODE")
@AndroidEntryPoint
class SearchLabFragment : BaseFragment<FragmentSearchLabBinding, SearchLabViewModel>(){

    override val layoutId: Int = R.layout.fragment_search_lab
    private val viewModels: SearchLabViewModel by activityViewModels()

    @Inject
    lateinit var appNavigation: AppNavigation
    @Inject
    lateinit var rxPreferences: RxPreferences
    override fun getVM(): SearchLabViewModel = viewModels

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModels.apply {
            genre = rxPreferences.getHistorySearchSelection().hisGenre
            status = rxPreferences.getHistorySearchSelection().hisStatus
            sort = rxPreferences.getHistorySearchSelection().hisSort
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        binding.apply {
            edtSearch.doOnTextChanged { text, _, _, _ ->
                viewModels.wordSearch = text.toString()}
            edtSearch.setText(viewModels.wordSearch)
            btnClear.setOnClickListener {
                viewModels.clearSelection()
                category.tvEnd.text = viewModels.genre.content
                status.tvEnd.text = viewModels.status.content
                sort.tvEnd.text = viewModels.sort.content
                edtSearch.text.clear()
            }
            btnClose.setOnClickListener {
                appNavigation.navigateUp()
            }
            category.apply {
                tvEnd.text = viewModels.genre.content
                tvStart.text = resources.getString(R.string.category)
                item.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString(BUNDLE_KEY.CRITERIA,CRITERIA_SEARCH.CATEGORY.toString())
                    }
                    appNavigation.openSelectionCagoryFromSearch(bundle)
                }
            }
            status.apply {
                tvStart.text = resources.getString(R.string.status)
                tvEnd.text = viewModels.status.content
                item.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString(BUNDLE_KEY.CRITERIA,CRITERIA_SEARCH.STATUS.toString())
                    }
                    appNavigation.openSelectionCagoryFromSearch(bundle)
                }
            }
            sort.apply {
                tvStart.text = resources.getString(R.string.sort)
                tvEnd.text = viewModels.sort.content
                item.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString(BUNDLE_KEY.CRITERIA,CRITERIA_SEARCH.SORT.toString())
                    }
                    appNavigation.openSelectionCagoryFromSearch(bundle)
                }
            }
            btnSearch.setOnClickListener {
                viewModels.setIsResultSearch(true)
                viewModels.apply {
                    rxPreferences.saveHistorySearchSelection(
                        HistorySelection(genre,status,sort)
                    )
                }
                viewModels.apply {
                    viewModels.labRequest.apply {
                        if(edtSearch.text.trim().isNotEmpty())
                            paramsSearch["body"] = edtSearch.text.trim()
                        else
                            paramsSearch.clear()
                        if(genre.isNotEmpty())
                            genre.forEachIndexed { index, item ->
                                paramsSearch["genres[${index}]"] = item.id
                            }
                        if(statuses.isNotEmpty())
                            statuses.forEachIndexed { index, i ->
                                paramsSearch["statuses[${index}]"] = i
                            }
                        if (sort.isNotEmpty())
                            viewModels.paramsSearch["sort"] = sort
                        if (order.isNotEmpty())
                            viewModels.paramsSearch["order"] = order
                    }
                }
                appNavigation.navigateUp()
            }
        }
    }
}

enum class CRITERIA_SEARCH{
    CATEGORY,
    STATUS,
    SORT
}