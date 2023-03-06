package jp.careapp.counseling.android.ui.contact_edit

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.executeAfter
import jp.careapp.core.utils.onTextChange
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.ContactMemberRequest
import jp.careapp.counseling.databinding.FragmentContactEditBinding
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.main.MainViewModel
import jp.careapp.counseling.android.ui.my_page.MyPageFragment
import jp.careapp.counseling.android.ui.my_page.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.utils.StringUtils.isValidEmail
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.utils.SignedUpStatus
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

@AndroidEntryPoint
class ContactEditFragment : BaseFragment<FragmentContactEditBinding, EditContactViewModel>() {

    override val layoutId: Int = R.layout.fragment_contact_edit
    private val viewModels: EditContactViewModel by viewModels()
    private val mypageViewModel: MyPageViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val shareViewModel: ShareViewModel by activityViewModels()
    var categories: MutableList<String> = arrayListOf()
    var contactRequest = ContactMemberRequest()
    var type_contact: String = ""

    @Inject
    lateinit var rxPreferences: RxPreferences

    @Inject
    lateinit var appNavigation: AppNavigation
    override fun getVM(): EditContactViewModel = viewModels
    override fun onResume() {
        super.onResume()
        if (binding.necessary.isChecked)
            contactRequest.reply = 1
        else
            contactRequest.reply = 0
    }

    override fun bindingStateView() {
        super.bindingStateView()
//        mypageViewModel.forceRefresh()
        categories = arrayListOf(
            getString(R.string.payment),
            getString(R.string.about_account),
            getString(R.string.about_consultation),
            getString(R.string.about_fun),
            getString(R.string.label_other)
        )
        type_contact = arguments?.getString("type_contact") ?: MyPageFragment::class.java.simpleName
        binding.appBar.apply {
            btnLeft.setImageDrawable(resources.getDrawable(R.drawable.ic_back_left))
            btnLeft.setOnClickListener {
                if (!isDoubleClick)
                    findNavController().navigateUp()
            }
            tvTitle.text = getString(R.string.contact_us)
            viewStatusBar.visibility = View.GONE
        }
        updateView()
    }

