package com.babbel.blabberblaster

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.babbel.blabberblaster.model.Message
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ViewModel : ViewModel() {

    var viewCallback: ViewCallback? = null
    var messageHistoryAdapter: MessageHistoryAdapter? = null

    @SuppressLint("CheckResult")
    fun sendMessage(content: String) {
        Single.just(listOf("Hello, I'm not working yet", "Sure, sounds good!", "Totally agree").random())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .delay(1000, TimeUnit.MILLISECONDS)
            .subscribe { response: String ->
                messageHistoryAdapter?.addMessage(Message(response, true))
                viewCallback?.onMessageReceived(response)
            }
    }

}
