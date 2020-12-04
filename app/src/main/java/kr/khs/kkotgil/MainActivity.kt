package kr.khs.kkotgil

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kr.khs.nh2020.network.JSparser
import kr.khs.nh2020.network.RegisterToken

class MainActivity : AppCompatActivity() {

    private val BACK_BUTTON_GAP = 2000L
    private var backBtnClicked = 0L
    private val homePage = "https://naver.com"
//    private val homePage = "http://192.168.0.29:8080/"

    override fun onResume() {
        super.onResume()
        webview.loadUrl(intent.getStringExtra("url") ?: homePage)
        intent.removeExtra("url")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFCM()
        getFirebaseToken()

        webview.settings.apply {
            javaScriptEnabled = true // 자바스크립트 실행 허용
            javaScriptCanOpenWindowsAutomatically = true // 자바스크립트에서 새창 실 행 허용
            setSupportMultipleWindows(false) // 새 창 실행 허용
            loadWithOverviewMode = false // 메타 태그 허용
            useWideViewPort = true // 화면 사이즈 맞추기 허용
            setSupportZoom(false) // 화면 줌 허용
            builtInZoomControls = false // 화면 확대 축소 허용 여부
            cacheMode = WebSettings.LOAD_NO_CACHE // 브라우저 캐시 허용 여부
            domStorageEnabled = false // 로컬저장소 허용
        }

        webview.webChromeClient = NHChromeClient(applicationContext)

        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url!!)
                backbtn.visibility = if(view?.canGoBack()!!) View.VISIBLE else View.GONE
                return true
            }
        }

        webview.addJavascriptInterface(JSparser(), "Android")

        homebtn.setOnClickListener {
            webview.loadUrl(homePage)
//            RegisterToken.registerToken()
        }

        backbtn.setOnClickListener {
            if(webview.canGoBack())
                webview.goBack()
            else
                Toast.makeText(applicationContext, "뒤로 갈 창이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initFCM() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_HIGH)
            )
        }
    }

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            RegisterToken.token = token

            Log.d("Firebase", token ?: "no token")
        })

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