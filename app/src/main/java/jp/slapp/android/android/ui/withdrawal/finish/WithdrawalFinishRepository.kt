package jp.slapp.android.android.ui.withdrawal.finish

import jp.slapp.android.android.data.network.CategoryResponse
import jp.slapp.android.android.data.pref.AppPreferences
import jp.slapp.android.android.network.ApiInterface
import jp.slapp.android.android.utils.dummyCategoryData
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