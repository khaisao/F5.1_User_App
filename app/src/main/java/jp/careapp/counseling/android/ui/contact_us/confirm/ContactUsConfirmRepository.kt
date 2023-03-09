package jp.careapp.counseling.android.ui.contact_us.confirm

import jp.careapp.counseling.android.data.model.ContactMemberRequest
import jp.careapp.counseling.android.network.ApiInterface
import javax.inject.Inject

class ContactUsConfirmRepository @Inject constructor(private val apiInterface: ApiInterface) {

    suspend fun sendContactUs(category: String, content: String, reply: Int) =
        apiInterface.sendContactUs(
            ContactMemberRequest(category, content, reply)
        )
}