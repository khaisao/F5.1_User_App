package jp.careapp.counseling.android.ui.labo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.utils.adapter.LoadingStateAdapter
import jp.careapp.counseling.databinding.FragmentLaboResolvedBinding
import javax.inject.Inject

@AndroidEntryPoint
class LaboResolvedFragment : Fragment() {

    private lateinit var binding: FragmentLaboResolvedBinding
    private lateinit var labAdapter: LaboAdapter
    private lateinit var loadingStateAdapter: LoadingStateAdapter

    @Inject
    lateinit var rxPreferences: RxPreferences
    private val viewModels: LaboViewModel by viewModels({ requireParentFragment() })
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLaboResolvedBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        labAdapter = LaboAdapter(parentFragment as LaboFragment, requireContext(), rxPreferences)
        loadingStateAdapter = LoadingStateAdapter()
        binding.apply {
            rvLaboResolved.apply {
                setHasFixedSize(true)
                adapter = labAdapter
                adapter = labAdapter.withLoadStateHeaderAndFooter(
                    footer = loadingStateAdapter,
                    header = loadingStateAdapter
                )
            }
            labAdapter.addLoadStateListener { loadStates ->
                swipeRefreshLayout.isRefreshing = loadStates.refresh is LoadState.Loading
                if(labAdapter.itemCount == 0 && loadStates.refresh !is LoadState.Loading){
                    rvLaboResolved.visibility = View.GONE
                    tvEmpty.visibility = View.VISIBLE
                } else {
                    rvLaboResolved.visibility = View.VISIBLE
                    tvEmpty.visibility = View.GONE
                }
            }
            swipeRefreshLayout.setOnRefreshListener { labAdapter.refresh() }
        }
        viewModels.pagingLaboResolved.observe(viewLifecycleOwner, Observer {
            labAdapter.submitData(lifecycle, it)
        })
        viewModels.tabResolveListener.observe(viewLifecycleOwner) {
            if (it) {
                binding.rvLaboResolved.smoothScrollToPosition(0)
            }
        }
    }
}