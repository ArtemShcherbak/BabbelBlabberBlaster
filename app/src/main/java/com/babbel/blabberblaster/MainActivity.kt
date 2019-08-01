package com.babbel.blabberblaster

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.babbel.blabberblaster.model.Message
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.widget.Toast
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(), ViewCallback {

    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this)[ViewModel::class.java]
        viewModel.viewCallback = this

        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = viewModel.messageHistoryAdapter
                ?: MessageHistoryAdapter().apply {
                    addMessage(Message("hello", true))
                    addMessage(Message("bye", false))
                    viewModel.messageHistoryAdapter = this
                }
        }

        microphone.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
            startActivityForResult(intent, 42)
        }

        send.setOnClickListener {
            editText?.text?.let {
                if (it.isNotEmpty()) {
                    sendMessage(it.toString())
                    it.clear()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null && requestCode == 42) {
            val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            sendMessage(results[0])
        }
    }

    private fun sendMessage(content: String) {
        viewModel.messageHistoryAdapter?.addMessage(Message(content, false))
        viewModel.sendMessage(content)
        scrollToLastMessage()
    }

    override fun scrollToLastMessage() {
        recycler_view.post {
            (viewModel.messageHistoryAdapter?.itemCount)?.let { recycler_view.scrollToPosition(it - 1) }
        }
    }
}

interface ViewCallback {
    fun scrollToLastMessage()
}
