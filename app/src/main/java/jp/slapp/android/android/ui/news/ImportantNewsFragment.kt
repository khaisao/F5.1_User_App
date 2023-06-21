package jp.slapp.android.android.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import jp.careapp.core.utils.executeAfter
import jp.slapp.android.databinding.FragmentImportantNewsBinding
import jp.slapp.android.android.utils.event.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImportantNewsFragment : Fragment() {

    private lateinit var binding: FragmentImportantNewsBinding
    private lateinit var newsAdapter: NewsAdapter
    private val viewModels: NewsViewModel by viewModels({ requireParentFragment() })
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImportantNewsBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsAdapter = NewsAdapter(viewLifecycleOwner, viewModels)
        binding.executeAfter {
            rvNotices.setHasFixedSize(true)
            rvNotices.adapter = newsAdapter
            viewModel = viewModels
        }
        viewModels.uiNotices.observe(
            viewLifecycleOwner,
            Observer {
                it ?: return@Observer
                newsAdapter.submitList(it.filter { it.category == 2 })
            }
        )
        viewModels.loadingNotices.observe(
            viewLifecycleOwner,
            Observer {
                binding.loading = it
            }
        )
        viewModels.error.observe(
            viewLifecycleOwner,
            EventObserver {
            }
        )
    }
}
