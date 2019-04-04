package br.com.madeiramadeira.instagranfirebase

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

/**
 * Created by Marlon D. Rocha on 04/04/2019.
 */
class FeedAdapter(val context: Context): RecyclerView.Adapter<FeedAdapter.PostViewHolder>() {
    private var posts: ArrayList<Post>? = null

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): PostViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.feed_item, parent, false)
        return PostViewHolder(view)
    }

    override fun getItemCount() = if (posts != null) posts!!.size else 0

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts!![position]
        holder.txtUser.text = post.user
        holder.txtComment.text = post.comment
        Picasso.get().load(post.imageUrl).into(holder.imgPost)

        holder.imgLike.setOnClickListener {
            (it as ImageView).setImageResource(R.drawable.ic_favorite)
        }
    }

    fun setPosts(posts: ArrayList<Post>) {
        this.posts = posts
        notifyDataSetChanged()
    }

    class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val txtUser = itemView.findViewById<TextView>(R.id.txtUser)
        val imgPost = itemView.findViewById<ImageView>(R.id.imgPost)
        val imgLike = itemView.findViewById<ImageView>(R.id.imgLike)
        val txtComment = itemView.findViewById<TextView>(R.id.txtComment)
    }
}