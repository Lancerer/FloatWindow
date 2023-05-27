package com.lancer.floatwindow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<Button>(R.id.go_to_call).apply {
            setOnClickListener {
                startActivity(Intent(this@MainActivity, CallActivity::class.java))
            }
        }
    }
}