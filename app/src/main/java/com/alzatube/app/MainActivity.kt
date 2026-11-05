package com.alzatube.app

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private lateinit var toolbar: LinearLayout
    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private var previousOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    private var landscapeOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.BLACK)
        }

        toolbar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(6, 6, 6, 6)
            setBackgroundColor(Color.rgb(25, 25, 25))
        }

        val title = TextView(this).apply {
            text = "AlzaTube 2.0"
            setTextColor(Color.WHITE)
            textSize = 16f
            setPadding(8, 0, 8, 0)
        }
        toolbar.addView(title, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        toolbar.addView(makeButton("Back") { if (webView.canGoBack()) webView.goBack() })
        toolbar.addView(makeButton("Reload") { webView.reload() })
        toolbar.addView(makeButton("Landscape") { toggleLandscape() })

        webView = WebView(this).apply {
            setBackgroundColor(Color.BLACK)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadsImagesAutomatically = true
            settings.mediaPlaybackRequiresUserGesture = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.setSupportMultipleWindows(false)
            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean = false
            }

            webChromeClient = object : WebChromeClient() {
                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    if (view == null) return
                    if (customView != null) {
                        callback?.onCustomViewHidden()
                        return
                    }
                    customView = view
                    customViewCallback = callback
                    previousOrientation = requestedOrientation
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    toolbar.visibility = View.GONE
                    webView.visibility = View.GONE
                    (root as ViewGroup).addView(
                        view,
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    )
                    enterImmersiveMode()
                }

                override fun onHideCustomView() {
                    leaveCustomView()
                }
            }
        }

        root.addView(toolbar, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        root.addView(webView, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        setContentView(root)
        webView.loadUrl("https://m.youtube.com")
    }

    private fun makeButton(label: String, action: () -> Unit): Button {
        return Button(this).apply {
            text = label
            textSize = 11f
            isAllCaps = false
            setPadding(5, 2, 5, 2)
            setOnClickListener { action() }
        }
    }

    private fun toggleLandscape() {
        landscapeOn = !landscapeOn
        requestedOrientation = if (landscapeOn) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        Toast.makeText(
            this,
            if (landscapeOn) "Landscape mode on" else "Automatic orientation",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun enterImmersiveMode() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            )
    }

    private fun leaveImmersiveMode() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }

    private fun leaveCustomView() {
        val activeView = customView ?: return
        (activeView.parent as? ViewGroup)?.removeView(activeView)
        customView = null
        toolbar.visibility = View.VISIBLE
        webView.visibility = View.VISIBLE
        leaveImmersiveMode()
        requestedOrientation = previousOrientation
        customViewCallback?.onCustomViewHidden()
        customViewCallback = null
    }

    override fun onBackPressed() {
        if (customView != null) {
            leaveCustomView()
        } else if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
