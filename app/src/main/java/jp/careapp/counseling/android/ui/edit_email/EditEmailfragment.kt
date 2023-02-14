package jp.careapp.counseling.android.ui.edit_email

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.databinding.FragmentEditEmailBinding

class EditEmailfragment : BaseFragment<FragmentEditEmailBinding, EditEmailViewModel>() {

    override val layoutId: Int = R.layout.fragment_edit_email
    private val viewModels: EditEmailViewModel by viewModels()
    override fun getVM(): EditEmailViewModel = viewModels
    private var email: String = ""
    override fun bindingStateView() {
        super.bindingStateView()
        email = arguments?.getString("email") ?: ""

        binding.etInputEmail.setText(email)
        binding.apply {
            appBar.also {
                it.btnLeft.setOnClickListener {
                    findNavController().navigateUp()
                }
                it.btnLeft.setImageDrawable(resources.getDrawable(R.drawable.ic_back_left))
                it.tvTitle.text = getString(R.string.sub_title_edt_reregister)
            }
        }
    }
}
