package jp.careapp.counseling.android.data.database

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ConsultantDatabase(
    @PrimaryKey
    var id: Int = 0,
    var content: String? = null
) : RealmObject()
