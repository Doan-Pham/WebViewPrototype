package com.example.webviewprototype

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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

const val DIVIDING_TEXT = "THIS_IS_SOME_DATA_THAT_YOU_NEED_SO_DONT_IGNORE_IT_"

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

interface JavaScriptInterface {
    fun getData(data: String)
}

@Composable
fun Web() {


    val javaScriptInterface = object : JavaScriptInterface {
        @JavascriptInterface
        override fun getData(data: String) {
            Log.d("WebView", "Getting data from web success: $data")
        }
    }
    // Declare a string that contains a url
    val mUrl = "https://www.codehim.com/demo/login-page-in-html5-with-validation/"

    // Adding a WebView inside AndroidView
    // with layout as full screen
    AndroidView(modifier = Modifier.fillMaxSize(), factory = {
        WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.javaScriptEnabled = true
            addJavascriptInterface(javaScriptInterface, "INTERFACE");

            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    val message = consoleMessage?.message()
                    Log.d("WebView", "Console message: ${consoleMessage?.message()}")
                    if (message?.contains(DIVIDING_TEXT) == true) {
                        Log.d(
                            "WebView",
                            "True Console message: ${consoleMessage.message()}"
                        )
                        Log.d(
                            "WebView",
                            "True true Console message: ${consoleMessage.message().removePrefix(DIVIDING_TEXT)}"
                        )
                    }
                    return super.onConsoleMessage(consoleMessage)
                }
            }
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    view?.evaluateJavascript(
                        "" +
                                "(function() { " +
                                "document.getElementById('login').addEventListener('click', function() {" +
                                " console.log('${DIVIDING_TEXT}' + document.getElementById('username').value);" +
                                "});" +
                                "INTERFACE.getData('${DIVIDING_TEXT}' + document.getElementById('username').value);" +
                                "})" +
                                "();"
                    ) { result ->
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