package com.example.demo.utils

import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.conversation.Chat
import com.aldebaran.qi.sdk.`object`.conversation.QiChatbot
import com.aldebaran.qi.sdk.`object`.conversation.Topic
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.builder.ChatBuilder

class ChatBotBuilder(
    var qiContext: QiContext,
    var qiChatbot: QiChatbot,
    var locale : Locale
) {

    lateinit var chat : Chat
    lateinit var chatFuture: Future<Void>

    fun build() : Future<Void>{
        chat = ChatBuilder.with(qiContext)
            .withChatbot(qiChatbot)
            .withLocale(locale)
            .build()
        chatFuture = chat.async().run()
        return chatFuture
    }

    fun setQiChatBot(qiChatbot: QiChatbot){
        this.qiChatbot = qiChatbot
    }

    fun changeTopicStatus(topics : MutableList<Topic>,status: Boolean){
        topics.forEach {topic ->
            var topicStatus = this.qiChatbot.topicStatus(topic)
            topicStatus.enabled = status
        }
    }

    fun getTopics() : MutableList<Topic>{
        return this.qiChatbot.topics
    }

    fun setLocaleVariable(locale: Locale){
        this.locale = locale
        build()
    }

    @JvmName("getChat1")
    fun getChat() : Chat{
        return chat
    }

}