package com.babbel.blabberblaster

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageHistoryAdapter(private val messageHistory: List<String>) : RecyclerView.Adapter<MessageHistoryAdapter.MessageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val textView = LayoutInflater.from(parent.context).inflate(R.layout.message, parent, false) as TextView
        return MessageViewHolder(textView)
    }

    override fun getItemCount() = messageHistory.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.text.text = messageHistory[position]

    }


    class MessageViewHolder(val text: TextView) : RecyclerView.ViewHolder(text) {

    }

}
