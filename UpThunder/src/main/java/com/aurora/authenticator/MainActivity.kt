package com.aurora.authenticator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.webkit.*
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.iterator
import com.wineberryhalley.bclassapp.BaseActivity
import com.wineberryhalley.upthunder.R
import java.util.ArrayList

class MainActivity : BaseActivity() {

    private lateinit var webview: WebView

    private val cookieManager = CookieManager.getInstance()


    override fun Main() {
        webview = findViewById(R.id.WebView)
        setup()
    }

    override fun statusChanged(pixelesSizeBar: Int) {
      
    }

    override fun resLayout(): Int {
     return R.layout.activity_web_m;
    }

    override fun keysNotification(): ArrayList<String> {
      return ArrayList<String>()
    }

    override fun onReceiveValues(values: ArrayList<String>?) {
        
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setup() {
        cookieManager.removeAllCookies(null)
        cookieManager.acceptThirdPartyCookies(webview)
        cookieManager.setAcceptThirdPartyCookies(webview, true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webview.settings.safeBrowsingEnabled = false
        }

        webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                val cookies = CookieManager.getInstance().getCookie(url)
                val cookieMap = Util.parseCookieString(cookies)
                if (cookieMap.isNotEmpty() && cookieMap[AUTH_TOKEN] != null) {
                    val oauthToken = cookieMap[AUTH_TOKEN]
                    webview.evaluateJavascript("(function() { return document.getElementById('profileIdentifier').innerHTML; })();") {
                        val email = it.replace("\"".toRegex(), "")
                        startResultsActivity(email, oauthToken)
                    }
                }
            }
        }

        webview.apply {
            settings.apply {
                allowContentAccess = true
                databaseEnabled = true
                domStorageEnabled = true
                javaScriptEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
            }
            loadUrl(EMBEDDED_SETUP_URL)
        }
    }

    private fun startResultsActivity(email: String, oauthToken: String?) {
        val intent = Intent(this@MainActivity, ResultActivity::class.java).apply {
            putExtra(AUTH_EMAIL, email)
            putExtra(AUTH_TOKEN, oauthToken)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent, ActivityOptionsCompat.makeCustomAnimation(
                this@MainActivity,
                android.R.anim.fade_in,
                android.R.anim.fade_out
        ).toBundle())
    }

    companion object {
        const val EMBEDDED_SETUP_URL = "https://accounts.google.com/EmbeddedSetup/identifier?flowName=EmbeddedSetupAndroid"
        const val AUTH_TOKEN = "oauth_token"
        const val AUTH_EMAIL = "AUTH_EMAIL"
    }
}