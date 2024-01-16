package com.adiandrodev.chatgemini

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.adiandrodev.chatgemini.databinding.ActivityMainBinding
import com.adiandrodev.chatgemini.fragments.MainFragment
import com.google.android.play.core.review.ReviewManagerFactory


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(binding.fragmentContainerView.id, MainFragment::class.java, null)
                .commit()
        }

        showReviewDialog()
    }

    private fun showReviewDialog(){
        val reviewManager = ReviewManagerFactory.create(applicationContext)
        reviewManager.requestReviewFlow().addOnCompleteListener{
            if (it.isSuccessful){
                reviewManager.launchReviewFlow(this , it.result)
            }
        }
    }

}