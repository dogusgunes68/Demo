package com.example.demo.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.actuation.*
import com.aldebaran.qi.sdk.`object`.conversation.*
import com.aldebaran.qi.sdk.`object`.geometry.Transform
import com.aldebaran.qi.sdk.`object`.locale.Language
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.`object`.locale.Region
import com.aldebaran.qi.sdk.builder.*
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.example.demo.ImageModel
import com.example.demo.R
import com.example.demo.databinding.ActivityMainBinding
import com.example.demo.sqlite.ImageDbHelper
import com.example.demo.utils.ChatBotBuilder
import com.example.demo.utils.GreetingChatbot
import com.example.demo.utils.downloadPic
import com.example.demo.utils.makePlaceHolder
import com.example.demo.viewmodel.ImageViewModel
import kotlinx.coroutines.*

class MainActivity : RobotActivity(),RobotLifecycleCallbacks {

    private lateinit var binding: ActivityMainBinding
    private lateinit var chat: Chat
    private lateinit var qiContext : QiContext
    private lateinit var qichatbot: QiChatbot
    private lateinit var imageViewModel: ImageViewModel
    //private lateinit var dbHelper : ImageDbHelper
    private lateinit var betul : Topic
    private lateinit var emreAraba : Topic
    private lateinit var emreTop : Topic
    private lateinit var emreUcurtma : Topic
    private lateinit var variable : QiChatVariable
    var started = false

    // private lateinit var editablePhraseSet: EditablePhraseSet
    val locale = Locale(Language.TURKISH, Region.TURKEY)

    var imageList = arrayListOf<ImageModel>()
    var index = 0
    lateinit var currentModel : ImageModel
    lateinit var data: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        QiSDK.register(this,this)
        imageViewModel = ViewModelProviders.of(this).get(ImageViewModel::class.java)

        binding.imageView.visibility = View.INVISIBLE
        binding.sayStartText.visibility = View.VISIBLE
        data = intent.getStringExtra("data").toString()

        imageViewModel.deleteAllImages()
        imageViewModel.storeInSQLite()

        //observeLiveData()
        //binding.imageView.downloadPic("http://isguzaktanegitim.net/pepper/ayse.png", makePlaceHolder(this@MainActivity))
        /*
        if (imageList.isNotEmpty()) {
            currentModel = imageList[index]
        }

         */
/*
        binding.nextButton.setOnClickListener {
            index++
            if(index == (imageList.size-1)){
                index = 0
            }
            currentModel = imageList[index]
            println("question:"+currentModel.question)

            binding.questionText.setText(currentModel.question)
            binding.imageView.downloadPic(currentModel.imageUrl, makePlaceHolder(this@MainActivity))

        }
        */

    }
