package org.jellyfin.mpp.app.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jellyfin.mpp.app.data.ApiService
import org.jellyfin.mpp.common.ImageType
import org.jellyfin.mpp.common.JView
import org.jellyfin.mpp.common.MediaType

class HomeViewModel(private val apiService: ApiService) : ViewModel() {
    val media = MutableLiveData<List<JView.CollectionFolder>>(listOf())
    val movieResume = MutableLiveData<List<JView>>(listOf())
    val musicResume = MutableLiveData<List<JView>>(listOf())

    suspend fun refresh() {
        withContext(Dispatchers.IO) {
            launch {
                val views = apiService.client.views()
                media.postValue(views.Items)
            }
            launch {
                val movies = apiService.client.resume(
                    mediaTypes = listOf(MediaType.Video),
                    enableImageTypes = listOf(
                        ImageType.Primary,
                        ImageType.Backdrop,
                        ImageType.Thumb
                    )
                )
                movieResume.postValue(movies.Items)
            }
            launch {
                val musicTracks = apiService.client.resume(
                    mediaTypes = listOf(MediaType.Audio),
                    enableImageTypes = listOf(
                        ImageType.Primary,
                        ImageType.Backdrop,
                        ImageType.Thumb
                    )
                )
                musicResume.postValue(musicTracks.Items)
            }
        }
    }
}
