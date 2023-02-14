package jp.careapp.counseling.android.data.model

import jp.careapp.counseling.android.ui.search_lab.SaveStateSelection
import jp.careapp.counseling.android.utils.SORT

data class HistorySelection(
    var hisGenre: SaveStateSelection = SaveStateSelection(content = SORT.DEFAULT),
    var hisStatus: SaveStateSelection = SaveStateSelection(content = SORT.DEFAULT),
    var hisSort: SaveStateSelection = SaveStateSelection(content = SORT.DEFAULT)
)