package com.ritorno.webverse.nearbychat

import android.Manifest
import android.R.attr.x
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Switch
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ritorno.webverse.R
import com.ritorno.webverse.avatar.WebViewActivity
import com.ritorno.webverse.nearbychat.GPS.GPSService


class NearbyFragment : Fragment(R.layout.fragment_nearby) {

    private var timer : CountDownTimer? = null
    private var longitude : String? = null
    private var latitude : String? = null
    private lateinit var sp : SharedPreferences
    private lateinit var  db : DatabaseReference

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        sp = requireActivity().getSharedPreferences("modes" , Context.MODE_PRIVATE)
        db =  FirebaseDatabase.getInstance().reference.child("nearby")

        if(ActivityCompat.checkSelfPermission(requireContext() , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(requireContext() , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(requireContext() , Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions( requireActivity(), arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION), 12)
        }

        timer = object : CountDownTimer(500000000, 10000) {
            override fun onTick(millisUntilFinished: Long) {
                update()
            }
            override fun onFinish() {

            }
        }
        timer?.start()


        val gpsswitch = requireActivity().findViewById<Switch>(R.id.gpsswitch)

        gpsswitch.setOnClickListener {
            if(gpsswitch.isChecked == true)
            {
                GPSService.startService(requireActivity())
            }else {
                GPSService.stopService(requireActivity())
            }
        }

        longitude = sp.getString("longitude" , "0.0000");
        latitude = sp.getString("latitude" , "0.000");
        val waladress = "0x824A829090595B4Ef2d523C70B6CaA0Da5e59871"
        val temp = "${latitude?.replace('.' , '@')}-${longitude?.replace('.' , '@')}"
        if( temp.length == 21)
        {
            db.child("users").get().addOnSuccessListener {
                if(it.value == null || !it.hasChild(waladress))
                {
                    db.child("users").child(waladress).setValue(temp)
                    db.child("Location").get().addOnSuccessListener {
                        if(!it.hasChild(temp)) {
                            db.child("Location").child(temp).push().setValue(waladress)
                        }
                        else{
                           // if(it.child(temp).hasChild(waladress))
                            //    db.child("Location").child("users").push().setValue(waladress)
                        }
                    }


                }else if(it.hasChild(waladress))
                {
                    db.child("users").child(waladress).get().addOnSuccessListener {
                        if(it.value != temp)
                        {
                            db.child("location").child(it.value.toString()).removeValue()
                            db.child("users").child(waladress).setValue(temp)
                            db.child("location").child(temp).child("users").push().setValue(waladress)
                        }
                    }
                }

                }


            }

    }

    fun update()
    {
        longitude = sp.getString("longitude" , "0.0000");
        latitude = sp.getString("latitude" , "0.000");
        val temp = "${latitude?.replace('.' , '@')}-${longitude?.replace('.' , '@')}"
        val waladress = "0x824A829090595B4Ef2d523C70B6CaA0Da5e59871"
        if( temp.length == 21)
        {
            db.child("users").get().addOnSuccessListener {
                if(it.value == null || !it.hasChild(waladress))
                {
                    db.child("users").child(waladress).setValue(temp)
                    db.child("Location").get().addOnSuccessListener {
                        if(!it.hasChild(temp)) {
                            db.child("Location").child(temp).push().setValue(waladress)
                        }
                        else{
                         //   if(it.child(temp).hasChild(waladress))
                             //   db.child("Location").child("users").push().setValue(waladress)
                        }
                    }


                }else if(it.hasChild(waladress))
                {
                    db.child("users").child(waladress).get().addOnSuccessListener {
                        if(it.value != temp)
                        {
                            db.child("location").child(temp).child(it.value.toString()).removeValue()
                            db.child("users").child(waladress).setValue(temp)
                            db.child("location").child(temp).child("users").push().setValue(waladress)
                        }
                    }
                }

            }
        }
    }


    override fun onDestroy() {
            timer?.cancel()
        super.onDestroy()
    }

}