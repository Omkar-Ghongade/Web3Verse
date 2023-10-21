package com.ritorno.webverse.nearbychat.GPS

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.*
import android.location.Geocoder
import android.location.Location
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.ritorno.webverse.MainActivity
import com.ritorno.webverse.R

class GPSService: Service()  {

    private val CHANNEL_ID = "Phone Kavach"
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var addressResultReceiver: LocationAddressResultReceiver? = null
    private var currentLocation: Location? = null
    private var locationCallback: LocationCallback? = null
    private lateinit var sp : SharedPreferences


    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    companion object {
        fun startService(context: Context) {
            val startIntent = Intent(context, GPSService::class.java)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, GPSService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //do heavy work on a background thread
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle("Random Chat")
            .setSmallIcon(R.drawable.baseline_all_inclusive_24)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(2, notification)
        //stopSelf();
        return Service.START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)


    }

    override fun onCreate() {
        super.onCreate()

        sp = applicationContext.getSharedPreferences("modes" , Context.MODE_PRIVATE)
        addressResultReceiver = LocationAddressResultReceiver(Handler())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                currentLocation = locationResult.locations[0]
                getAddress()
            }
        }
        startLocationUpdates()

    }

    override fun onDestroy() {
        super.onDestroy()

    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {

        val locationRequest = LocationRequest()
        locationRequest.setInterval(10000)
        locationRequest.setFastestInterval(1000)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        fusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback!!, null)

    }

    public fun getAddress() {
        if (!Geocoder.isPresent()) {
            Toast.makeText(this, "Can't find current address, ", Toast.LENGTH_SHORT ).show()
            return
        }

        val intent = Intent(this, GetAddressIntentService::class.java)
        intent.putExtra("add_receiver", addressResultReceiver)
        intent.putExtra("add_location", currentLocation)
        startService(intent)

    }

    inner class LocationAddressResultReceiver internal constructor(handler: Handler?) :
        ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            if (resultCode == 0) {
                Log.d("Address", "Location null retrying")
                getAddress()
            }
            if (resultCode == 1) {
                //Toast.makeText(applicationContext, "Address not found, ", Toast.LENGTH_SHORT).show()
            }
            val currentAdd = resultData.getString("address_result")
            val longi = resultData.getDouble("longitude")
            val lati = resultData.getDouble("latitude")
            if (currentAdd != null) {
                val editor = sp.edit()
                editor.putString("location" , currentAdd)
                editor.putString("longitude" , "$longi")
                editor.putString("latitude" , "$lati")
                editor.commit()
            }
        }
    }

}