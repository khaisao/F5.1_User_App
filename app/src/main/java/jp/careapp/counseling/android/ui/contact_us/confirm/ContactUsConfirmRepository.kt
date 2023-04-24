package jp.careapp.counseling.android.ui.contact_us.confirm

import jp.careapp.counseling.android.data.model.ContactRequestWithoutMail
import jp.careapp.counseling.android.data.model.ContactRequestWithMail
import jp.careapp.counseling.android.network.ApiInterface
import javax.inject.Inject

class ContactUsConfirmRepository @Inject constructor(private val apiInterface: ApiInterface) {

    suspend fun sendContactWithoutMail(category: String, content: String, reply: Int) =
        apiInterface.sendContactWithoutMail(ContactRequestWithoutMail(category, content, reply))

    suspend fun sendContactWithMail(category: String, content: String, reply: Int, email: String) =
        apiInterface.sendContactWithMail(ContactRequestWithMail(category, content, reply, email))
}