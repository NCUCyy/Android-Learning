package com.cyy.exp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.cyy.exp.databinding.ActivityFirstBinding

class FirstActivity : AppCompatActivity() {
    lateinit var binding: ActivityFirstBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        binding = ActivityFirstBinding.inflate(layoutInflater)
        binding.textView.text = "XML配置方式2---VieBinding"
        setContentView(binding.root)

//        val textView = findViewById<TextView>(R.id.textView)
//        textView.text = "XML配置方式1---findViewById"
    }
}