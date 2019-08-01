package com.babbel.blabberblaster

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.babbel.blabberblaster.model.Message
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType


class ViewModel : ViewModel() {

    val okHttpClient = OkHttpClient()
    private val Url = "http://10.202.248.22:8989"
    val JSON = "application/json; charset=utf-8".toMediaType()
    val gson = Gson()


    var viewCallback: ViewCallback? = null
    var messageHistoryAdapter: MessageHistoryAdapter? = null

    @SuppressLint("CheckResult")
    fun sendMessage(content: String) {
        Single.just(
            listOf(
                "Hola, todavía no estoy trabajando",
                "¡Claro, suena bien!",
                "Totalmente de acuerdo"
            ).random()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .delay(1000, TimeUnit.MILLISECONDS)
            .subscribe { response: String ->
                messageHistoryAdapter?.addMessage(Message(response, true))
                viewCallback?.onMessageReceived(response)
            }
    }

    fun getGreeting(): Observable<StartResponse> {
        val body = RequestBody.create(JSON, "")
        val request = Request.Builder()
            .url("$Url/start")
            .post(body)
            .build()

        return Observable.fromCallable {
            gson.fromJson(okHttpClient.newCall(request).execute().body?.string(), StartResponse::class.java)
        }
    }

}

data class StartResponse(
    val text: String
)
