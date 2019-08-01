package com.babbel.blabberblaster

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.babbel.blabberblaster.model.Message
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import androidx.lifecycle.ViewModelProviders
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ViewModel
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this)[ViewModel::class.java]

        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener {status ->
            if(status != TextToSpeech.ERROR) {
                textToSpeech.language = Locale("es", "ES")
            }
        })

        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = viewModel.messageHistoryAdapter
                ?: MessageHistoryAdapter().apply {
                    viewModel.messageHistoryAdapter = this
                }
        }

        microphone.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("es", "ES"))
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

    @SuppressLint("CheckResult")
    override fun onStart() {
        super.onStart()
        viewModel.getGreeting()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                onMessageReceived(it.text)
            }, {
                Toast.makeText(this, "An error occured", Toast.LENGTH_LONG).show()
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null && requestCode == 42) {
            val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            sendMessage(results[0])
        }
    }

    @SuppressLint("CheckResult")
    private fun sendMessage(content: String) {
        viewModel.messageHistoryAdapter?.addMessage(Message(content, false))
        (viewModel.messageHistoryAdapter?.itemCount)?.let { recycler_view.scrollToPosition(it - 1) }
        viewModel.sendMessage(content)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                onMessageReceived(it.text)
            }, {
                Toast.makeText(this, "An error occured", Toast.LENGTH_LONG).show()
            })
    }

    private fun onMessageReceived(content: String) {
        recycler_view.post {
            (viewModel.messageHistoryAdapter?.itemCount)?.let { recycler_view.scrollToPosition(it - 1) }
            textToSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}
