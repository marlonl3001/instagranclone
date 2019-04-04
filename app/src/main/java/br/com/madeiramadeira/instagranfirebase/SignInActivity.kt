package br.com.madeiramadeira.instagranfirebase

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        mAuth = FirebaseAuth.getInstance()
        edtLogin.setText("marlon.rocha@bulkylog.com.br")
        edtPassword.setText("123456")
    }

    fun signIn(view: View) {
        mAuth.signInWithEmailAndPassword(edtLogin.text.toString(),
            edtPassword.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val mUser = mAuth.currentUser
                    Toast.makeText(this, "Usuário: ${mUser!!.email} logado com sucesso!!", Toast.LENGTH_LONG).show()
                    val intent = Intent(applicationContext, FeedActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    fun signUp(view: View) {
        mAuth.createUserWithEmailAndPassword(edtLogin.text.toString(),
            edtPassword.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Cadastro criado com sucesso!", Toast.LENGTH_LONG).show()
                    val intent = Intent(applicationContext, FeedActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }
}
