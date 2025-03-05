package gas.trulala.trulalago

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.view.View
import android.webkit.WebView
import android.content.Intent
import android.webkit.WebViewClient

class Browser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.browser)

        val web = findViewById<WebView>(R.id.web)
        val proses = findViewById<ProgressBar>(R.id.proses)

        web.settings.javaScriptEnabled = true

        web.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                proses.visibility = View.VISIBLE
                
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                proses.visibility = View.GONE
            }
        }

        web.loadUrl("https://www.google.com")
    }
}