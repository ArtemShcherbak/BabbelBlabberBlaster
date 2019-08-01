package com.babbel.blabberblaster

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.babbel.blabberblaster.model.Message

class MessageHistoryAdapter : RecyclerView.Adapter<MessageHistoryAdapter.MessageViewHolder>() {

    private val messageHistory = mutableListOf<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val textView = LayoutInflater.from(parent.context).inflate(R.layout.message, parent, false) as TextView
        return MessageViewHolder(textView)
    }

    override fun getItemCount() = messageHistory.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.text.text = messageHistory[position].msg

    }

    fun addMessage(msg: Message) {
        messageHistory.add(msg)
        notifyItemInserted(messageHistory.lastIndex)
    }

    class MessageViewHolder(val text: TextView) : RecyclerView.ViewHolder(text) {

    }

}
