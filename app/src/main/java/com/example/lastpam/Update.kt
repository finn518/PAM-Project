package com.example.lastpam

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class Update : AppCompatActivity() {
    private var updateTitle: EditText? = null
    private var updateDesc: EditText? = null
    private var updateGenre: EditText? = null
    private var updateTayang: EditText? = null
    private var updateImage: ImageView? = null
    private var updateBtn: Button? = null
    private var key: String? = null
    private var oldimageUrl: String? = null
    private var uri: Uri? = null
    private var dbRef: DatabaseReference? = null
    private var stRef: StorageReference? = null
    private var kategori: String? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val TAG = "UpdateActivity"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        updateTitle = findViewById(R.id.updateTitle)
        updateDesc = findViewById(R.id.updateDesc)
        updateGenre = findViewById(R.id.updateGenre)
        updateTayang = findViewById(R.id.updateTayang)
        updateBtn = findViewById(R.id.updateBtn)
        updateImage = findViewById(R.id.updateImg)

        val bun: Bundle? = intent.extras
        if (bun != null) {
            updateTitle?.setText(bun.getString("title"))
            updateDesc?.setText(bun.getString("desc"))
            updateGenre?.setText(bun.getString("genre"))
            updateTayang?.setText(bun.getString("tayang"))
            kategori = bun.getString("kategori")
            key = bun.getString("key")
            oldimageUrl = bun.getString("img")
            if (updateImage != null && oldimageUrl != null) {
                Glide.with(this).load(oldimageUrl).into(updateImage!!)
            }
        }

        dbRef = FirebaseDatabase.getInstance("https://projectpam-107dc-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference(kategori!!).child(key!!)
        updateImage?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        updateBtn?.setOnClickListener {
            if (uri != null) {
                uploadImageAndSaveData()
            } else {
                updateData(oldimageUrl!!)
            }
        }
    }

    private fun uploadImageAndSaveData() {
        uri?.let {
            stRef = FirebaseStorage.getInstance().getReference("upload/${it.lastPathSegment}")
            stRef!!.putFile(it)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        val urlImage = uri.toString()
                        Log.d(TAG, "URL gambar baru: $urlImage")
                        updateData(urlImage)
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal mendapatkan URL gambar", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Error mendapatkan URL gambar", e)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error mengunggah gambar", e)
                }
        }
    }

    private fun updateData(urlImage: String) {
        val judul: String = updateTitle!!.text.toString().trim()
        val desc: String = updateDesc!!.text.toString().trim()
        val genre: String = updateGenre!!.text.toString().trim()
        val waktuTayang: String = updateTayang!!.text.toString().trim()

        val update = Movies(
            title = judul,
            desc = desc,
            genre = genre,
            tayang = waktuTayang,
            img = urlImage
        )

        dbRef!!.setValue(update).addOnSuccessListener {
            Log.d(TAG, "Data berhasil diperbarui di database")
            if (urlImage != oldimageUrl) {
                val ref = FirebaseStorage.getInstance().getReferenceFromUrl(oldimageUrl!!)
                ref.delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Gambar lama berhasil dihapus", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Gambar lama berhasil dihapus")
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal menghapus gambar lama", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Error menghapus gambar lama", e)
                    }
            }
            Toast.makeText(this, "Berhasil di update", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Home::class.java))
            finish()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Gagal update", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Error memperbarui data di database", e)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            uri = data.data
            uri?.let {
                updateImage?.setImageURI(it)
            }
        }
    }
}
