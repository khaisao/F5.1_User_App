package jp.careapp.counseling.android.data.repository.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import jp.careapp.counseling.android.data.model.labo.LaboResponse
import jp.careapp.counseling.android.network.ApiInterface
import retrofit2.HttpException
import java.util.*

class LaboPagingSource constructor(
    private val apiService: ApiInterface,
    private val labParams: MutableMap<String, Any> = HashMap()
): PagingSource<Int, LaboResponse>() {
    override fun getRefreshKey(state: PagingState<Int, LaboResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LaboResponse> {
        val position = params.key ?:1
        labParams["page"] = position
        return try {
           apiService.getLabo(params = labParams)
               .run {
                   LoadResult.Page(
                       data = this.dataResponse,
                       prevKey = if (position <= 1) null else position - 1,
                       nextKey = if (position >= getTotalPage(
                               this.pagination.total,
                               this.pagination.limit
                           )
                       ) null else position + 1
                   )
               }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }

    private fun getTotalPage(total: Int, limit: Int): Int {
        return if (total % limit == 0)
            total / limit
        else
            total / limit + 1
    }
}