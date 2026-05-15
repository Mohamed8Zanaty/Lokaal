package com.example.lokaal.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.lokaal.domain.model.Moment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class MomentsPagingSource(
    private val store: FirebaseFirestore
) : PagingSource<QuerySnapshot, Moment>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Moment>): QuerySnapshot? = null

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Moment> {
        return try {
            val baseQuery = store
                .collection("moments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(params.loadSize.toLong())

            val query = if (params.key != null) baseQuery.startAfter(params.key!!.documents.last()) else baseQuery
            val snapshot = query.get().await()
            val moments = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Moment::class.java)
            }

            LoadResult.Page(
                data = moments,
                prevKey = null,
                nextKey = if(snapshot.isEmpty) null else snapshot
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}