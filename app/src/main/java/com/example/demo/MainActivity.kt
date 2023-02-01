package com.example.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.Qi
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.actuation.Animate
import com.aldebaran.qi.sdk.`object`.actuation.Animation
import com.aldebaran.qi.sdk.`object`.conversation.*
import com.aldebaran.qi.sdk.`object`.holder.AutonomousAbilitiesType
import com.aldebaran.qi.sdk.`object`.holder.Holder
import com.aldebaran.qi.sdk.`object`.locale.Language
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.`object`.locale.Region
import com.aldebaran.qi.sdk.builder.*
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : RobotActivity(),RobotLifecycleCallbacks {

    private lateinit var chat: Chat
    private lateinit var editablePhraseSet: EditablePhraseSet
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        QiSDK.register(this,this)
    }

    override fun onRobotFocusGained(qiContext: QiContext?) {
        listen(qiContext)
    }

    override fun onRobotFocusLost() {
        chat.removeAllOnHeardListeners()
    }

    override fun onRobotFocusRefused(reason: String?) {
        TODO("Not yet implemented")
    }

    fun listen(qiContext:QiContext?){

        val topic: Topic = TopicBuilder.with(qiContext)
            .withResource(R.raw.demo)
            .build()
        val locale = Locale(Language.TURKISH, Region.TURKEY)

        val qichatbot: QiChatbot = QiChatbotBuilder.with(qiContext)
            .withTopic(topic)
            .withLocale(locale)
            .build()

        // Create a Chat
        chat = ChatBuilder.with(qiContext)
            .withChatbot(qichatbot)
            .withLocale(locale)
            .build()


        var editablePhraseSet: EditablePhraseSet = qichatbot.dynamicConcept("greetings")
        val phrases: MutableList<Phrase> = mutableListOf<Phrase>(Phrase("merhaba"), Phrase("selam"), Phrase("naber"))
        editablePhraseSet.addPhrases(phrases)

        // Execute the chat asynchronously
        val chatFuture: Future<Void> = chat.async()!!.run()

        chatFuture.thenConsume { future ->
            if (future.hasError()) {
                Log.e(TAG, "Discussion finished with error.", future.error)
            }
        }

        chat.addOnHeardListener {heard->
            editablePhraseSet.phrases.forEach {
                if (it.text.toString().contains(heard.text.toString())){
                    /*
                    val holder: Holder = HolderBuilder.with(qiContext)
                        .withAutonomousAbilities(AutonomousAbilitiesType.BACKGROUND_MOVEMENT)
                        .build()

                    // Hold the ability asynchronously.
                    holder.async().hold()

                    // Release the ability asynchronously.
                    holder.async().release()

                     */
                    // Create an animation object.
                    println("Girdiii")
                    val myAnimation: Animation = AnimationBuilder.with(qiContext)
                        .withResources(R.raw.dance_b001)
                        .build()

// Build the action.
                    val animate: Animate = AnimateBuilder.with(qiContext)
                        .withAnimation(myAnimation)
                        .build()

// Run the action asynchronously.
                    animate.async().run()
                }
            }
        }
        handleListenedData(qiContext)

    }

    fun handleListenedData(qiContext: QiContext?){


    }

    override fun onDestroy() {
        super.onDestroy()
        QiSDK.unregister(this)
    }
}