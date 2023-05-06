package com.example.demo.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.demo.R
import com.example.demo.adapter.GridAdapter
import com.example.demo.databinding.ActivityChoice2Binding

class ChoiceActivity2 : AppCompatActivity() {
    private lateinit var binding : ActivityChoice2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choice2)

        binding = ActivityChoice2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val names = arrayOf("Emre Tavşan","Emre Kuş","Betül Hasta","Emre Pasta")

        val gridAdapter = GridAdapter(this,names)

        binding.gridView2.adapter = gridAdapter
    }
}