    private fun updateView() {
        if (rxPreferences.getSignedUpStatus() == SignedUpStatus.LOGIN_WITHOUT_EMAIL) {
            val contactEdit = ContactEdit()
            if (type_contact == MyPageFragment::class.java.simpleName) {
                actionFromMyPageWithoutEmail()
            } else {
                actionFromLogin(contactEdit)
            }
            binding.executeAfter {
//                mypageViewModel.uiMember.observe(
//                    viewLifecycleOwner,
//                    {
//                        tvMail.isVisible = it.signupStatus != SignedUpStatus.LOGIN_WITHOUT_EMAIL
//                        edtMailEdit.isVisible = it.signupStatus == SignedUpStatus.LOGIN_WITHOUT_EMAIL
//                        edtMailEdit.setText(if (it.signupStatus == SignedUpStatus.LOGIN_WITHOUT_EMAIL) "" else it.mail)
//                        tvCodeValue.text = it.code
//                        contactEdit.mail = it.mail
//                        contactEdit.code = it.code
//                        rxPreferences.setSignedUpStatus(it.signupStatus ?: SignedUpStatus.UNKNOWN)
//                    }
//                )
                edtMailEdit.onTextChange {
                    if (type_contact == MyPageFragment::class.java.simpleName) {
                        enableConfirmButton(isValidInputWithEmailCategoryAndInquiry)
                    } else {
                        enableConfirmButton(isValidInputWithEmailAndInquiry)
                    }
                }
                edtContent.onTextChange {
                    if (type_contact == MyPageFragment::class.java.simpleName) {
                        enableConfirmButton(isValidInputWithEmailCategoryAndInquiry)
                    } else {
                        enableConfirmButton(isValidInputWithEmailAndInquiry)
                    }
                }
                btnNecessary.setOnClickListener {
                    necessary.isChecked = true
                    unnecessary.isChecked = !necessary.isChecked
                    contactRequest.reply = 1
                }
                btnUnnecessary.setOnClickListener {
                    unnecessary.isChecked = true
                    necessary.isChecked = !unnecessary.isChecked
                    contactRequest.reply = 0
                }
                btnConfirm.setOnClickListener {
                    if (!isDoubleClick) {
                        contactRequest.content = edtContent.text.toString().trim()
                        contactEdit.contactMemberRequest = contactRequest
                        contactEdit.mail = edtMailEdit.text.toString().trim()
                        val bundle = Bundle().apply {
                            putParcelable("contact", contactEdit)
                            putString("type_contact", type_contact)
                        }
                        appNavigation.openEditContactToConfirmContact(bundle)
                    }
                }
            }
        } else {
            var contactEdit = ContactEdit()
            if (type_contact == MyPageFragment::class.java.simpleName) {
                actionFromMypage()
            } else {
                actionFromLogin(contactEdit)
            }
            binding.executeAfter {
//                mypageViewModel.uiMember.observe(
//                    viewLifecycleOwner,
//                    Observer {
//                        tvMail.isVisible = it.signupStatus != SignedUpStatus.LOGIN_WITHOUT_EMAIL
//                        edtMailEdit.isVisible = it.signupStatus == SignedUpStatus.LOGIN_WITHOUT_EMAIL
//                        edtMailEdit.setText(if (it.signupStatus == SignedUpStatus.LOGIN_WITHOUT_EMAIL) "" else it.mail)
//                        tvMail.text = it.mail
//                        tvCodeValue.text = it.code
//                        contactEdit.mail = it.mail
//                        contactEdit.code = it.code
//                        rxPreferences.setSignedUpStatus(it.signupStatus ?: SignedUpStatus.UNKNOWN)
//                    }
//                )
                edtContent.onTextChange {
                    if (type_contact == MyPageFragment::class.java.simpleName) {
                        if (edtContent.text.trim()
                                .isNullOrEmpty() || spinner.text.toString() == resources.getString(R.string.please_select)
                        ) {
                            btnConfirm.isEnabled = false
                            btnConfirm.background =
                                resources.getDrawable(R.drawable.bg_disable_button)
                            btnConfirm.setTextColor(resources.getColor(R.color.color_6D5D9A))
                        } else {
                            btnConfirm.isEnabled = true
                            btnConfirm.background =
                                resources.getDrawable(R.drawable.bg_gradient_common)
                            btnConfirm.setTextColor(resources.getColor(R.color.white))
                        }
                    } else {
                        if (edtContent.text.trim().isNullOrEmpty()
                        ) {
                            btnConfirm.isEnabled = false
                            btnConfirm.background =
                                resources.getDrawable(R.drawable.bg_disable_button)
                            btnConfirm.setTextColor(resources.getColor(R.color.color_6D5D9A))
                        } else {
                            btnConfirm.isEnabled = true
                            btnConfirm.background =
                                resources.getDrawable(R.drawable.bg_gradient_common)
                            btnConfirm.setTextColor(resources.getColor(R.color.white))
                        }
                    }
                }
                btnNecessary.setOnClickListener {
                    necessary.isChecked = true
                    unnecessary.isChecked = !necessary.isChecked
                    contactRequest.reply = 1
                }
                btnUnnecessary.setOnClickListener {
                    unnecessary.isChecked = true
                    necessary.isChecked = !unnecessary.isChecked
                    contactRequest.reply = 0
                }
                btnConfirm.setOnClickListener {
                    if (!isDoubleClick) {
                        contactRequest.content = edtContent.text.toString().trim()
                        contactEdit.contactMemberRequest = contactRequest
                        val bundle = Bundle().apply {
                            putParcelable("contact", contactEdit)
                            putString("type_contact", type_contact)
                        }
                        appNavigation.openEditContactToConfirmContact(bundle)
                    }
                }
            }
        }
    }

