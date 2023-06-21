package jp.slapp.android.android.data.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

abstract class FlowUseCase<in P, R : Any> {

    operator fun invoke(parameters: P): Flow<PagingData<R>> = execute(parameters)

    protected abstract fun execute(parameters: P): Flow<PagingData<R>>
}