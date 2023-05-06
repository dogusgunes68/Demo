package com.example.demo.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.demo.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {
    private lateinit var binding : ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOgretim.setOnClickListener {
            val intent = Intent(this,ChoiceActivity::class.java)
            startActivity(intent)
        }

        binding.btnDegerlendirme.setOnClickListener {
            val intent = Intent(this,ChoiceActivity2::class.java)
            startActivity(intent)
        }

    }
}