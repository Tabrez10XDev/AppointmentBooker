package com.example.NIC_Dungarpur.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.NIC_Dungarpur.databinding.DashboardBinding
import com.google.firebase.auth.FirebaseAuth

class Dashboard : AppCompatActivity() {
    private lateinit var binding: DashboardBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        supportActionBar?.title = "Admin Dashboard"




    }





}

