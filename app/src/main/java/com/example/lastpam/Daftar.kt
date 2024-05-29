package com.example.lastpam

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class Daftar : AppCompatActivity() {
    private var etNama: EditText? = null
    private var etEmailDaftar: EditText? = null
    private var etPassDaftar: EditText? = null
    private var btnDaftar: Button? = null
    private var auth: FirebaseAuth? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar)

        etNama = findViewById(R.id.etNameDaftar)
        etEmailDaftar = findViewById(R.id.etEmailDaftar)
        etPassDaftar = findViewById(R.id.etPassDaftar)
        btnDaftar = findViewById(R.id.btnDaftarBaru)
        auth = FirebaseAuth.getInstance()


        btnDaftar!!.setOnClickListener(){
            daftar(etNama!!.text.toString(),etEmailDaftar!!.text.toString(),etPassDaftar!!.text.toString())
        }
    }
    private fun daftar(nama: String?, email: String?, pass: String?) {
        if (!validateForm(etEmailDaftar,etPassDaftar,etNama)){
            return
        }
        auth!!.createUserWithEmailAndPassword(email!!,pass!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(
                        ContentValues.TAG,
                        "createUserWithEmail:success")
                    val user = auth!!.currentUser
                    val userId = user!!.uid
                    val database = FirebaseDatabase.getInstance("https://projectpam-107dc-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    val userRef = database.getReference("admins").child(userId)
                    val userData = mapOf(
                        "email" to email,
                        "uid" to userId,
                        "name" to nama
                    )

                    userRef.setValue(userData).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@Daftar, "Berhasil Daftar", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@Daftar, "Gagal Daftar", Toast.LENGTH_SHORT).show()
                        }
                    }
                    updateUI(user)
                } else {
                    Log.w(
                        ContentValues.TAG,
                        "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this@Daftar,
                        task.exception.toString(),
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }
    private fun validateForm(email: EditText?, pass: EditText?, nama: EditText?): Boolean {
        var result = true
        if (TextUtils.isEmpty(email!!.text.toString())) {
            email!!.error = "Required"
            result = false
        } else {
            email!!.error = null
        }
        if (TextUtils.isEmpty(pass!!.text.toString())) {
            pass!!.error = "Required"
            result = false
        } else {
            pass!!.error = null
        }
        if (nama != null) {
            if (TextUtils.isEmpty(nama.text.toString())) {
                nama.error = "Required"
                result = false
            } else {
                nama.error = null
            }
        }
        return result
    }
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this@Daftar,
                Home::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this@Daftar,
                Daftar::class.java)
            Toast.makeText(this@Daftar, "Login Dulu",
                Toast.LENGTH_SHORT).show()
        }
    }
}