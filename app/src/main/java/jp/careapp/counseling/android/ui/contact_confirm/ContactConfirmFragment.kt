package jp.careapp.counseling.android.ui.contact_confirm

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.executeAfter
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.ContactRequest
import jp.careapp.counseling.databinding.FragmentContactConfirmBinding
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.contact_edit.ContactEdit
import jp.careapp.counseling.android.ui.my_page.MyPageFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.counseling.android.data.model.ContactRequestWithoutMail
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.utils.SignedUpStatus
import javax.inject.Inject

@AndroidEntryPoint
class ContactConfirmFragment :
    BaseFragment<FragmentContactConfirmBinding, ConfirmContactViewModel>() {

    override val layoutId: Int = R.layout.fragment_contact_confirm
    private val viewModels: ConfirmContactViewModel by viewModels()
    override fun getVM(): ConfirmContactViewModel = viewModels

    @Inject
    lateinit var appNavigation: AppNavigation
    @Inject
    lateinit var rxPreferences: RxPreferences

    private var contactEdit: ContactEdit? = null
    var type_contact: String = ""
    override fun bindingStateView() {
        type_contact = arguments?.getString("type_contact") ?: MyPageFragment::class.java.simpleName
        contactEdit = arguments?.getParcelable("contact")
        super.bindingStateView()
        if (type_contact != MyPageFragment::class.java.simpleName) {
            binding.apply {
                tvCode.visibility = View.GONE
                tvCodeValue.visibility = View.GONE
            }
        }
        binding.appBar.apply {
            btnLeft.setImageDrawable(resources.getDrawable(R.drawable.ic_back_left))
            btnLeft.setOnClickListener {
                if (!isDoubleClick)
                    findNavController().navigateUp()
            }
            tvTitle.text = getString(R.string.contact_us)
            viewStatusBar.visibility = View.GONE
        }
        contactEdit?.let { rs ->
            binding.executeAfter {
                contact = rs
                binding.btnBackEdit.setOnClickListener {
                    if (!isDoubleClick)
                        appNavigation.navigateUp()
                }
            }
            binding.btnSend.setOnClickListener {
                if (!isDoubleClick)
                    if (rxPreferences.getSignedUpStatus() == SignedUpStatus.LOGIN_WITHOUT_EMAIL) {
                        viewModels.setParamsContactWithoutMail(
                            ContactRequestWithoutMail(
                                rs.contactMemberRequest.category,
                                getString(R.string.contact_content_with_code, rs.code, rs.contactMemberRequest.content),
                                rs.contactMemberRequest.reply,
                                rs.mail
                            )
                        )
                    } else {
                        if (type_contact == MyPageFragment::class.java.simpleName)
                            viewModels.setParamsContactMember(rs.contactMemberRequest)
                        else
                            viewModels.setParamsContact(
                                ContactRequest(
                                    rs.contactMemberRequest.category,
                                    rs.contactMemberRequest.content,
                                    rs.contactMemberRequest.reply,
                                    rs.mail
                                )
                            )
                    }
            }
        }
        viewModels.addContactLoading.observe(
            viewLifecycleOwner,
            Observer {
                showHideLoading(it)
            }
        )
        viewModels.addContactMemberLoading.observe(
            viewLifecycleOwner,
            Observer {
                showHideLoading(it)
            }
        )
        viewModels.addContactWithoutMailLoading.observe(
            viewLifecycleOwner,
            Observer {
                showHideLoading(it)
            }
        )

        viewModels.addContactMemberSuccess.observe(
            viewLifecycleOwner,
            Observer {
                if (type_contact == MyPageFragment::class.java.simpleName)
                    appNavigation.openContactConfirmToContactDone()
            }
        )
        viewModels.addContactSuccess.observe(
            viewLifecycleOwner,
            Observer {
                if (type_contact != MyPageFragment::class.java.simpleName)
                    appNavigation.openContactConfirmToContactDone()
            }
        )
        viewModels.addContactWithoutMailSuccess.observe(
            viewLifecycleOwner,
            Observer {
                appNavigation.openContactConfirmToContactDone()
            }
        )

        viewModels.error.observe(
            viewLifecycleOwner,
            Observer {
            }
        )
    }
}
