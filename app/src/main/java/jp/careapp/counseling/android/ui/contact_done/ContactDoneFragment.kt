package jp.careapp.counseling.android.ui.contact_done

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.databinding.FragmentContactDoneBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactDoneFragment :
    BaseFragment<FragmentContactDoneBinding, DoneContactViewModel>() {

    override val layoutId: Int = R.layout.fragment_contact_done
    private val viewModels: DoneContactViewModel by viewModels()
    override fun getVM(): DoneContactViewModel = viewModels

    override fun bindingStateView() {
        super.bindingStateView()
        binding.appBar.apply {
            btnLeft.setImageDrawable(resources.getDrawable(R.drawable.ic_back_left))
            btnLeft.setOnClickListener {
                if (!isDoubleClick) {
                    findNavController().navigateUp()
                    findNavController().popBackStack(R.id.contactEditFragment, true)
                }
            }
            tvTitle.text = getString(R.string.contact_us)
            viewStatusBar.visibility = View.GONE
        }
    }
}
