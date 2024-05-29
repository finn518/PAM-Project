package com.example.lastpam

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Home : AppCompatActivity() {
    private lateinit var rvTrending: RecyclerView
    private lateinit var rvPlaying: RecyclerView
    private lateinit var rvUpcoming: RecyclerView
    private lateinit var trendingAdapter: MovieAdapter
    private lateinit var playingAdapter: MovieAdapter
    private lateinit var upcomingAdapter: MovieAdapter
    private lateinit var addBtn: TextView
    private var auth: FirebaseAuth? = null
    private var dataTrending: MutableList<Movies> = mutableListOf()
    private var dataUpcoming: MutableList<Movies> = mutableListOf()
    private var dataPlaying: MutableList<Movies> = mutableListOf()
    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null
    private lateinit var dbTrending: DatabaseReference
    private lateinit var dbPlaying: DatabaseReference
    private lateinit var dbUpcoming: DatabaseReference
    private var nickName: TextView? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        nickName = findViewById(R.id.userName)
        rvTrending = findViewById(R.id.rvTrending)
        rvPlaying = findViewById(R.id.rvPlay)
        rvUpcoming = findViewById(R.id.rvUpcoming)
        addBtn = findViewById(R.id.add)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase!!.reference
        dbTrending = FirebaseDatabase.getInstance("https://projectpam-107dc-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("trending")
        dbPlaying = FirebaseDatabase.getInstance("https://projectpam-107dc-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("playing")
        dbUpcoming = FirebaseDatabase.getInstance("https://projectpam-107dc-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("upcoming")

        dbTrending.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                dataTrending.clear()
                for(moviesnapshot in snapshot.children){
                    val movie = moviesnapshot.getValue(Movies::class.java)
                    movie?.id = moviesnapshot.key
                    if (movie !== null){
                        dataTrending.add(movie)
                    }
                }
                trendingAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Home, "Tidak bisa mendapatkan data", Toast.LENGTH_SHORT).show()
            }
        })

        dbPlaying.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                dataPlaying.clear()
                for(moviesnapshot in snapshot.children){
                    val movie = moviesnapshot.getValue(Movies::class.java)
                    movie?.id = moviesnapshot.key
                    if (movie !== null){
                        dataPlaying.add(movie)
                    }
                }
                playingAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Home, "Tidak bisa mendapatkan data", Toast.LENGTH_SHORT).show()
            }
        })

        dbUpcoming.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                dataUpcoming.clear()
                for(moviesnapshot in snapshot.children){
                    val movie = moviesnapshot.getValue(Movies::class.java)
                    movie?.id = moviesnapshot.key
                    if (movie !== null){
                        dataUpcoming.add(movie)
                    }
                }
                upcomingAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Home, "Tidak bisa mendapatkan data", Toast.LENGTH_SHORT).show()
            }
        })

        val userId = auth!!.currentUser?.uid
        if (userId != null) {
            val ref = FirebaseDatabase.getInstance("https://projectpam-107dc-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("admins").child(userId)

            ref.child("name").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.getValue(String::class.java)
                    nickName?.text = name
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@Home, "Cannot retrieve name", Toast.LENGTH_SHORT).show()
                }
            })
        }

        rvTrending.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        trendingAdapter = MovieAdapter(this,dataTrending)
        rvTrending.adapter = trendingAdapter
        rvPlaying.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        playingAdapter = MovieAdapter(this,dataPlaying)
        rvPlaying.adapter = playingAdapter
        rvUpcoming.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        upcomingAdapter = MovieAdapter(this,dataUpcoming)
        rvUpcoming.adapter = upcomingAdapter


        addBtn.setOnClickListener(){
            val intent: Intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }

        val keluar: ImageButton = findViewById(R.id.logout)
        keluar.setOnClickListener(){
            logOut()
        }
    }

    fun logOut() {
        auth!!.signOut()
        val intent = Intent(this@Home,
            MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}