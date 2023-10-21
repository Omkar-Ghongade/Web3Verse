package com.ritorno.webverse.dashboard

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ritorno.webverse.R
import com.ritorno.webverse.avatar.AvatarActivity


class DashboardFragment : Fragment(R.layout.fragment_dashboard) {


    private lateinit var  db : DatabaseReference
    private lateinit var adapter: DashboardRecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val avatar_btn = requireActivity().findViewById<ImageView>(R.id.avatar_image_1)

        avatar_btn.setOnClickListener {
            val intent = Intent(requireActivity() , AvatarActivity::class.java)
            startActivity(intent)
        }



        adapter = DashboardRecyclerViewAdapter(this@DashboardFragment)
        val manager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, true)
        val nftRecyclerView = requireActivity().findViewById<RecyclerView>(R.id.dashoard_recyclerview)
        nftRecyclerView.layoutManager = manager
        nftRecyclerView.adapter = adapter

        val arr : ArrayList<nft_item> = ArrayList()

        db =  FirebaseDatabase.getInstance().reference.child("users")

            db.child("0x02d462cd31e8632724594b4e38cad76159347270a521f83216f848e265480fd9")
            .child("nfts").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot){
                    for(  x in dataSnapshot.getChildren())
                    {
                        val t : nft_item? = x.getValue(nft_item::class.java)
                        t?.let { arr.add(it) }
                    }
                    adapter.update(arr)
                    nftRecyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })

    }

}