package `in`.nic.raj_dungarpur.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.NIC_Dungarpur.R

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Akshat1)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        supportActionBar?.title = "Virtual Appointment"

    }
}