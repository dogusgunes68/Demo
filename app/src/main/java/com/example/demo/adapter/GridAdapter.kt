package com.example.demo.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import com.example.demo.R
import com.example.demo.view.ChoiceActivity
import com.example.demo.view.ChoiceActivity2
import com.example.demo.view.MainActivity
import com.example.demo.view.PopUpActivity
import kotlin.reflect.typeOf

internal class GridAdapter(
    val context: Activity,
    val name : Array<String>
) : BaseAdapter() {

    private lateinit var inflater : LayoutInflater

    override fun getCount(): Int {
        return name.size
    }

    override fun getItem(p0: Int): Any? {
        return null
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var convertView = p1

        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


        if (convertView == null){
            convertView = inflater.inflate(R.layout.grid_item,null)
        }

        val button = convertView!!.findViewById<Button>(R.id.btn_grid)
        button.text = name[p0]

        button.setOnClickListener {
            println(it.context.toString())
            if (it.context.toString().contains("ChoiceActivity2")){
                val intent = Intent(it.context, PopUpActivity::class.java)
                intent.putExtra("data",name[p0])
                it.context.startActivity(intent)
                context.finish()
                //it.context.applicationContext
            }else{
                val intent = Intent(it.context, MainActivity::class.java)
                intent.putExtra("data",name[p0])
                it.context.startActivity(intent)
                context.finish()
               // it.context.applicationContext
            }

        }

        return convertView
    }
}