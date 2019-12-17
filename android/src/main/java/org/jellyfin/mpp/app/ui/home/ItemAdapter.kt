package org.jellyfin.mpp.app.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import org.jellyfin.mpp.app.R

class ItemAdapter : RecyclerView.Adapter<ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val homeItem = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ItemViewHolder(homeItem)
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.image.apply {
            setImageURI("https://upload.wikimedia.org/wikipedia/commons/7/7a/Firefox_Developer_Edition_Logo%2C_2017.svg")
        }
    }
}

class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val image = itemView.findViewById<SimpleDraweeView>(R.id.image)
}
