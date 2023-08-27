package com.example.webviewprototype

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.webviewprototype.ui.theme.WebViewPrototypeTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebViewPrototypeTheme {
                Scaffold { padding ->
                    Column(modifier = Modifier.padding(padding)) {
                        var shouldShowWeb by remember {
                            mutableStateOf(false)
                        }
                        if (shouldShowWeb) Web()
                        Button(modifier = Modifier.weight(1f), onClick = { shouldShowWeb = !shouldShowWeb }) {
                            Text(text = "y")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Web() {

    // Declare a string that contains a url
    val mUrl = "https://www.codehim.com/demo/sign-in-and-sign-up-form-template/"

    // Adding a WebView inside AndroidView
    // with layout as full screen
    AndroidView(factory = {
        WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.javaScriptEnabled = true
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    view?.evaluateJavascript("" +
                            "(function() { " +
                                "return ('hello world'); " +
                            "})" +
                            "();") { result ->
                        Log.d("WebView", "Something's sent: $result")
                    }
                }
            }
            loadUrl(mUrl)
        }
    }, update = {
        it.loadUrl(mUrl)
    })
}