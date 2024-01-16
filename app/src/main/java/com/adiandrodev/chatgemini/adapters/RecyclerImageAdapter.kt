package com.adiandrodev.chatgemini.adapters

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adiandrodev.chatgemini.viewmodels.Image
import com.adiandrodev.chatgemini.databinding.ImagesRecyclerViewItemBinding

class RecyclerImageAdapter(
    private var imagesList: ArrayList<Image>,
    private val clickListener: (imageUri: Uri, imageIndex: Int) -> Unit
) : RecyclerView.Adapter<RecyclerImageAdapter.ViewHolder>() {

    class ViewHolder(binding: ImagesRecyclerViewItemBinding): RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ImagesRecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = imagesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageURI(imagesList[position].imageUri)
        holder.itemView.setOnClickListener {
            clickListener.invoke(imagesList[position].imageUri, position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun dataUpdated(newImageList: ArrayList<Image>) {
        imagesList = newImageList
        notifyDataSetChanged()
    }
}