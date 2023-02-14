package jp.careapp.counseling.android.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import jp.careapp.counseling.android.data.model.labo.LaboResponse
import jp.careapp.counseling.android.data.repository.source.LaboPagingSource
import jp.careapp.counseling.android.network.ApiInterface
import kotlinx.coroutines.flow.Flow
import java.util.HashMap
import javax.inject.Inject

class LaboRepository @Inject constructor(
    private val apiService: ApiInterface,
): FlowUseCase<MutableMap<String, Any>, LaboResponse>() {

    override fun execute(parameters: MutableMap<String, Any>): Flow<PagingData<LaboResponse>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = 10,
                pageSize = 10,
                prefetchDistance = 2,
                enablePlaceholders = true,
                maxSize = 200
            ),
            pagingSourceFactory = {LaboPagingSource(apiService,parameters)}
        ).flow
    }
}