package com.aurora.authenticator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewFlipper
import com.wineberryhalley.bclassapp.BaseActivity
import com.wineberryhalley.upthunder.R
import com.wineberryhalley.upthunder.updater.CheckUActivity
import com.wineberryhalley.upthunder.updater.StrDt
import nl.komponents.kovenant.task
import nl.komponents.kovenant.ui.failUi
import nl.komponents.kovenant.ui.successUi
import java.io.FileNotFoundException
import java.util.*

class ResultActivity : BaseActivity() {


    private var lastBackPressed = 0L

    private lateinit var viewFlipper: ViewFlipper
    private lateinit var name: TextView
    private lateinit var email: TextView
    private lateinit var auth: TextView

    private lateinit var token: TextView


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            val email = intent.getStringExtra(MainActivity.AUTH_EMAIL)
            val token = intent.getStringExtra(MainActivity.AUTH_TOKEN)
            retrieveAc2dmToken(email, token)
        }
    }

    override fun onBackPressed() {
        if (lastBackPressed + 1000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            lastBackPressed = System.currentTimeMillis()
            Toast.makeText(this, "Click twice to exit", Toast.LENGTH_SHORT).show()
        }
    }

    override fun Main() {
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        auth = findViewById(R.id.auth)
        token = findViewById(R.id.token)
        viewFlipper = findViewById(R.id.view_flipper)
        onNewIntent(intent)
    }

    override fun statusChanged(pixelesSizeBar: Int) {

    }

    override fun resLayout(): Int {
        return R.layout.activity_result_m
    }

    override fun keysNotification(): ArrayList<String> {
        return ArrayList<String>()
    }

    override fun onReceiveValues(values: ArrayList<String>?) {

    }

    private fun retrieveAc2dmToken(Email: String?, oAuthToken: String?) {

        task {
            AC2DMTask().getAC2DMResponse(Email, oAuthToken)
        } successUi {
            if (it.isNotEmpty()) {
             StrDt.saveData(it)
                val intent = Intent(applicationContext, CheckUActivity::class.java)
                intent.putExtra("well_init", "yes")
            //    startActivity(intent)
            } else {
                viewFlipper.displayedChild = 2
                Toast.makeText(this, "Failed to generate AC2DM AuthGPlay Token", Toast.LENGTH_LONG).show()
            }
        } failUi {
            viewFlipper.displayedChild = 2
            Toast.makeText(this, "Failed to generate AC2DM AuthGPlay Token", Toast.LENGTH_LONG).show()
        }
    }
    companion object{
        fun loadProperties(deviceName: String?): Properties? {
            return try {
                val properties = Properties()
                val inputStream = javaClass
                        .classLoader
                        .getResourceAsStream(deviceName)
                if (inputStream != null) {
                    properties.load(inputStream)
                } else {
                    throw FileNotFoundException("Device config file not found")
                }
                properties
            } catch (e: Exception) {
                null
            }
        }
    }

}