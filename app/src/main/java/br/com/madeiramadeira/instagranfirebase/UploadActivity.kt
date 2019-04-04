package br.com.madeiramadeira.instagranfirebase

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_upload.*
import java.lang.Exception
import java.util.*
import android.support.annotation.NonNull
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.storage.UploadTask
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task


class UploadActivity : AppCompatActivity() {

    var selectedImage: Uri? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var dataRef: DatabaseReference
    private lateinit var mStorageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        initializeFirebase()
    }

    private fun initializeFirebase() {
        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        dataRef = firebaseDatabase.reference
        mStorageRef = FirebaseStorage.getInstance().reference
    }

    fun chooseImage(view: View) {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            openImageGallery()
        }
    }

    fun uploadPost(view: View) {

        var uuid = UUID.randomUUID()
        val imageName = "images/$uuid.jpg"
        val storageReference = mStorageRef.child(imageName)
        val uploadTask = storageReference.putFile(selectedImage!!)
        uploadTask.continueWithTask(object : Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
            override fun then(task: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                return storageReference.downloadUrl
            }
        }).addOnCompleteListener { task ->
            if (task.isComplete && task.isSuccessful) {
                val downloadUrl = task.result.toString()

                val user = mAuth.currentUser
                val userEmail = user!!.email
                val userComment = edtComment.text.toString()
                uuid = UUID.randomUUID()
                val postId = uuid.toString()
                val postReference = dataRef.child("Posts").child(postId)
                postReference.child("email").setValue(userEmail)
                postReference.child("userName").setValue("Marlon D. Rocha")
                postReference.child("comment").setValue(userComment)
                postReference.child("downloadUrl").setValue(downloadUrl)

                Toast.makeText(applicationContext, "Seu post foi enviado!", Toast.LENGTH_LONG).show()
                this.onBackPressed()
            } else
                Toast.makeText(applicationContext, task.exception?.localizedMessage, Toast.LENGTH_LONG).show()
        }

//        uploadTask.addOnSuccessListener { taskSnapshot ->
//                if (taskSnapshot.task.isSuccessful) {
//
//                    val downloadUrl = storageReference.downloadUrl.toString()
//                    val user = mAuth.currentUser
//                    val userEmail = user!!.email
//                    val userComment = edtComment.text.toString()
//                    uuid = UUID.randomUUID()
//                    val postId = uuid.toString()
//                    val postReference = dataRef.child("Posts").child(postId)
//                    postReference.child("email").setValue(userEmail)
//                    postReference.child("userName").setValue("Marlon D. Rocha")
//                    postReference.child("comment").setValue(userComment)
//                    postReference.child("downloadUrl").setValue(downloadUrl)
//
//                    Toast.makeText(applicationContext, "Seu post foi enviado!", Toast.LENGTH_LONG).show()
//                } else
//                    Toast.makeText(applicationContext, taskSnapshot.task.exception?.localizedMessage, Toast.LENGTH_LONG).show()
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
//            }

    }

    private fun openImageGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 2)
    }

    //Evento chamado após usuário aceitar permissão de acesso às imagens
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openImageGallery()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImage = data.data

            try {
                btnImage.visibility = View.GONE
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                imgPost.setImageBitmap(bitmap)
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
