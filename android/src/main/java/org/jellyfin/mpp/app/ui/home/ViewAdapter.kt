package org.jellyfin.mpp.app.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jellyfin.mpp.app.R
import org.jellyfin.mpp.common.JView

class ViewAdapter(val views: List<JView>? = null) : RecyclerView.Adapter<ViewViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewViewHolder {
        val homeItem = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)
        return ViewViewHolder(homeItem)
    }

    override fun getItemCount(): Int {
        return views?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewViewHolder, position: Int) {
        val view = views!![position]
        holder.title.text = view.Name
        val itemAdapter = ItemAdapter()
        holder.items.apply {
            layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            itemAnimator = DefaultItemAnimator()
            adapter = itemAdapter
        }
    }
}

class ViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title = itemView.findViewById<TextView>(R.id.title)
    val items = itemView.findViewById<RecyclerView>(R.id.items)
}
