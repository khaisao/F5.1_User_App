package jp.slapp.android.android.utils

import jp.slapp.android.R
import jp.slapp.android.android.AppApplication
import jp.slapp.android.android.data.model.GenResItem

class GenresUtil {
    companion object {
        fun getListGenres(): ArrayList<GenResItem> {
            val listGenRes: ArrayList<GenResItem> = ArrayList()
            listGenRes.add(
                GenResItem(
                    Define.SearchCondition.GENRES_6,
                    AppApplication.getAppContext()!!.getString(R.string.genres_6)
                )
            )
            listGenRes.add(
                GenResItem(
                    Define.SearchCondition.GENRES_7,
                    AppApplication.getAppContext()!!.getString(R.string.genres_7)
                )
            )
            listGenRes.add(
                GenResItem(
                    Define.SearchCondition.GENRES_9,
                    AppApplication.getAppContext()!!.getString(R.string.genres_9)
                )
            )
            listGenRes.add(
                GenResItem(
                    Define.SearchCondition.GENRES_10,
                    AppApplication.getAppContext()!!.getString(R.string.genres_10)
                )
            )
            listGenRes.add(
                GenResItem(
                    Define.SearchCondition.GENRES_11,
                    AppApplication.getAppContext()!!.getString(R.string.genres_11)
                )
            )
            listGenRes.add(
                GenResItem(
                    Define.SearchCondition.GENRES_12,
                    AppApplication.getAppContext()!!.getString(R.string.genres_12)
                )
            )
            listGenRes.add(
                GenResItem(
                    Define.SearchCondition.GENRES_32,
                    AppApplication.getAppContext()!!.getString(R.string.genres_32)
                )
            )
            return listGenRes
        }
    }
}
