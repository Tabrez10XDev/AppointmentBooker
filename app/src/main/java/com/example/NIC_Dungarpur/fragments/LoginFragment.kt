package com.example.NIC_Dungarpur.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.NIC_Dungarpur.R
import com.example.NIC_Dungarpur.app.Dashboard
import com.example.NIC_Dungarpur.app.DashboardUser
import com.example.NIC_Dungarpur.databinding.FragmentLoginBinding
import com.example.NIC_Dungarpur.util.ROOT_UID
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class  LoginFragment : Fragment() {


    private lateinit var binding: FragmentLoginBinding

    override fun onStart() {
        super.onStart()
        checkLoggedInState()
    }

    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        auth = FirebaseAuth.getInstance()
        hidebar()

        binding.blogin.setOnClickListener {

            hideKeyboard()
            loginUser()
        }

        binding.bsignup.setOnClickListener {
            hideKeyboard()
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)

        }
    }

    private fun hideKeyboard(){
        val view = activity?.currentFocus
        view?.let { v ->
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }



    private fun loginUser() {
        showbar()
        Log.d("Lj","Inside")

        val number = binding.tvsign.text.toString().trim()
        val password = binding.tvpass.text.toString().trim()
        if( number.isNotEmpty() and password.isNotEmpty()){
            Log.d("Lj","Not empty")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(number,password).addOnSuccessListener {
                        hidebar()
                        checkLoggedInState()
                    }.addOnFailureListener {
                        hidebar()
                        Toast.makeText(activity, "Invalid Credentials", Toast.LENGTH_SHORT).show()

                    }.addOnCanceledListener {
                        hidebar()
                        Toast.makeText(activity, "Error!", Toast.LENGTH_SHORT).show()

                    }

                }
                catch (e : Exception){
                    withContext(Dispatchers.Main){
                        hidebar()
                        Toast.makeText(activity,e.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
        else{
            Toast.makeText(activity,"Invalid Credentials", Toast.LENGTH_SHORT).show()
            hidebar()
        }
    }

    private fun checkLoggedInState(){

        if(auth.currentUser != null
                && auth.uid == ROOT_UID){
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

    private fun hidebar(){
        binding.progresslog.visibility = View.INVISIBLE
        binding.blogin.isEnabled = true
        binding.bsignup.isEnabled = true
    }

    private fun showbar(){
        binding.progresslog.visibility = View.VISIBLE
        binding.blogin.isEnabled = false
        binding.bsignup.isEnabled = false
    }






}