package com.example.lastpam

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class HeroAdapter(private val context: Context, private val data: List<Movies>):
    RecyclerView.Adapter<HeroAdapter.HeroViewHolder>() {
    class HeroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsImg: ImageView = itemView.findViewById(R.id.news)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.hero_item, parent, false)
        return HeroViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        val current = data[position]
        holder.newsImg.setImageResource(current.img!!)
    }
}