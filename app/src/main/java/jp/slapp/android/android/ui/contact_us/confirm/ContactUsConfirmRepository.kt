package jp.slapp.android.android.ui.contact_us.confirm

import jp.slapp.android.android.data.model.ContactRequestWithoutMail
import jp.slapp.android.android.data.model.ContactRequestWithMail
import jp.slapp.android.android.network.ApiInterface
import javax.inject.Inject

class ContactUsConfirmRepository @Inject constructor(private val apiInterface: ApiInterface) {

    suspend fun sendContactWithoutMail(category: String, content: String, reply: Int) =
        apiInterface.sendContactWithoutMail(ContactRequestWithoutMail(category, content, reply))

    suspend fun sendContactWithMail(category: String, content: String, reply: Int, email: String) =
        apiInterface.sendContactWithMail(ContactRequestWithMail(category, content, reply, email))
}