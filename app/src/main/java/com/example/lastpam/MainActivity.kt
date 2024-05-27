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

    //login
    private var btntoLogin: Button? = null
    private var btntoDaftar: Button? = null
    private var etEmailLogin: EditText? = null
    private var etPassLogin: EditText? = null
    private var loginLayout: ConstraintLayout? = null

    //daftar
    private var etNama: EditText? = null
    private var etEmailDaftar: EditText? = null
    private var etPassDaftar: EditText? = null
    private var btnDaftar: Button? = null
    private var daftarLayout: ConstraintLayout? = null
    //firebase
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginLayout = findViewById(R.id.loginLayout)
        daftarLayout = findViewById(R.id.register)
        btntoLogin = findViewById(R.id.btnLogin)
        btntoDaftar = findViewById(R.id.btnDaftar)
        btnDaftar = findViewById(R.id.btnDaftarBaru)
        etEmailLogin = findViewById(R.id.etEmail)
        etPassLogin = findViewById(R.id.etPass)
        etNama = findViewById(R.id.etNameDaftar)
        etEmailDaftar = findViewById(R.id.etEmailDaftar)
        etPassDaftar = findViewById(R.id.etPassDaftar)
        auth = FirebaseAuth.getInstance()
        btntoLogin!!.setOnClickListener(){
            login(etEmailLogin!!.text.toString(), etPassLogin!!.text.toString())
        }
        btntoDaftar!!.setOnClickListener(){
            loginLayout?.visibility = View.GONE
            daftarLayout?.visibility = View.VISIBLE
        }
        btnDaftar!!.setOnClickListener(){
            daftar(etNama!!.text.toString(),etEmailDaftar!!.text.toString(),etPassDaftar!!.text.toString())
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth!!.currentUser
        if (currentUser != null) {
            updateUI(currentUser)
        } else {
            loginLayout?.visibility = View.VISIBLE
            daftarLayout?.visibility = View.GONE
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
                            Toast.makeText(this@MainActivity, "Berhasil Daftar", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@MainActivity, "Gagal Daftar", Toast.LENGTH_SHORT).show()
                        }
                    }
                    updateUI(user)
                } else {
                    Log.w(
                        ContentValues.TAG,
                        "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this@MainActivity,
                        task.exception.toString(),
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this@MainActivity,
                Home::class.java)
            startActivity(intent)
        } else {
            loginLayout?.visibility = View.VISIBLE
            daftarLayout?.visibility = View.GONE
            Toast.makeText(this@MainActivity, "Login Dulu",
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
                        "Authentication failed.",
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