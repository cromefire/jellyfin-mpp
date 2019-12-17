package org.jellyfin.mpp.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jellyfin.mpp.app.R
import org.jellyfin.mpp.app.data.ApiService
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
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val views = root.findViewById<RecyclerView>(R.id.views)
        val viewAdapter = ViewAdapter()
        views.apply {
            layoutManager = LinearLayoutManager(root.context)
            itemAnimator = DefaultItemAnimator()
            adapter = viewAdapter
        }

        val refresh = root.findViewById<SwipeRefreshLayout>(R.id.refresh)
        refresh.setColorSchemeColors(
            resources.getColor(R.color.primary, null),
            resources.getColor(R.color.accent, null)
        )
        refresh.setOnRefreshListener {
            refreshViews(refresh, views)
        }

        refreshViews(refresh, views)

        return root
    }

    private fun refreshViews(refresh: SwipeRefreshLayout, views: RecyclerView) {
        refresh.isRefreshing = true
        GlobalScope.launch {
            val viewList = apiService.client.views()
            val adapter = ViewAdapter(viewList.Items)
            requireActivity().runOnUiThread {
                views.adapter = adapter
                refresh.isRefreshing = false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
