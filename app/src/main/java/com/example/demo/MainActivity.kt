package com.example.demo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.widget.Button
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
import com.example.demo.databinding.ActivityMainBinding
import com.example.demo.utils.ChatBotBuilder
import com.example.demo.utils.downloadPic
import com.example.demo.utils.makePlaceHolder
import com.example.demo.viewmodel.ImageViewModel
import kotlinx.coroutines.*
import java.net.URL

class MainActivity : RobotActivity(),RobotLifecycleCallbacks {

    private lateinit var binding: ActivityMainBinding
    private lateinit var chat: Chat
    private lateinit var qiContext : QiContext
    private lateinit var demo : Topic
    private lateinit var dialog1 : Topic
    private lateinit var sad : Topic
    private lateinit var qichatbot: QiChatbot
    private lateinit var imageViewModel: ImageViewModel
    lateinit var handler : Handler
    lateinit var runnable : Runnable

    // private lateinit var editablePhraseSet: EditablePhraseSet
    val locale = Locale(Language.TURKISH, Region.TURKEY)

    var imageList = arrayListOf<ImageModel>()
    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        QiSDK.register(this,this)
        imageViewModel = ViewModelProviders.of(this).get(ImageViewModel::class.java)

        binding.nextButton.isEnabled = false
        //observeLiveData()
        //binding.imageView.downloadPic("http://isguzaktanegitim.net/pepper/ayse.png", makePlaceHolder(this@MainActivity))

        binding.nextButton.setOnClickListener {
            index++
            if(index == (imageList.size-1)){
                index = 0

            }
            val imageModel = imageList[index]
            println("question:"+imageModel.question)
            binding.questionText.setText(imageModel.question)
            binding.imageView.downloadPic(imageModel.imageUrl, makePlaceHolder(this))
            chat.async().addOnHeardListener {phrase->
                heardAndEvaluate(imageModel,phrase)
            }
        }

    }

    fun saySomething(sayWhat : String){
        runBlocking {
            launch {
                val phrase: Phrase = Phrase(sayWhat)
                val say: Say = SayBuilder.with(qiContext)
                    .withPhrase(phrase)
                    .build()
                println("say something:"+sayWhat)
                say.async().run()
            }

        }

    }

    fun observeLiveData(givenChat : Chat){

        imageViewModel.images.observe(this, Observer {images->
            //println(images)
            images.let{

                imageList.addAll(images)
                binding.nextButton.isEnabled = true
                binding.questionText.text = images[index].question
                binding.imageView.downloadPic(images[index].imageUrl, makePlaceHolder(this))

                givenChat.async().addOnHeardListener {phrase->
                    heardAndEvaluate(images[index],phrase)
                }
                /*
                    images.forEach {image->
                        runBlocking {

                            //binding.imageView.setImageResource(0);
                            delay(100)
                            println("url: "+image.imageUrl)

                            delay(100)
                            println("label:"+image.label)
                            var bool = false

                            if (bool){
                                delay(-10000)
                            }
                            delay(10000)


                        }
                    }


                 */

                }

            }
        )
    }
    fun heardAndEvaluate(image : ImageModel,phrase: Phrase){

            println(phrase.text)
            if (phrase.text.toLowerCase().equals(image.label.toLowerCase())){
                runOnUiThread {
                    showPopup(image.label)
                }
                saySomething("Doğru")
            }else{
                saySomething("Yanlış")
            }

    }

    override fun onRobotFocusGained(qiContext: QiContext?) {
        this.qiContext = qiContext!!
        listen(qiContext)

    }

    override fun onRobotFocusLost() {
        //chat.removeAllOnHeardListeners()
    }

    override fun onRobotFocusRefused(reason: String?) {

    }

    fun listen(qiContext: QiContext?){

        createQichatBotWithTopics()

        val chatBotBuilder = ChatBotBuilder(qiContext!!,qichatbot,locale)
        var chatFuture = chatBotBuilder.build()
        chat = chatBotBuilder.chat

        fillDynamicConcept("greetings", mutableListOf(Phrase("Merhaba")))

        chatBotBuilder.changeTopicStatus(mutableListOf(dialog1,demo),false)

        runOnUiThread {
            runBlocking {
                imageViewModel.getAllData()
                observeLiveData(chatBotBuilder.chat)
            }
        }
        println("listen")
    }

    fun fillDynamicConcept(conceptName : String,phraseList : MutableList<Phrase>){
        var editablePhraseSet: EditablePhraseSet = this.qichatbot.dynamicConcept(conceptName)
        editablePhraseSet.addPhrases(phraseList)
    }

    fun createQichatBotWithTopics(){
        sad = TopicBuilder.with(this.qiContext)
            .withResource(R.raw.sad)
            .build()
        demo = TopicBuilder.with(this.qiContext)
            .withResource(R.raw.demo)
            .build()
        dialog1= TopicBuilder.with(this.qiContext)
            .withResource(R.raw.dialog1)
            .build()

        qichatbot = QiChatbotBuilder.with(this.qiContext)
            .withTopics(mutableListOf(demo,dialog1,sad))
            .withLocale(locale)
            .build()
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

    fun showPopup(label : String){//emotion paramater
        val parent = binding.parentLayout
        val view : View = View.inflate(this,R.layout.pop_up_layout,null)
        val happyIv : ImageView = view.findViewById(R.id.pop_up_imageview_happy)
        val sadIv : ImageView = view.findViewById(R.id.pop_up_imageview_sad)

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(view,width,height,false)
        popupWindow.showAtLocation(parent,Gravity.CENTER,0,0)

        binding.nextButton.isEnabled = false

        happyIv.setOnClickListener {
            if (label.equals("mutlu")){
                //saySomething("Doğru Cevap")
                popupWindow.dismiss()
                binding.nextButton.isEnabled = true
            }else{
                //saySomething("Yanlış Cevap")
                popupWindow.dismiss()
                binding.nextButton.isEnabled = true
            }
        }

        sadIv.setOnClickListener {
            if(label.equals("üzgün")){
                //saySomething("Doğru Cevap")
                popupWindow.dismiss()
                binding.nextButton.isEnabled = true

            }else{
                //saySomething("Yanlış Cevap")
                popupWindow.dismiss()
                binding.nextButton.isEnabled = true

            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        QiSDK.unregister(this)
    }
}