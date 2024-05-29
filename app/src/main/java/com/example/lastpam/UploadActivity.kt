package com.example.lastpam

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UploadActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    val pilihan = arrayOf("trending", "playing", "upcoming")

    private var upImage: ImageButton? = null
    private var upJudul: EditText? = null
    private var upDesc : EditText? = null
    private var upGenre: EditText? = null
    private var upTayang: EditText? = null
    private var upKategori: Spinner? = null
    private var save: Button? = null
    private var uri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        upImage = findViewById(R.id.uploadImg)
        upJudul = findViewById(R.id.uploadTitle)
        upGenre = findViewById(R.id.uploadGenre)
        upTayang = findViewById(R.id.uploadDate)
        upKategori = findViewById(R.id.kategori)
        upDesc = findViewById(R.id.uploadDesc)
        save = findViewById(R.id.uploadBtn)

        upKategori?.onItemSelectedListener = this
        val kategoriAdapter: ArrayAdapter<String> = ArrayAdapter(this,android.R.layout.simple_spinner_item,pilihan)

        kategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        upKategori?.adapter = kategoriAdapter


        upImage?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        save?.setOnClickListener(){
            savedData()
        }

    }

    private fun savedData() {
        val storage: StorageReference = FirebaseStorage.getInstance().getReference().child("upload").child(uri?.lastPathSegment!!)
        storage.putFile(uri!!)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    val urlImage = uri.toString()
                    uploadData(urlImage)
                }.addOnFailureListener {
                    Toast.makeText(this, "Gagal mendapatkan URL gambar", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadData(urlImage: String) {
        val judul: String = upJudul!!.text.toString().trim()
        val desc: String = upDesc!!.text.toString().trim()
        val genre: String = upGenre!!.text.toString().trim()
        val waktuTayang: String = upTayang!!.text.toString().trim()
        val kategori: String = upKategori!!.selectedItem.toString().trim()

        if (judul.isEmpty() || desc.isEmpty() || genre.isEmpty() || waktuTayang.isEmpty() || kategori.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val waktuTayangConvert = convertDateFormat(waktuTayang) ?: run {
            Toast.makeText(this, "Format tanggal tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance("https://projectpam-107dc-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val key = database.getReference("Trending").push().key

        val new = Movies(
            id = key,
            title = judul,
            desc = desc,
            genre = genre,
            tayang = waktuTayangConvert,
            img = urlImage,
            kategori = kategori
        )

        key?.let {
            database.getReference(kategori).child(it).setValue(new).addOnSuccessListener {
                Toast.makeText(this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Data gagal ditambahkan", Toast.LENGTH_SHORT).show()
            }
        }

        // Clear input fields
        upImage!!.setImageResource(0)
        upJudul!!.text.clear()
        upDesc!!.text.clear()
        upGenre!!.text.clear()
        upTayang!!.text.clear()
        upKategori!!.setSelection(0)
    }


    fun convertDateFormat(inputDate: String): String? {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) // Locale Indonesia
            val date: Date? = inputFormat.parse(inputDate)
            date?.let {
                outputFormat.format(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            uri = data.data
            uri?.let {
                upImage?.setImageURI(it)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}