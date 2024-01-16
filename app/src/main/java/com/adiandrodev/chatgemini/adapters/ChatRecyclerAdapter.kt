package com.adiandrodev.chatgemini.adapters

import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.adiandrodev.chatgemini.viewmodels.Chat
import com.adiandrodev.chatgemini.databinding.ChatRecyclerItemBinding

class ChatRecyclerAdapter(
    private val chats: ArrayList<Chat> = ArrayList(),
    private val displayMetrics: DisplayMetrics
): RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder>() {

    class ViewHolder(binding: ChatRecyclerItemBinding): RecyclerView.ViewHolder(binding.root) {
        val modelLayout = binding.modelChatLayout
        val userLayout = binding.youChatLayout
        val modelText = binding.modelMessageTextView
        val userText = binding.youMessageTextView
        val modelErrorText = binding.errorMessageTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ChatRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = chats.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chats[position]
        if (chat.errorMessage.isEmpty()) {
            holder.modelErrorText.visibility = View.GONE
        } else {
            holder.modelErrorText.visibility = View.VISIBLE
        }
        if (chat.prompt.isEmpty()) {
            holder.modelText.visibility = View.GONE
        } else {
            holder.modelText.visibility = View.VISIBLE
        }
        if (chat.role == Chat.ROLE_YOU) {
            holder.userLayout.visibility = View.VISIBLE
            holder.modelLayout.visibility = View.GONE
        } else {
            holder.userLayout.visibility = View.GONE
            holder.modelLayout.visibility = View.VISIBLE
        }

        holder.modelText.text = chat.prompt
        holder.userText.text = chat.prompt
        holder.modelErrorText.text = chat.errorMessage

        val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        if (position == 0) {
            layoutParams.setMargins(0.toPixels(), 150.toPixels(), 0.toPixels(), 0.toPixels())
            holder.itemView.layoutParams = layoutParams
        } else {
            layoutParams.setMargins(0.toPixels(), 0.toPixels(), 0.toPixels(), 0.toPixels())
            holder.itemView.layoutParams = layoutParams
        }
    }

    private fun Int.toPixels(): Int {
        val displayMetrics = displayMetrics
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            displayMetrics
        ).toInt()
    }
}