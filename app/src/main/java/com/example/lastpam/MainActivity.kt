package com.example.lastpam

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private var btntoLogin: Button? = null
    private var btntoDaftar: Button? = null
    private var etEmailLogin: EditText? = null
    private var etPassLogin: EditText? = null
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btntoLogin = findViewById(R.id.btnLogin)
        btntoDaftar = findViewById(R.id.btnDaftar)
        etEmailLogin = findViewById(R.id.etEmail)
        etPassLogin = findViewById(R.id.etPass)
        auth = FirebaseAuth.getInstance()

        btntoLogin!!.setOnClickListener(){
            login(etEmailLogin!!.text.toString(), etPassLogin!!.text.toString())
        }
        btntoDaftar!!.setOnClickListener(){
            val intent = Intent(this,Daftar::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth!!.currentUser
        if (currentUser != null) {
            updateUI(currentUser)
        }
    }
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this@MainActivity,
                Home::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this@MainActivity, "Daftar dulu yaa!!!",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun login(email: String?, pass: String?) {
        if (!validateForm(etEmailLogin, etPassLogin, null)) {
            return
        }
        auth!!.signInWithEmailAndPassword(email!!, pass!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG,
                        "signInWithEmail:success")
                    val user = auth!!.currentUser
                    Toast.makeText(this@MainActivity,
                        user.toString(),
                        Toast.LENGTH_SHORT).show()
                    updateUI(user)
                } else {
                    Log.w(ContentValues.TAG,
                        "signInWithEmail:failure", task.exception)
                    Toast.makeText(this@MainActivity,
                        "Login gagal harap daftar dulu",
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

}