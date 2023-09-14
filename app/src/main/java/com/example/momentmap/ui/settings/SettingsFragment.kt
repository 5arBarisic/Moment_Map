package com.example.momentmap.ui.settings

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.momentmap.MainActivity
import com.example.momentmap.R
import com.example.momentmap.authentication.SignInActivity
import com.example.momentmap.databinding.FragmentSettingsBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val updatedUser = mutableMapOf<String, String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()

        binding.oldPassword.addTextChangedListener(textWatcher)
        binding.password.addTextChangedListener(textWatcher)
        binding.confirmPassword.addTextChangedListener(textWatcher)

        binding.changePasswordButton.isEnabled = false
        binding.changePasswordButton.setOnClickListener {
            changePassword()
        }

        binding.signOutButton.setOnClickListener {

            auth.signOut()
            startActivity(Intent(activity, SignInActivity::class.java))
            activity?.finish()

        }

        return binding.root
    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding.run {
                changePasswordButton.isEnabled =
                    validateOldPassword() &&
                            validatePassword() &&
                            validatePasswordConfirmation() &&
                            !validateOldPasswordSameAsNew()

                oldPasswordLayout.helperText =
                    if (!validateOldPassword())
                        resources.getString(R.string.password_helper_text)
                    else null
                passwordLayout.helperText =
                    if (!validatePassword())
                        resources.getString(R.string.password_helper_text)
                    else if (validateOldPasswordSameAsNew())
                        resources.getString(R.string.password_same)
                    else null
                confirmPasswordLayout.helperText =
                    if (!validatePasswordConfirmation())
                        resources.getString(R.string.password_mismatch)
                    else null
            }
        }

        override fun afterTextChanged(p0: Editable?) = Unit
    }

    private fun validatePassword(): Boolean {
        return binding.password.text.toString().length >= 8
    }

    private fun validateOldPassword(): Boolean {
        return binding.oldPassword.text.toString().length >= 8
    }

    private fun validatePasswordConfirmation(): Boolean {
        return binding.password.text.toString() == binding.confirmPassword.text.toString()
    }

    private fun validateOldPasswordSameAsNew(): Boolean {
        return binding.oldPassword.text.toString() == binding.password.text.toString()
    }

    private fun changePassword() {
        val user = auth.currentUser
        val credential =
            EmailAuthProvider.getCredential(user?.email!!, binding.oldPassword.text.toString())


        user.reauthenticate(credential).addOnCompleteListener {

            if (it.isSuccessful) {
                Toast.makeText(requireContext(), "Re-Authentication success.", Toast.LENGTH_SHORT)
                    .show()
                user.updatePassword(binding.password.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            updatedUser["password"] = binding.password.text.toString()

                            database = FirebaseDatabase
                                .getInstance("https://momentmap-faef6-default-rtdb.europe-west1.firebasedatabase.app/")
                                .getReference("Users")

                            database.child(auth.currentUser?.uid.toString())
                                .updateChildren(updatedUser as Map<String, Any>)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "User updated!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }.addOnFailureListener {
                                Toast.makeText(
                                    requireContext(),
                                    "User isn't updated!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            Toast.makeText(
                                requireContext(),
                                "Password changed.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            auth.signOut()
                            startActivity(Intent(activity, SignInActivity::class.java))
                            activity?.finish()

                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Re-Authentication failed.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}