package com.ritorno.webverse.avatar

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kotlin.readyplayerme.WebViewInterface
import com.kotlin.readyplayerme.WebViewInterface.WebMessage
import com.ritorno.webverse.MainActivity
import com.ritorno.webverse.R

class WebViewActivity : AppCompatActivity() {
    interface WebViewCallback {
        fun onAvatarExported(avatarUrl: String)
        fun onOnUserSet(userId: String)
        fun onOnUserUpdated(userId: String)
        fun onOnUserAuthorized(userId: String)
        fun onAssetUnlock(assetRecord: WebViewInterface.AssetRecord)
        fun onUserLogout(){}
    }

    companion object {
        private const val ID_KEY = "id"
        private const val ASSET_ID_KEY = "assetId"
        const val CLEAR_BROWSER_CACHE = "clear_browser_cache"
        const val URL_KEY = "url_key"
        var callback: WebViewCallback? = null
        private lateinit var  db : DatabaseReference
        fun setWebViewCallback(callback: WebViewCallback) {
            Companion.callback = callback
        }
    }

    var avatarUrl : String? = null
    private lateinit var sp: SharedPreferences
    lateinit var webview : WebView;
    lateinit var pgbar : ProgressBar;
    private var isCreateNew = false
    
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var webViewUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        db =  FirebaseDatabase.getInstance().reference.child("Avatars")

        sp = getSharedPreferences("avatar", Context.MODE_PRIVATE)
        webview = findViewById<WebView>(R.id.webview)
        pgbar = findViewById<ProgressBar>(R.id.progress_bar)

        isCreateNew = intent.getBooleanExtra(CLEAR_BROWSER_CACHE, false)
        webViewUrl = intent.getStringExtra(URL_KEY) ?: "https://demo.readyplayer.me/avatar"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpWebView(intent.getBooleanExtra(CLEAR_BROWSER_CACHE, false))
        setUpWebViewClient()
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun setUpWebView(clearBrowserCache: Boolean) {
        Log.d("RPM", "onCreate: clearBrowserCache $clearBrowserCache")
        with(webview.settings){
            javaScriptEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            databaseEnabled = true
            domStorageEnabled = true
            allowFileAccess = true

        }

        with(webview){
            addJavascriptInterface(WebViewInterface(this@WebViewActivity){ webMessage ->
                handleWebMessage(webMessage)
            }, "WebView")
            if (clearBrowserCache){
                clearWebViewData()
            }
            Log.d("RPM","setUpWebView url = $webViewUrl")
            loadUrl(webViewUrl)
        }
    }

    private fun setUpWebViewClient() {
        with(webview){
            webViewClient = object: WebViewClient(){
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    pgbar.visibility = View.GONE
                    visibility = View.VISIBLE
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    executeJavascript()
                    if (
                        CookieManager.getInstance().hasCookies()
                    ) CookieHelper(this@WebViewActivity).setUpdateState(true)
                }
            }

            webChromeClient = object: WebChromeClient(){
                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    this@WebViewActivity.filePathCallback = filePathCallback

                    fileChooserParams?.let {
                        if (it.isCaptureEnabled){
                            if (hasPermissionAccess()) {
                                ImagePicker.with(this@WebViewActivity)
                                    .crop()
                                    .cameraOnly()
                                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                                    .maxResultSize(1920, 1920)	//Final image resolution will be less than 1080 x 1080(Optional)
                                    .start()


                            } else {
                                requestPermission.launch(arrayOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ))
                            }
                        } else {
                            openDocumentContract.launch("image/*")
                        }
                    }
                    return true
                }

