package jp.careapp.counseling.android.ui.withdrawal.finish

import jp.careapp.counseling.android.data.network.CategoryResponse
import jp.careapp.counseling.android.data.pref.AppPreferences
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.utils.dummyCategoryData
import javax.inject.Inject

class WithdrawalFinishRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val appPreferences: AppPreferences
) {

    suspend fun getListCategory() = apiInterface.getListCategory()

    fun saveListCategory(categoryList: List<CategoryResponse>) =
        appPreferences.saveListCategory(categoryList)

    fun saveListCategoryDummy() = appPreferences.saveListCategory(dummyCategoryData())
}