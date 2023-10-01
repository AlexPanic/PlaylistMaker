package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.btn_search)
        val imageClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Поищем что-нибудь!", Toast.LENGTH_SHORT).show()
            }
        }
        btn.setOnClickListener(imageClickListener)

        val btn2 = findViewById<Button>(R.id.btn_media)
        btn2.setOnClickListener {
            Toast.makeText(this@MainActivity, "Моя медиатека", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btn_settings).setOnClickListener {
            Toast.makeText(this@MainActivity, "К настройкам!", Toast.LENGTH_SHORT).show()
        }

    }
}