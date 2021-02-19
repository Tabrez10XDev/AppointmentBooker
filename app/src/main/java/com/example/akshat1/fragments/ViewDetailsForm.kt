package com.example.akshat1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.akshat1.R
import com.example.akshat1.databinding.FragmentUserDetailsFormBinding

class ViewDetailsForm : Fragment() {

    private lateinit var binding : FragmentUserDetailsFormBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_details_form, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentUserDetailsFormBinding.bind(view)
        binding.etName.isEnabled = false
        binding.etNumber.isEnabled = false
        binding.etEmail.isEnabled = false
        binding.etAddress.isEnabled = false
        binding.etVisitPurpose.isEnabled = false
        binding.uploadFilebtn.isEnabled = false
        binding.Formbtn.text = "Delete Slot"

    }
}