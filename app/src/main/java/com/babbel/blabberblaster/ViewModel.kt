package com.babbel.blabberblaster

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import io.reactivex.Observable
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


class ViewModel : ViewModel() {

    val okHttpClient = OkHttpClient()
    private val Url = "http://10.202.248.22:8989"
    val JSON = "application/json; charset=utf-8".toMediaType()
    val gson = Gson()

    var messageHistoryAdapter: MessageHistoryAdapter? = null

//    @SuppressLint("CheckResult")
//    fun sendMessage(content: String) {
//        Single.just(
//            listOf(
//                "Hola, todavía no estoy trabajando",
//                "¡Claro, suena bien!",
//                "Totalmente de acuerdo"
//            ).random()
//        )
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .delay(1000, TimeUnit.MILLISECONDS)
//            .subscribe { response: String ->
//                messageHistoryAdapter?.addMessage(Message(response, true))
//                viewCallback?.onMessageReceived(response)
//            }
//    }

    fun getGreeting(): Observable<StartResponse> {
        val request = Request.Builder()
            .url("$Url/start")
            .build()

        return Observable.fromCallable {
            gson.fromJson(okHttpClient.newCall(request).execute().body?.string(), StartResponse::class.java)
        }
    }

    fun sendMessage(content: String): Observable<SendMessageResponse> {
        val body = gson.toJson(SendMessageRequest(content)).toRequestBody(JSON)
        val request = Request.Builder()
            .url("$Url/send")
            .post(body)
            .build()

        return Observable.fromCallable {
            gson.fromJson(okHttpClient.newCall(request).execute().body?.string(), SendMessageResponse::class.java)
        }
    }
}

data class StartResponse(
    val text: String
)

data class SendMessageRequest(
    val text: String
)

data class SendMessageResponse(
    val text: String
)
