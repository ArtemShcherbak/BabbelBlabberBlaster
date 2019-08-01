package com.babbel.blabberblaster

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    private lateinit var speechRecognizer: SpeechRecognizer
    private val recognitionListener = object: RecognitionListener {
        override fun onReadyForSpeech(p0: Bundle?) {}

        override fun onRmsChanged(p0: Float) {}

        override fun onBufferReceived(p0: ByteArray?) {}

        override fun onPartialResults(p0: Bundle?) {}

        override fun onEvent(p0: Int, p1: Bundle?) {}

        override fun onBeginningOfSpeech() {}

        override fun onEndOfSpeech() {}

        override fun onError(p0: Int) {
            microphone.setColorFilter(Color.BLACK)
        }

        override fun onResults(data: Bundle?) {
            microphone.setColorFilter(Color.BLACK)
            data?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let {
                sendMessage(it[0])
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this)[ViewModel::class.java]

        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener {status ->
            if(status != TextToSpeech.ERROR) {
                textToSpeech.language = Locale("es", "ES")
            }
        })

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(recognitionListener)

        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = viewModel.messageHistoryAdapter
                ?: MessageHistoryAdapter().apply {
                    viewModel.messageHistoryAdapter = this
                }
        }

        microphone.setOnClickListener {
            microphone.setColorFilter(Color.RED)
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("es", "ES"))
            speechRecognizer.startListening(intent)
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 42)
        } else{
            viewModel.getGreeting()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onMessageReceived(it.text)
                }, {
                    Toast.makeText(this, "An error occured", Toast.LENGTH_LONG).show()
                })
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            42 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    viewModel.getGreeting()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            onMessageReceived(it.text)
                        }, {
                            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                        })
                } else {
                    finish()
                }
                return
            }
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
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            })
    }

    private fun onMessageReceived(content: String) {
        recycler_view.post {
            viewModel.messageHistoryAdapter?.addMessage(Message(content, true))
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
