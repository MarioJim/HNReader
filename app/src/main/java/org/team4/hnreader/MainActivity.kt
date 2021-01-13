package org.team4.hnreader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    public fun openNavDrawer(view: View) {
        //val intentToNavDrawer = Intent(this, NavDrawerActivity::class.java)
        //startActivity(intentToNavDrawer)
    }
}