/*
    fun saySomething(sayWhat : String){
        runCatching {
            val phrase: Phrase = Phrase(sayWhat)
            val say: Say = SayBuilder.with(qiContext)
                .withPhrase(phrase)
                .build()
            println("say something:"+sayWhat)
            say.run()
        }

    }

 */

    fun observeLiveData(givenChat : Chat){

        imageViewModel.image.observe(this, Observer { image->
            println(image)

            binding.imageView.downloadPic(image.imageUrl, makePlaceHolder(this))

            givenChat.async().addOnHeardListener {phrase->
                heardAndEvaluate(image,phrase)
            }

        })

        /*
        imageViewModel.images.observe(this, Observer {images->
            //println(images)
            println(images)
            images.let{

                imageList.clear()
                imageList.addAll(images)
                binding.nextButton.isEnabled = true
                binding.questionText.text = images[index].question
                binding.imageView.downloadPic(images[index].imageUrl, makePlaceHolder(this))

                givenChat.async().addOnHeardListener {phrase->
                    heardAndEvaluate(images[index],phrase)
                }
            }

        })

         */
    }

    fun heardAndEvaluate(image : ImageModel, phrase: Phrase){

        println(phrase.text)

        if (phrase.text.toLowerCase().equals("başla")){
            runOnUiThread {
                binding.imageView.visibility = View.VISIBLE
                binding.sayStartText.visibility = View.INVISIBLE
            }
        }

        if (phrase.text.toLowerCase().equals(image.label.toLowerCase())){
            /*
            runOnUiThread {
                showPopup(image.label.toLowerCase())
            }

             */
        }

    }

    override fun onRobotFocusGained(qiContext: QiContext?) {
        this.qiContext = qiContext!!

        listen(qiContext)

    }

    override fun onRobotFocusLost() {
        chat.removeAllOnHeardListeners()
    }

    override fun onRobotFocusRefused(reason: String?) {
        println("refused:${reason}")
    }


    fun listen(qiContext: QiContext?){

        createQichatBotWithTopics(data)

        val chatBotBuilder = ChatBotBuilder(qiContext!!,qichatbot,locale)
        var chatFuture = chatBotBuilder.build()

        qichatbot.addOnEndedListener { endReason ->
            chatFuture.requestCancellation()
        }

        chat = chatBotBuilder.chat

        runOnUiThread {
            runBlocking {
                imageViewModel.getImageFromSqlite(data)
                observeLiveData(chat)
            }
        }

        runBlocking {
            launch {
                delay(300)
                chat.addOnSayingChangedListener {
                    if(it != null && it.text !== null && it.text != "" && it.text != " "){
                        //println(chat.saying.text.toLowerCase().contains("doğru cevap"))
                        var condition : Boolean = chat.saying.text.toLowerCase().contains(data + " kendini üzgün hisseder") || chat.saying.text.toLowerCase().contains("doğru cevap") || chat.saying.text.toLowerCase().contains("aferin")
                        println("c:"+condition)
                        if (condition){
                            println(condition)
                            runOnUiThread {
                                val intent = Intent(this@MainActivity,StartActivity::class.java)
                                intent.putExtra("data",data)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }

                }
            }
        }

        println("listen")
    }


    private fun assignVariable(value: Boolean) {
        // Set the value.
        variable?.async()?.setValue(value.toString())
    }


    fun fillDynamicConcept(conceptName : String,phraseList : MutableList<Phrase>){
        var editablePhraseSet: EditablePhraseSet = this.qichatbot.dynamicConcept(conceptName)
        editablePhraseSet.addPhrases(phraseList)
    }

    fun createQichatBotWithTopics(name : String){
        //val names = arrayOf("Betul Bebek","Emre Araba","Emre Top","Emre Uçurtma")
        println("name:${name}")
        when(name){
            "Betul Bebek" -> {
                betul = TopicBuilder.with(this.qiContext)
                    .withResource(R.raw.betul)
                    .build()

                qichatbot = QiChatbotBuilder.with(this.qiContext)
                    .withTopics(mutableListOf(betul))
                    .withLocale(locale)
                    .build()
                data = "betul"
            }
            "Emre Araba" ->{
                emreAraba = TopicBuilder.with(this.qiContext)
                    .withResource(R.raw.emre_araba)
                    .build()

                qichatbot = QiChatbotBuilder.with(this.qiContext)
                    .withTopics(mutableListOf(emreAraba))
                    .withLocale(locale)
                    .build()
                data = "emreAraba"

            }
            "Emre Top" ->{
                emreTop = TopicBuilder.with(this.qiContext)
                    .withResource(R.raw.emre_top)
                    .build()

                qichatbot = QiChatbotBuilder.with(this.qiContext)
                    .withTopics(mutableListOf(emreTop))
                    .withLocale(locale)
                    .build()
                data = "emreTop"

            }
            "Emre Uçurtma"->{
                emreUcurtma = TopicBuilder.with(this.qiContext)
                    .withResource(R.raw.emre_ucurtma)
                    .build()

                qichatbot = QiChatbotBuilder.with(this.qiContext)
                    .withTopics(mutableListOf(emreUcurtma))
                    .withLocale(locale)
                    .build()
                data = "emreUcurtma"

            }
        }

    }

    fun shakeHead(qiContext: QiContext?){

        val actuation: Actuation = qiContext!!.actuation
        val baseFrame : Frame = actuation.gazeFrame()

        val transform1: Transform = TransformBuilder.create().fromXTranslation(1.0)

        val transform2: Transform = TransformBuilder.create().fromXTranslation(5.0)

        //val targetFrame: Frame = baseFrame.makeAttachedFrame(transform2).frame()
        val gazeFrame : Frame = actuation.gazeFrame()
        val attachedFrame : AttachedFrame = gazeFrame.makeAttachedFrame(transform1)
        attachedFrame.update(transform2)
        gazeFrame.computeTransform(attachedFrame.frame())


        /*
        val mapping = qiContext.mapping
        val freeFrame: FreeFrame = mapping.makeFreeFrame()
        val baseFrame: Frame = ...
        val transform: Transform = ...
        freeFrame.update(baseFrame, transform, timestamp)
         */

    }

    override fun onDestroy() {
        super.onDestroy()
        QiSDK.unregister(this)
        //dbHelper.close()
    }

    fun showPopup(label : String){//emotion paramater
        val parent = binding.parentLayout
        val view : View = View.inflate(this, R.layout.pop_up_layout,null)
        val happyIv : ImageView = view.findViewById(R.id.pop_up_imageview_happy)
        val sadIv : ImageView = view.findViewById(R.id.pop_up_imageview_sad)

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(view,width,height,false)
        popupWindow.showAtLocation(parent,Gravity.CENTER,0,0)

        //binding.nextButton.isEnabled = false

        happyIv.setOnClickListener {
            if (label.equals("mutlu")){
                //saySomething("Doğru Cevap")
                popupWindow.dismiss()
                val intent = Intent(this,StartActivity::class.java)
                startActivity(intent)
               // binding.nextButton.isEnabled = true
            }else{
                //saySomething("Yanlış Cevap")
                //popupWindow.dismiss()
                //binding.nextButton.isEnabled = false
            }
        }

        sadIv.setOnClickListener {
            if(label.equals("üzgün")){
                //saySomething("Doğru Cevap")
                popupWindow.dismiss()
                val intent = Intent(this,StartActivity::class.java)
                startActivity(intent)
               //binding.nextButton.isEnabled = true
            }else{
                //saySomething("Yanlış Cevap")
                //popupWindow.dismiss()
                //binding.nextButton.isEnabled = false
            }
        }
    }
}