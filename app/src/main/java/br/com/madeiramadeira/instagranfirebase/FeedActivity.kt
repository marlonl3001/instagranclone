package br.com.madeiramadeira.instagranfirebase

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_feed.*

class FeedActivity : AppCompatActivity() {

    private lateinit var dataBase: FirebaseDatabase
    private lateinit var mReference: DatabaseReference
    private lateinit var adapter: FeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        dataBase = FirebaseDatabase.getInstance()
        mReference = dataBase.reference
        adapter = FeedAdapter(this)
        recyclerItens.adapter = adapter
        fetchPosts()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.feed_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val intent = Intent(applicationContext, UploadActivity::class.java)
        startActivity(intent)

        return super.onOptionsItemSelected(item)
    }

    private fun fetchPosts() {
        val postsRef = dataBase.getReference("Posts")
        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                val itens = ArrayList<Post>()
                for(fPost in data.children) {
                    val postO = fPost.value as HashMap<String, String>
                    val post = Post(postO["userName"]!!,
                        postO["downloadUrl"]!!, postO["comment"]!!)
                    itens.add(post)
                }
                adapter.setPosts(itens)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}
