package org.team4.hnreader.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.team4.hnreader.data.local.DataStoreHelper
import org.team4.hnreader.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var dataStoreHelper: DataStoreHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataStoreHelper = DataStoreHelper.getInstance(this)
        lifecycleScope.launch {
            dataStoreHelper.dataStore.data.first()
        }

        checkTheme()

        Handler(Looper.getMainLooper()).postDelayed({
            val intentToMainActivity = Intent(this, MainActivity::class.java)
            intentToMainActivity.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intentToMainActivity)
            finish()
        }, 2000)
    }

    private fun checkTheme() {
        dataStoreHelper.currentTheme.asLiveData().observe(this) { result ->
            runBlocking {
                AppCompatDelegate.setDefaultNightMode(
                    if (result == DataStoreHelper.LIGHT_THEME)
                        AppCompatDelegate.MODE_NIGHT_NO
                    else
                        AppCompatDelegate.MODE_NIGHT_YES
                )
                delegate.applyDayNight()
            }
        }
    }
}