                override fun onPermissionRequest(request: PermissionRequest?) {
                    Log.d("PERMISSION", "onPermissionRequest: ${request?.resources} ")
                    request?.grant(arrayOf(Manifest.permission.CAMERA))

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            if( requestCode == ImagePicker.REQUEST_CODE ) {
                if (data != null) {
                    val path = MediaStore.Images.Media.insertImage(
                        contentResolver,
                        data.dataString?.toUri()!!.path,
                        "fromCamera.jpeg",
                        ""
                    )
                    filePathCallback?.onReceiveValue(arrayOf(Uri.parse(path)))

                }
            }
        }
    }
   // private val openCameraResultContract  = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
       // it?.let {
        //    Log.d("ON RESULT", "no data bitmap: $it")
        //    val path = MediaStore.Images.Media.insertImage(contentResolver, it, "fromCamera.jpeg", "")
         //   filePathCallback?.onReceiveValue(arrayOf(Uri.parse(path)))
       // } ?: Toast.makeText(this, "No Image captured !!", Toast.LENGTH_SHORT).show()
  //  }

   val openDocumentContract = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){
        it?.let {
            filePathCallback?.onReceiveValue(arrayOf(it))
        } ?: Toast.makeText(this, "No Image Selected !!", Toast.LENGTH_SHORT).show()
    }

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){ permissionMap ->

        ImagePicker.with(this@WebViewActivity)
            .crop()
            .cameraOnly()
            .crop(1080F, 2120F)
            .compress(1024)			//Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1920)	//Final image resolution will be less than 1080 x 1080(Optional)
            .start()
    }

    private fun hasPermissionAccess(): Boolean{
        return arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun executeJavascript() {
        with(webview){
            evaluateJavascript("""
                var hasSentPostMessage = false;
                function subscribe(event) {
                    const json = parse(event);
                    const source = json.source;
                    
                    if (source !== 'readyplayerme') {
                      return;
                    }
                    
                    if (json.eventName === 'v1.frame.ready' && !hasSentPostMessage) {
                        window.postMessage(
                            JSON.stringify({
                                target: 'readyplayerme',
                                type: 'subscribe',
                                eventName: 'v1.**'
                            }),
                            '*'
                        );
                        hasSentPostMessage = true;
                    }

                    WebView.receiveData(event.data)
                }

                function parse(event) {
                    try {
                        return JSON.parse(event.data);
                    } catch (error) {
                        return null;
                    }
                }

                window.removeEventListener('message', subscribe);
                window.addEventListener('message', subscribe);
            """.trimIndent(), null)
        }
    }

    private fun handleWebMessage(webMessage: WebMessage) {

        when (webMessage.eventName) {
            WebViewInterface.WebViewEvents.USER_SET -> {
                val userId = requireNotNull(webMessage.data[ID_KEY]) {
                    "RPM: 'userId' cannot be null"
                }
                callback?.onOnUserSet(userId)
            }
            WebViewInterface.WebViewEvents.USER_UPDATED -> {
                val userId = requireNotNull(webMessage.data[ID_KEY]) {
                    "RPM: 'userId' cannot be null webMessage.data"
                }
                callback?.onOnUserUpdated(userId)
            }
            WebViewInterface.WebViewEvents.USER_AUTHORIZED -> {
                val userId = requireNotNull(webMessage.data[ID_KEY]) {
                    "RPM: 'userId' cannot be null webMessage.data"
                }
                callback?.onOnUserAuthorized(userId)
            }
            WebViewInterface.WebViewEvents.ASSET_UNLOCK -> {
                val userId = requireNotNull(webMessage.data[ID_KEY]) {
                    "RPM: 'id' cannot be null webMessage.data"
                }
                val assetId = requireNotNull(webMessage.data[ASSET_ID_KEY]) {
                    "RPM: 'assetId' cannot be null webMessage.data"
                }
                val assetRecord = WebViewInterface.AssetRecord(userId, assetId)
                callback?.onAssetUnlock(assetRecord)
            }
            WebViewInterface.WebViewEvents.AVATAR_EXPORT -> {
                 avatarUrl = requireNotNull(webMessage.data["url"]) {
                    "RPM: 'url' cannot be null in webMessage.data"
                    finishActivityWithFailure("RPM: avatar 'url' property not found in event data")
                }
                callback?.onAvatarExported(avatarUrl!!)



                finishActivityWithResult()
            }
            WebViewInterface.WebViewEvents.USER_LOGOUT -> {

                callback?.onUserLogout()
            }
        }
    }

    private fun finishActivityWithResult() {
        val resultString = "Avatar Created Successfully"

       // val editor = sp.edit()
       // editor.putString("gbllink", resultString.toString())
      //  editor.commit()
      //  Toast.makeText(this , sp.getString("gbllink" , "nope") , Toast.LENGTH_SHORT).show()

    }

    private fun finishActivityWithFailure(errorMessage: String) {
        val data = Intent()
        data.putExtra("error_key", errorMessage)
        setResult(Activity.RESULT_CANCELED, data)
        finish()
    }

    fun WebView.clearWebViewData() {
        clearHistory()
        clearFormData()
        clearCache(true)
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().removeSessionCookies(null)
        CookieManager.getInstance().flush()
        WebStorage.getInstance().deleteAllData()
    }

    override fun onDestroy() {
        db.child("akash").push().setValue(avatarUrl).addOnSuccessListener {

        }
        super.onDestroy()
    }
}
