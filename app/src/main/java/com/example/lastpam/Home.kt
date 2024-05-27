package com.example.lastpam

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Home : AppCompatActivity() {
    private lateinit var rvTrending: RecyclerView
    private lateinit var new: RecyclerView
    private lateinit var top: RecyclerView
    private lateinit var adapter: MovieAdapter
    private lateinit var newsAdapter: HeroAdapter
    private lateinit var news: RecyclerView
    private lateinit var upImage: ImageButton
    private lateinit var upTitle: EditText
    private lateinit var upDesc: EditText
    private lateinit var upGenre: EditText
    private lateinit var upDate: EditText
    private lateinit var upKategori: EditText
    private lateinit var addBtn: TextView
    private lateinit var sc:ScrollView
    private lateinit var formAdd : LinearLayout
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val data = listOf(
            Movies("EndGame", R.drawable.satu),
            Movies("Age Of Ultron", R.drawable.dua),
            Movies("Infinity War", R.drawable.tiga),
            Movies("CivilWar", R.drawable.empat)
        )

        var news1 = listOf(
            Movies("EndGame", R.drawable.news1),
            Movies("Age Of Ultron", R.drawable.news2),
            Movies("Infinity War", R.drawable.news3),
            Movies("CivilWar", R.drawable.news3)
        )

        rvTrending = findViewById(R.id.rvTrending)
        top = findViewById(R.id.rvUpcoming)
        new = findViewById(R.id.rvPlay)
        news = findViewById(R.id.rvNews)
        upImage = findViewById(R.id.uploadImg)
        sc = findViewById(R.id.sc)
        formAdd = findViewById(R.id.formAdd)
        addBtn = findViewById(R.id.add)
        upImage = findViewById(R.id.uploadImg)


        rvTrending.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        rvTrending.adapter = MovieAdapter(this,data)
        top.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        top.adapter = MovieAdapter(this,data)
        new.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        new.adapter = MovieAdapter(this,data)
        rvTrending.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        rvTrending.adapter = MovieAdapter(this,data)

        news.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        news.adapter = HeroAdapter(this,news1)

        addBtn.setOnClickListener(){
            sc.visibility = View.GONE
            formAdd.visibility = View.VISIBLE
        }

        upImage.setOnClickListener(){
            val intent: Intent = Intent(Intent.ACTION_PICK)
            intent.setType("image/*")
            startActivity(intent)
        }







    }
}