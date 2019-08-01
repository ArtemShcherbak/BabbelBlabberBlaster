package com.babbel.blabberblaster

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.babbel.blabberblaster.model.Message

class MessageHistoryAdapter : RecyclerView.Adapter<MessageHistoryAdapter.MessageViewHolder>() {

    private val messageHistory = mutableListOf<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val linearLayout = LayoutInflater.from(parent.context).inflate(R.layout.message, parent, false) as LinearLayout
        return MessageViewHolder(linearLayout)
    }

    override fun getItemCount() = messageHistory.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        with(messageHistory[position]) {
            if (isIncoming) {
                holder.setCardBackground(R.drawable.message_card_background_incoming)
                holder.alignToTheStart()
            } else {
                holder.setCardBackground(R.drawable.message_card_background_outcoming)
                holder.alignToTheEnd()
            }
            holder.setText(msg)
        }
    }

    fun addMessage(msg: Message) {
        messageHistory.add(msg)
        notifyItemInserted(messageHistory.lastIndex)
    }

    fun clear() {
        messageHistory.clear()
        notifyDataSetChanged()
    }

    class MessageViewHolder(private val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout) {

        fun setText(text: String) {
            linearLayout.findViewById<TextView>(R.id.cardview_text).text = text
        }

        fun setCardBackground(id: Int) {
            linearLayout.findViewById<CardView>(R.id.cardview).setBackgroundResource(id)
        }

        fun alignToTheStart() {
            linearLayout.findViewById<LinearLayout>(R.id.ll_cardview).gravity = Gravity.START
        }

        fun alignToTheEnd() {
            linearLayout.findViewById<LinearLayout>(R.id.ll_cardview).gravity = Gravity.END
        }
    }

}
