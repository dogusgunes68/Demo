package com.example.demo.view

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.conversation.*
import com.aldebaran.qi.sdk.`object`.locale.Language
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.`object`.locale.Region
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder
import com.aldebaran.qi.sdk.builder.TopicBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.bumptech.glide.load.engine.Resource
import com.example.demo.R
import com.example.demo.databinding.ActivityPopUpBinding
import com.example.demo.utils.ChatBotBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class PopUpActivity : RobotActivity(),RobotLifecycleCallbacks {
    private lateinit var qiContext : QiContext
    private lateinit var qichatbot: QiChatbot
    private lateinit var chat: Chat
    private lateinit var emre : Topic
    private lateinit var betul : Topic

    private lateinit var binding : ActivityPopUpBinding

    val locale = Locale(Language.TURKISH, Region.TURKEY)
    lateinit var data: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pop_up)
        binding = ActivityPopUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        QiSDK.register(this,this)
        binding.imageView.visibility = View.INVISIBLE
        binding.imageviewHappy.visibility = View.INVISIBLE
        binding.imageviewSad.visibility = View.INVISIBLE
        data = intent.getStringExtra("data").toString()

        //"Emre Tavşan","Emre Kuş","Betül Hasta","Emre Pasta"
        when(data){

            "Emre Tavşan" ->{
                binding.imageView.setImageBitmap(decodeSampledBitmapFromResource(resources,R.drawable.tavsan,350,250))
            }
            "Emre Kuş" ->{
                binding.imageView.setImageBitmap(decodeSampledBitmapFromResource(resources,R.drawable.kus,350,250))
            }
            "Betül Hasta" ->{
                binding.imageView.setImageBitmap(decodeSampledBitmapFromResource(resources,R.drawable.hasta,350,250))
            }
            "Emre Pasta" ->{
                binding.imageView.setImageBitmap(decodeSampledBitmapFromResource(resources,R.drawable.pasta,350,250))
            }

        }
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize

    }
    fun decodeSampledBitmapFromResource(
        res: Resources,
        resId: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap {
        // First decode with inJustDecodeBounds=true to check dimensions
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeResource(res, resId, this)

            // Calculate inSampleSize
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            inJustDecodeBounds = false

            BitmapFactory.decodeResource(res, resId, this)
        }
    }


    override fun onRobotFocusGained(qiContext: QiContext?) {
        this.qiContext = qiContext!!
        listen(qiContext)
    }

    override fun onRobotFocusLost() {
        TODO("Not yet implemented")
    }

    override fun onRobotFocusRefused(reason: String?) {
        TODO("Not yet implemented")
    }


    fun listen(qiContext: QiContext?){

        createQichatBotWithTopics(data)

        val chatBotBuilder = ChatBotBuilder(qiContext!!,qichatbot,locale)
        var chatFuture = chatBotBuilder.build()

        qichatbot.addOnEndedListener { endReason ->
            chatFuture.requestCancellation()
        }

        chat = chatBotBuilder.chat


        chat.addOnHeardListener {phrase ->
            if (phrase.text.toLowerCase().equals("başla")){
                runOnUiThread {
                    binding.imageView.visibility = View.VISIBLE
                    binding.sayStartText2.visibility = View.INVISIBLE
                }
            }
        }


        chat.addOnSayingChangedListener {
            if (it != null && it.text != null && it.text != "" && it.text != " "){


            if (chat.saying.text.toLowerCase().contains("kendini nasıl hissediyor göster")){
                println("asdasdadsasdasd")
                runBlocking {
                    launch {
                        delay(3000)
                        runOnUiThread {
                            binding.imageView.visibility = View.INVISIBLE
                            binding.imageviewHappy.visibility = View.VISIBLE
                            binding.imageviewSad.visibility = View.VISIBLE
                            binding.imageviewHappy.setOnClickListener {
                                val intent = Intent(this@PopUpActivity,ChoiceActivity2::class.java)
                                startActivity(intent)
                                finish()
                            }
                            binding.imageviewSad.setOnClickListener {
                                val intent = Intent(this@PopUpActivity,ChoiceActivity2::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
            }
            }

        }

        println("listen")
    }

    fun fillDynamicConcept(conceptName : String,phraseList : MutableList<Phrase>){
        var editablePhraseSet: EditablePhraseSet = this.qichatbot.dynamicConcept(conceptName)
        editablePhraseSet.addPhrases(phraseList)
    }

    fun createQichatBotWithTopics(name : String){
        //val names = arrayOf("Betul Bebek","Emre Araba","Emre Top","Emre Uçurtma")
        println("name:${name}")
        when(name.toLowerCase().contains("emre")){
            true ->{
                emre = TopicBuilder.with(this.qiContext)
                    .withResource(R.raw.emre2)
                    .build()

                qichatbot = QiChatbotBuilder.with(this.qiContext)
                    .withTopics(mutableListOf(emre))
                    .withLocale(locale)
                    .build()
            }
            false ->{
                betul = TopicBuilder.with(this.qiContext)
                    .withResource(R.raw.betul2)
                    .build()

                qichatbot = QiChatbotBuilder.with(this.qiContext)
                    .withTopics(mutableListOf(betul))
                    .withLocale(locale)
                    .build()

                runOnUiThread {
                    binding.imageviewHappy.setImageBitmap(decodeSampledBitmapFromResource(resources,R.drawable.kizmutlu,120,200))
                    binding.imageviewSad.setImageBitmap(decodeSampledBitmapFromResource(resources,R.drawable.kizuzgun,120,200))
                }
            }
        }

    }
    fun showPopup(){//emotion paramater
       // val parent = binding.parentLayout
        val view : View = View.inflate(this, R.layout.activity_pop_up,null)
        val happyIv : ImageView = view.findViewById(R.id.imageview_happy)
        val sadIv : ImageView = view.findViewById(R.id.imageview_sad)

        happyIv.setOnClickListener {
            runBlocking {
                launch {

                    delay(2000)
                    val intent = Intent(this@PopUpActivity,StartActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
        sadIv.setOnClickListener {
            runBlocking {
                launch {
                    delay(2000)
                    val intent = Intent(this@PopUpActivity,StartActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }

        /*
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(view,width,height,false)
        //popupWindow.showAtLocation(parent, Gravity.CENTER,0,0)

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

         */
    }
    override fun onDestroy() {
        super.onDestroy()
        QiSDK.unregister(this)
    }
}