package com.ritorno.webverse.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ritorno.webverse.R

class DashboardRecyclerViewAdapter( private val listener:DashboardFragment): RecyclerView.Adapter<DashboardViewHolder>() {

    val items: ArrayList<nft_item> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.nft_item , parent , false )
        val viewholder = DashboardViewHolder(view)
        return viewholder
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {

        holder.chainName.text = items[position].chain
        holder.descreption.text = items[position].description
        holder.name.text = items[position].name
        val currentitem = items[position].imageUrl.toString()

        Glide.with(holder.itemView.context).load(currentitem).into(holder.nftImage)


    }

    override fun getItemCount(): Int {

        return  items.size
    }
    fun update(updatedNews: ArrayList<nft_item>) {
        items.clear()
        items.addAll(updatedNews)

        notifyDataSetChanged()
    }
}


class DashboardViewHolder(itemView : View): RecyclerView.ViewHolder(itemView)
{
    val chainName : TextView = itemView.findViewById(R.id.chainname)
    val name: TextView = itemView.findViewById(R.id.nft_name)
    val descreption: TextView = itemView.findViewById(R.id.nft_descreption)
    val nftImage : ImageView = itemView.findViewById(R.id.nft_image)
}