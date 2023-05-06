package com.example.demo.utils

import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.conversation.Listen
import com.aldebaran.qi.sdk.`object`.conversation.Phrase
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.`object`.locale.Language
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.`object`.locale.Region
import com.aldebaran.qi.sdk.builder.SayBuilder

class Chatting(
    val listen : Listen,

    val qiContext: QiContext
) {

    lateinit var say : Say

    fun speak(text:String){
        val phrase = Phrase(text)
        val locale: Locale = Locale(Language.TURKISH, Region.TURKEY);
        say = SayBuilder.with(qiContext)
            .withPhrase(phrase)
            .withLocale(locale)
            .build()

        say.run()
    }

    fun stopSay(){

    }

    fun checkAnswer(){

    }

    fun listen(){

    }



}