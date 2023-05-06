package com.example.demo.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.conversation.*
import com.aldebaran.qi.sdk.builder.ListenBuilder
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.example.demo.R
import com.example.demo.utils.ChatBotBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ConversationActivity : RobotActivity(),RobotLifecycleCallbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)
    }

    override fun onRobotFocusGained(qiContext: QiContext?) {
        runBlocking {
            launch {
                listen(qiContext)
                delay(200)
                say(qiContext,"Betül’ün oyuncak bebeği kırılmış")
                delay(200)
                say(qiContext,"merhaba")
                delay(200)
                say(qiContext,"merhaba")
                //qiContext.conversation.makeListen()
                /*
                qiContext.mainExecutor.execute {

                }
                 */
                //qiContext.focus.take()

            }
        }
    }

    fun listen(qiContext: QiContext?){
        val phraseSet: PhraseSet = PhraseSetBuilder.with(qiContext)
            .withTexts()
            .build()

        val listen: Listen = ListenBuilder.with(qiContext)
            .withPhraseSet(phraseSet)
            .withBodyLanguageOption(BodyLanguageOption.DISABLED)
            .build()

        listen.async().run()
    }

    fun say(qiContext: QiContext?,text: String){
        // Create a phrase.
        val phrase: Phrase = Phrase(text)

        val say: Say = SayBuilder.with(qiContext)
            .withPhrase(phrase)
            .build()

        say.run()
    }

    override fun onRobotFocusLost() {
        TODO("Not yet implemented")
    }

    override fun onRobotFocusRefused(reason: String?) {
        TODO("Not yet implemented")
    }
}
