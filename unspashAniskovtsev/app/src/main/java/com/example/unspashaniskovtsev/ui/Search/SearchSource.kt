package com.example.unspashaniskovtsev.ui.Search

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.unspashaniskovtsev.Models.Photo
import com.example.unspashaniskovtsev.data.Network.Network

class SearchSource(
    private val query: String
): PagingSource<Int, Photo>() {

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        return try {
            val page = params.key ?: 1
            val pageSize =  params.loadSize
            val response = Network.unsplashApi.searchPhotos(query, pageSize, page).results
            val nextKey = if(response.size < pageSize) null else page+1
            val prevKey = if(page == 1) null else page-1
            LoadResult.Page(response, prevKey, nextKey)
        } catch (e: Exception){
            Log.d("SearchSource", e.message.toString())
            LoadResult.Error(e)
        }



    }
}