    fun actionFromMypage() {
        val listPopupWindow = ListPopupWindow(requireContext(), null, R.attr.listPopupWindowStyle)
        listPopupWindow.anchorView = binding.spinner
        val adapter = ArrayAdapter(requireContext(), R.layout.list_popup_window_item, categories)
        listPopupWindow.setAdapter(adapter)
        listPopupWindow.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            binding.spinner.setText(categories.get(position))
            listPopupWindow.dismiss()
        }
        binding.apply {
            spinner.visibility = View.VISIBLE
            tvCategory.visibility = View.GONE
            spinner.setOnClickListener {
                (!isDoubleClick)
                listPopupWindow.show()
            }
            spinner.onTextChange {
                if (edtContent.text.trim()
                    .isNullOrEmpty() || spinner.text.toString() == resources.getString(R.string.please_select)
                ) {
                    btnConfirm.isEnabled = false
                    btnConfirm.background = resources.getDrawable(R.drawable.bg_disable_button)
                    btnConfirm.setTextColor(resources.getColor(R.color.color_6D5D9A))
                    contactRequest.category = spinner.text.toString()
                    edtContent.isFocusable = true
                } else {
                    btnConfirm.isEnabled = true
                    btnConfirm.background = resources.getDrawable(R.drawable.bg_gradient_common)
                    btnConfirm.setTextColor(resources.getColor(R.color.white))
                    contactRequest.category = spinner.text.toString()
                    edtContent.isFocusable = true
                }
            }
        }
    }

    private fun actionFromMyPageWithoutEmail() {
        val listPopupWindow = ListPopupWindow(requireContext(), null, R.attr.listPopupWindowStyle)
        listPopupWindow.anchorView = binding.spinner
        val adapter = ArrayAdapter(requireContext(), R.layout.list_popup_window_item, categories)
        listPopupWindow.setAdapter(adapter)
        listPopupWindow.setOnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            binding.spinner.setText(categories[position])
            listPopupWindow.dismiss()
        }
        binding.apply {
            spinner.visibility = View.VISIBLE
            tvCategory.visibility = View.GONE
            spinner.setOnClickListener {
                (!isDoubleClick)
                listPopupWindow.show()
            }
            spinner.onTextChange {
                contactRequest.category = spinner.text.toString()
                edtContent.isFocusable = true
                enableConfirmButton(isValidInputWithEmailCategoryAndInquiry)
            }
        }
    }

    fun actionFromLogin(contactEdit: ContactEdit) {
        mainViewModel.getMail.observe(
            viewLifecycleOwner,
            Observer {
                binding.tvMail.text = it
                contactEdit.mail = it ?: ""
            }
        )
        binding.apply {
            tvCode.visibility = View.GONE
            tvCodeValue.visibility = View.GONE
            spinner.visibility = View.GONE
            tvCategory.visibility = View.VISIBLE
            contactRequest.category = tvCategory.text.toString()
        }
    }

    private fun enableConfirmButton(enabled: Boolean) {
        binding.btnConfirm.apply {
            isEnabled = enabled
            background = activity?.let { activity ->
                ContextCompat.getDrawable(
                    activity,
                    if (enabled) R.drawable.bg_gradient_common else R.drawable.bg_disable_button
                )
            }
            this@ContactEditFragment.context?.let {
                setTextColor(
                    ContextCompat.getColor(
                        it,
                        if (enabled) R.color.white else R.color.color_6D5D9A
                    )
                )
            }
        }
    }

    private val isValidInputWithEmailAndInquiry: Boolean
        get() = binding.edtMailEdit.text.trim().isValidEmail() &&
                binding.edtContent.text.trim().isNotEmpty()

    private val isValidInputWithEmailCategoryAndInquiry: Boolean
        get() = binding.edtMailEdit.text.trim().isValidEmail() &&
                binding.edtContent.text.trim().isNotEmpty() &&
                binding.spinner.text.toString() != resources.getString(R.string.please_select)
}

@Parcelize
data class ContactEdit(
    var mail: String = "",
    var code: String = "",
    var contactMemberRequest: ContactMemberRequest = ContactMemberRequest()
) : Parcelable
