package com.example.lastpam

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.github.clans.fab.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class Detail : AppCompatActivity() {

    private var detailImg: ImageView? = null
    private var detailTitle: TextView? = null
    private var detailDesc: TextView? = null
    private var detailGenre: TextView? = null
    private var detailTayang: TextView? = null
    private var delBtn : FloatingActionButton? = null
    private var updateBtn: FloatingActionButton? = null
    private var key: String? = null
    private var imgUrl:String? = null
    private var kategori: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)



        detailImg = findViewById(R.id.detailImg)
        detailTitle = findViewById(R.id.detailTitle)
        detailDesc = findViewById(R.id.detailDesc)
        detailGenre = findViewById(R.id.detailGenre)
        detailTayang = findViewById(R.id.detailTayang)
        delBtn = findViewById(R.id.deleteBtn)
        updateBtn = findViewById(R.id.editBtn)

        val bun: Bundle? = intent.extras

        if (bun != null){
            detailTitle?.text = bun.getString("title")
            detailDesc?.text = bun.getString("desc")
            detailGenre?.text = bun.getString("genre")
            detailTayang?.text = bun.getString("tayang")
            kategori = bun.getString("kategori")
            key = bun.getString("key")
            imgUrl = bun.getString("img")
            if (detailImg != null && imgUrl != null) {
                Glide.with(this).load(imgUrl).into(detailImg!!)
            }
        }

        delBtn?.setOnClickListener {
            val ref: DatabaseReference = FirebaseDatabase.getInstance("https://projectpam-107dc-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference(kategori!!)
            val storage: FirebaseStorage = FirebaseStorage.getInstance()

            if (imgUrl != null && key != null) {
                Log.d("DetailActivity", "Attempting to delete item with key: $key in category: $kategori")
                Log.d("DetailActivityImg", "Image URL: $imgUrl")

                try {
                    val storageRef: StorageReference = storage.getReferenceFromUrl(imgUrl!!)

                    storageRef.delete().addOnSuccessListener {
                        Log.d("DetailActivity", "File successfully deleted from storage")
                        ref.child(key!!).removeValue().addOnSuccessListener {
                            Log.d("DetailActivity", "Item successfully deleted from database")
                            Toast.makeText(this, "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(applicationContext, Home::class.java))
                            finish()
                        }.addOnFailureListener { e ->
                            Log.e("DetailActivity", "Error removing data from database", e)
                            Toast.makeText(this, "Gagal menghapus data dari database", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener { e ->
                        Log.e("DetailActivity", "Error deleting file from storage", e)
                        Toast.makeText(this, "Gagal menghapus file dari storage", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("DetailActivity", "Exception during storage deletion", e)
                    Toast.makeText(this, "Terjadi kesalahan saat menghapus file dari storage", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "URL atau kunci tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }



        updateBtn?.setOnClickListener(){
            val intent = Intent(this, Update::class.java)
                .putExtra("title", detailTitle?.text.toString())
                .putExtra("desc", detailDesc?.text.toString())
                .putExtra("genre", detailGenre?.text.toString())
                .putExtra("tayang", detailTayang?.text.toString())
                .putExtra("img", imgUrl)
                .putExtra("key", key)
                .putExtra("kategori", kategori)
            startActivity(intent)
        }

    }
}