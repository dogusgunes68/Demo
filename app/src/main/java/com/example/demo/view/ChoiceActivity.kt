package com.example.demo.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.demo.R
import com.example.demo.adapter.GridAdapter
import com.example.demo.databinding.ActivityChoiceBinding

class ChoiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChoiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val names = arrayOf("Betul Bebek","Emre Araba","Emre Top","Emre Uçurtma")

        val gridAdapter = GridAdapter(this,names)

        binding.gridView.adapter = gridAdapter
/*
        binding.gridView.setOnItemClickListener { adapterView, view, i, l ->
            when(names[i]){
                "Betul Bebek" -> {

                }
                "Emre Araba" -> {
                    val intent = Intent(this,MainActivity::class.java)
                    intent.putExtra("data","emre araba")
                    startActivity(intent)
                }
                "Emre Top" -> {
                    val intent = Intent(this,MainActivity::class.java)
                    intent.putExtra("data","emre top")
                    startActivity(intent)
                }
                "Emre Uçurtma" -> {
                    val intent = Intent(this,MainActivity::class.java)
                    intent.putExtra("data","emre ucurtma")
                    startActivity(intent)
                }
                else ->{
                    println("Hiçbiri")
                }
            }
        }

 */
    }
}