package com.example.NIC_Dungarpur.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.NIC_Dungarpur.R
import com.example.NIC_Dungarpur.databinding.DashboardUserBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardUser : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var binding: DashboardUserBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        supportActionBar?.title = auth.currentUser.email




    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout-> {
                logOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }

        private fun logOut(){
            auth.signOut()
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            this?.finish()
        }


}