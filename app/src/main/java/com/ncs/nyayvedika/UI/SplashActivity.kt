package com.ncs.nyayvedika.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.ncs.nyayvedika.R
import com.ncs.nyayvedika.UI.MainHolder.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
             startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        },3000)

    }
}