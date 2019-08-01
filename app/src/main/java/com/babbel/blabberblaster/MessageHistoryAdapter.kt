package com.babbel.blabberblaster

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.babbel.blabberblaster.model.Message

class MessageHistoryAdapter : RecyclerView.Adapter<MessageHistoryAdapter.MessageViewHolder>() {

    private val messageHistory = mutableListOf<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.message, parent, false) as CardView
        return MessageViewHolder(cardView)
    }

    override fun getItemCount() = messageHistory.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.cardView.setBackgroundResource(R.drawable.message_card_background)
        holder.setText(messageHistory[position].msg)

    }

    fun addMessage(msg: Message) {
        messageHistory.add(msg)
        notifyItemInserted(messageHistory.lastIndex)
    }

    class MessageViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView) {

        fun setText(text: String) {
            cardView.findViewById<TextView>(R.id.cardview_text).text = text
        }

    }

}
