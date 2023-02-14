package jp.careapp.counseling.android.ui.labo.report

import androidx.appcompat.widget.AppCompatRadioButton
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentReportLaboBinding
import javax.inject.Inject

@AndroidEntryPoint
class ReportLaboFragment : BaseFragment<FragmentReportLaboBinding, ReportLaboViewModel>() {

    @Inject
    lateinit var navigation: AppNavigation

    private val viewModel: ReportLaboViewModel by viewModels()
    override val layoutId: Int = R.layout.fragment_report_labo
    override fun getVM(): ReportLaboViewModel = viewModel

    override fun initView() {
        super.initView()

        arguments?.apply {
            if (containsKey(BUNDLE_KEY.LAB_ID)) {
                viewModel.setLabId(getInt(BUNDLE_KEY.LAB_ID))
            }
        }

        binding.apply {
            toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
                override fun onClickLeft() {
                    super.onClickLeft()
                    if (!isDoubleClick) {
                        navigation.navigateUp()
                    }
                }
            })

            tvReport.setOnClickListener {
                if (!isDoubleClick) {
                    val reason =
                        rgReason.findViewById<AppCompatRadioButton>(rgReason.checkedRadioButtonId).text.toString()
                    viewModel.reportLabo(reason)
                }
            }

            rgReason.setOnCheckedChangeListener { _, _ ->
                tvReport.isEnabled = true
            }
        }

        viewModel.actionState.observe(viewLifecycleOwner) {
            when (it) {
                ActionState.ReportLaboSuccess -> {
                    navigation.navigateUp()
                }
            }
        }
    }
}