package com.ritorno.webverse.avatar

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kotlin.readyplayerme.WebViewInterface
import com.ritorno.webverse.R

class AvatarActivity : AppCompatActivity(), WebViewActivity.WebViewCallback {

    private var urlConfig: UrlConfig = UrlConfig()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avatar)

        val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted -> }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermission.launch(Manifest.permission.CAMERA)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermission.launch(Manifest.permission.READ_MEDIA_AUDIO)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            requestPermission.launch(Manifest.permission.READ_MEDIA_IMAGES)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
            requestPermission.launch(Manifest.permission.READ_MEDIA_VIDEO)
        }

        val updatebtn = this.findViewById<Button>(R.id.update_button)
        val createbtn = this.findViewById<Button>(R.id.create_button)

        WebViewActivity.setWebViewCallback(this)
        if (CookieHelper(this).getUpdateState()){
            updatebtn.visibility = View.VISIBLE
        }

        createbtn.setOnClickListener {
            openWebViewPage(false)
        }

        updatebtn.setOnClickListener{
            openWebViewPage(true)
        }
    }

    private fun openWebViewPage(clearBrowserCache: Boolean) {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.CLEAR_BROWSER_CACHE, clearBrowserCache)
        intent.putExtra(WebViewActivity.URL_KEY, UrlBuilder(urlConfig).buildUrl())
        webViewActivityResultLauncher.launch(intent)
    }

    private val webViewActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("RPM", "Result activity run.")
        }
    }


    override fun onAvatarExported(avatarUrl: String) {
        Log.d("RPM", "Avatar Exported - Avatar URL: $avatarUrl")
        showAlert(avatarUrl)
    }

    override fun onOnUserSet(userId: String) {
        Log.d("RPM", "User Set - User ID: $userId")
    }

    override fun onOnUserUpdated(userId: String) {
        Log.d("RPM", "User Updated - User ID: $userId")
    }

    override fun onOnUserAuthorized(userId: String) {
        Log.d("RPM", "User Authorized - User ID: $userId")
    }

    override fun onAssetUnlock(assetRecord: WebViewInterface.AssetRecord) {
        Log.d("RPM", "Asset Unlock - Asset Record: $assetRecord")
    }

    override fun onUserLogout() {
        Log.d("RPM", "User Logout")
    }

    private fun showAlert(url: String){
        val context = this@AvatarActivity
        val clipboardData = ClipData.newPlainText("Ready Player Me", url)
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(clipboardData)
        Toast.makeText(context, "Url copied into clipboard.", Toast.LENGTH_SHORT).show()

        // display modal window with the avatar url
        val builder = AlertDialog.Builder(context).apply {
            setTitle("Result")
            setMessage(url)
            setPositiveButton("Ok"){ dialog, _ ->
                dialog.dismiss()
            }
        }.create()
        builder.show()
    }

}