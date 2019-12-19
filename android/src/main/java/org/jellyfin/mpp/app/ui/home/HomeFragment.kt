package org.jellyfin.mpp.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jellyfin.mpp.app.R
import org.jellyfin.mpp.app.data.ApiService
import org.jellyfin.mpp.common.JView
import javax.inject.Inject

class HomeFragment : DaggerFragment() {
    private lateinit var homeViewModel: HomeViewModel
    @Inject
    lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this, HomeViewModelFactory(apiService))
                .get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val media = root.findViewById<RecyclerView>(R.id.media)
        @Suppress("UNCHECKED_CAST")
        val mediaAdapter = ItemAdapter(homeViewModel.media as LiveData<List<JView>>)
        media.apply {
            layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
            itemAnimator = DefaultItemAnimator()
            adapter = mediaAdapter
            isNestedScrollingEnabled = false
        }
        val movieResume = root.findViewById<RecyclerView>(R.id.movie_resume)
        val movieResumeAdapter = ItemAdapter(homeViewModel.movieResume)
        movieResume.apply {
            layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
            itemAnimator = DefaultItemAnimator()
            adapter = movieResumeAdapter
        }
        val musicResume = root.findViewById<RecyclerView>(R.id.music_resume)
        val musicResumeAdapter = ItemAdapter(homeViewModel.musicResume)
        musicResume.apply {
            layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
            itemAnimator = DefaultItemAnimator()
            adapter = musicResumeAdapter
        }

        val refresh = root.findViewById<SwipeRefreshLayout>(R.id.refresh)
        refresh.setColorSchemeColors(
            resources.getColor(R.color.primary, null),
            resources.getColor(R.color.accent, null)
        )
        refresh.setOnRefreshListener {
            refreshViews(refresh)
        }

        refreshViews(refresh)

        return root
    }

    private fun refreshViews(refresh: SwipeRefreshLayout) {
        refresh.isRefreshing = true
        GlobalScope.launch {
            homeViewModel.refresh()
            requireActivity().runOnUiThread {
                media_layout.visibility = if (homeViewModel.media.value!!.isNotEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                movie_resume_layout.visibility =
                    if (homeViewModel.movieResume.value!!.isNotEmpty()) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                music_resume_layout.visibility =
                    if (homeViewModel.musicResume.value!!.isNotEmpty()) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                refresh.isRefreshing = false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
