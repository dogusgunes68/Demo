package com.example.demo.utils

import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.conversation.*
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.builder.SayBuilder
import java.util.concurrent.ExecutionException
import kotlin.coroutines.cancellation.CancellationException

class GreetingChatbot(context: QiContext) : BaseChatbot(context) {

    private val greetings = mutableListOf<String>( "hello", "hi", "good morning", "good afternoon", "good evening" )

    override fun replyTo(phrase: Phrase, locale: Locale): StandardReplyReaction {
        return if (greetings.contains(phrase.text)) {
            StandardReplyReaction(
                MyChatbotReaction(qiContext, "Hello you"),
                ReplyPriority.NORMAL)
        } else {
            StandardReplyReaction(
                MyChatbotReaction(qiContext, "I can just greet you"),
                ReplyPriority.FALLBACK)
        }
    }

    override fun acknowledgeHeard(phrase: Phrase, locale: Locale) {
        println("acknowledgeHeard:${phrase.text}")
    }

    override fun acknowledgeSaid(phrase: Phrase, locale: Locale) {
        println("acknowledgeSaid:${phrase.text}")
    }

    class MyChatbotReaction(context: QiContext, answer: String) : BaseChatbotReaction(context) {

        private val answer: String = "answer"
        private var fsay: Future<Void>? = null

        override fun runWith(speechEngine: SpeechEngine) {

            val say: Say = SayBuilder.with(speechEngine)
                .withText(answer)
                .build()
            fsay = say.async().run()

            try {
                fsay?.get() // Do not leave the method before the actions are done
            } catch (e: ExecutionException) {
                println(e.localizedMessage)
            } catch (e: CancellationException) {
                println(e.localizedMessage)
            }
        }

        override fun stop() {
            fsay?.cancel(true)
        }
    }
}