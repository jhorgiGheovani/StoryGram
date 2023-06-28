package com.bangkit23.storygram.ui.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangkit23.storygram.data.remote.response.ListStoryItem
import com.bangkit23.storygram.databinding.StoryItemBinding
import com.bangkit23.storygram.ui.activity.DetailsActivity
import com.bangkit23.storygram.utils.convertDate
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class StoryAdapter :
    PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storyItem = getItem(position)
        if(storyItem!=null){
            holder.bind(storyItem)
        }


    }

    class ViewHolder(private var binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(storyData: ListStoryItem){
            binding.nama.text = storyData.name
            binding.progressBarStoryItem.visibility=View.VISIBLE
            Glide.with(itemView.context)
                .load(storyData.photoUrl)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBarStoryItem.visibility=View.GONE
                        return false
                    }

                })
                .into(binding.photo)

            binding.deskripsi.text = storyData.description

            if (storyData.createdAt != null) {
                binding.tanggal.text = convertDate(storyData.createdAt)
            }

            itemView.setOnClickListener{
                val intent = Intent(itemView.context, DetailsActivity::class.java)
                intent.putExtra("id", storyData.id)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.photo, "photo"),
                        Pair(binding.nama, "name"),
                        Pair(binding.deskripsi, "desc"),
                        Pair(binding.tanggal, "date")
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }

        }
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}