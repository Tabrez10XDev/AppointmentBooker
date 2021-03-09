package com.example.akshat1.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.akshat1.R
import com.example.akshat1.app.Dashboard
import com.example.akshat1.app.DashboardUser
import com.example.akshat1.databinding.FragmentSignupBinding
import com.example.akshat1.util.ROOT_UID
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SignupFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding
    lateinit var auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignupBinding.bind(view)
        auth = FirebaseAuth.getInstance()

        hidebar()


        binding.bregister.setOnClickListener {
            val view = activity?.currentFocus
            view?.let { v ->
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)
            }
            registerUser()

        }


    }





    private fun showbar(){
        binding.progresssign.visibility = View.VISIBLE
        binding.bregister.isEnabled = false

    }

    private fun hidebar(){
        binding.progresssign.visibility = View.INVISIBLE
        binding.bregister.isEnabled = true
    }
    private fun registerUser() {
        showbar()
        val number = binding.tvsign.text.toString().trim()
        val password = binding.tvpass.text.toString().trim()
        if( number.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(number,password).addOnSuccessListener {
                        Toast.makeText(activity,"Account created",Toast.LENGTH_SHORT).show()
                        checkLoggedInState()
                    }.addOnFailureListener(){
                        Toast.makeText(activity,it.message,Toast.LENGTH_SHORT).show()
                        hidebar()
                    }


                }
                catch (e : Exception){
                    withContext(Dispatchers.Main){
                        hidebar()
                        Toast.makeText(activity,e.message,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }




    private fun checkLoggedInState(){


            if(auth.currentUser != null
                && auth.uid == ROOT_UID
            ){
                val intent = Intent(activity, Dashboard::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                activity?.finish()

            }
            else if(auth.currentUser != null){
                val intent = Intent(activity, DashboardUser::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                activity?.finish()


        }
    }



}