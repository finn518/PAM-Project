package com.example.lastpam

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MovieAdapter(private val context: Context, private val movieData: MutableList<Movies>): RecyclerView.Adapter<MovieAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img: ImageView = itemView.findViewById(R.id.poster)
        var title: TextView = itemView.findViewById(R.id.movieTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return movieData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = movieData[position]
        holder.title.text = current.title
        Glide.with(context).load(current.img).into(holder.img)
        holder.itemView.setOnClickListener(){
            val intent = Intent(context,Detail::class.java)
            intent.putExtra("img", movieData.get(holder.adapterPosition).img)
            intent.putExtra("title", movieData.get(holder.adapterPosition).title)
            intent.putExtra("key", movieData.get(holder.adapterPosition).id)
            intent.putExtra("desc", movieData.get(holder.adapterPosition).desc)
            intent.putExtra("tayang", movieData.get(holder.adapterPosition).tayang)
            intent.putExtra("genre", movieData.get(holder.adapterPosition).genre)
            context.startActivity(intent)
        }
    }
}