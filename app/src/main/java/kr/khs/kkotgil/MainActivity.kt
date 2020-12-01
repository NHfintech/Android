package kr.khs.kkotgil

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val BACK_BUTTON_GAP = 2000L
    private var backBtnClicked = 0L
    private val homepage = "https://www.naver.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webview.settings.apply {
            javaScriptEnabled = true // 자바스크립트 실행 허용
            javaScriptCanOpenWindowsAutomatically = true // 자바스크립트에서 새창 실 행 허용
            setSupportMultipleWindows(true) // 새 창 실행 허용
            loadWithOverviewMode = false // 메타 태그 허용
            useWideViewPort = true // 화면 사이즈 맞추기 허용
            setSupportZoom(false) // 화면 줌 허용
            builtInZoomControls = false // 화면 확대 축소 허용 여부
            cacheMode = WebSettings.LOAD_NO_CACHE // 브라우저 캐시 허용 여부
            domStorageEnabled = false // 로컬저장소 허용
        }

        webview.loadUrl(homepage)
        webview.webChromeClient = NHChromeClient(applicationContext)

        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url!!)

                return true
            }
        }

        homebtn.setOnClickListener {
            webview.loadUrl(homepage)
        }
    }

    class NHChromeClient(val context : Context) : WebChromeClient() {
        override fun onJsAlert(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?
        ): Boolean {
            AlertDialog.Builder(context)
                .setTitle("알림")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        result?.confirm()
                    }).create().show()

            return true
        }
    }

    override fun onBackPressed() {
        val cur = System.currentTimeMillis()

        when {
            webview.canGoBack() -> webview.goBack()
            cur - backBtnClicked <= BACK_BUTTON_GAP -> super.onBackPressed()
            else -> {
                backBtnClicked = cur;
                Toast.makeText(applicationContext, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}