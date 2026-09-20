package com.arcade.jogodavelha

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color as AndroidColor
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.webkit.WebViewAssetLoader
import com.arcade.jogodavelha.ui.theme.ArcadeBackground
import com.arcade.jogodavelha.ui.theme.JogoDaVelhaTheme
import java.net.URLDecoder

class MainActivity : ComponentActivity() {

    private var webViewInstance: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = false
        insetsController.isAppearanceLightNavigationBars = false

        setContent {
            JogoDaVelhaTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = ArcadeBackground,
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(ArcadeBackground)
                    ) {
                        ArcadeGameScreen(
                            onWebViewCreated = { webViewInstance = it },
                            onExitRequested = { finish() }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        webViewInstance?.onResume()
    }

    override fun onPause() {
        webViewInstance?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        webViewInstance?.destroy()
        webViewInstance = null
        super.onDestroy()
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ArcadeGameScreen(
    onWebViewCreated: (WebView) -> Unit,
    onExitRequested: () -> Unit
) {
    val context = LocalContext.current

    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(AndroidColor.parseColor("#05091A"))

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                mediaPlaybackRequiresUserGesture = false
                allowFileAccess = true
                allowContentAccess = true
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
                cacheMode = WebSettings.LOAD_DEFAULT
                useWideViewPort = true
                loadWithOverviewMode = false
                setSupportZoom(false)
                displayZoomControls = false
            }

            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            overScrollMode = WebView.OVER_SCROLL_NEVER

            val assetLoader = WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
                .build()

            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView,
                    request: WebResourceRequest
                ): WebResourceResponse? {
                    val url = request.url
                    val path = url.path ?: ""
                    // Handle uploads and custom asset paths with URL decoding
                    if (path.contains("/assets/")) {
                        val assetPath = path.substringAfter("/assets/")
                        val customStream = loadAssetStream(context, assetPath)
                        if (customStream != null) return customStream
                    }

                    return assetLoader.shouldInterceptRequest(url)
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    return true
                }
            }

            addJavascriptInterface(AndroidNativeBridge(context, onExitRequested), "AndroidNative")

            loadUrl("https://appassets.androidplatform.net/assets/index.html")
        }
    }

    DisposableEffect(webView) {
        onWebViewCreated(webView)
        onDispose { }
    }

    BackHandler {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            webView.evaluateJavascript(
                "(function() { " +
                        "var el = document.querySelector('button[onClick*=\"goHome\"], button[onClick*=\"leaveMatch\"], button[onClick*=\"closeModal\"]'); " +
                        "if (el) { el.click(); return true; } " +
                        "return false; " +
                        "})();"
            ) { result ->
                if (result != "true") {
                    onExitRequested()
                }
            }
        }
    }

    AndroidView(
        factory = { webView },
        modifier = Modifier.fillMaxSize()
    )
}

private fun loadAssetStream(context: Context, relativePath: String): WebResourceResponse? {
    return try {
        val decoded = URLDecoder.decode(relativePath, "UTF-8")
        val mimeType = when {
            decoded.endsWith(".html", true) -> "text/html"
            decoded.endsWith(".js", true) -> "application/javascript"
            decoded.endsWith(".css", true) -> "text/css"
            decoded.endsWith(".mp3", true) -> "audio/mpeg"
            decoded.endsWith(".ttf", true) -> "font/ttf"
            decoded.endsWith(".json", true) -> "application/json"
            decoded.endsWith(".png", true) -> "image/png"
            else -> "application/octet-stream"
        }
        val stream = context.assets.open(decoded)
        WebResourceResponse(mimeType, "UTF-8", stream)
    } catch (e: Exception) {
        null
    }
}

class AndroidNativeBridge(
    private val context: Context,
    private val onExitRequested: () -> Unit
) {
    @JavascriptInterface
    fun exitApp() {
        onExitRequested()
    }

    @JavascriptInterface
    fun vibrate(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {
        }
    }
}
