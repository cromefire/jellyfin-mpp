package org.jellyfin.mpp.app.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import org.jellyfin.mpp.app.R
import org.jellyfin.mpp.common.ImageType
import org.jellyfin.mpp.common.JView

class ItemAdapter(private val items: LiveData<List<JView>>) : RecyclerView.Adapter<ItemViewHolder>() {
    private val observer = Observer<List<JView>> {
        // Todo: optimize?
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        items.observeForever(observer)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        items.removeObserver(observer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val homeItem = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ItemViewHolder(homeItem)
    }

    override fun getItemCount(): Int {
        return items.value!!.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val view = items.value!![position]

        val primary = view.ImageTags[ImageType.Primary]
        if (primary != null) {
            holder.image.setImageURI("https://jellyfin.cromefire.myds.me/jellyfin/Items/${view.Id}/Images/Primary?maxHeight=390&maxWidth=260&tag=$primary&quality=90")
        }

        when (view) {
            is JView.Movie -> {
                holder.year.apply {
                    visibility = View.VISIBLE
                    text = view.ProductionYear.toString()
                }
            }
            else -> {}
        }

        holder.title.text = if (view.Name.length > 20) {
            "${view.Name.substring(0, 17)}..."
        } else {
            view.Name
        }
    }
}

class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val image: SimpleDraweeView = itemView.findViewById(R.id.image)
    val title: TextView = itemView.findViewById(R.id.item_title)
    val year: TextView = itemView.findViewById(R.id.year)
}
