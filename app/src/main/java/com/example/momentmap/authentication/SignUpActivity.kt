package com.example.momentmap.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.momentmap.MainActivity
import com.example.momentmap.R
import com.example.momentmap.data.User
import com.example.momentmap.databinding.ActivitySignUpBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase
            .getInstance("https://momentmap-faef6-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Users")
        setContentView(binding.root)

        binding.email.addTextChangedListener(textWatcher)
        binding.password.addTextChangedListener(textWatcher)
        binding.confirmPassword.addTextChangedListener(textWatcher)

        binding.signUpButton.isEnabled = false

        binding.signInButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding.signUpButton.setOnClickListener {
            auth.createUserWithEmailAndPassword(
                binding.email.text.toString(),
                binding.password.text.toString()
            ).addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User created!", Toast.LENGTH_SHORT).show()

                    saveNewUser(
                        auth.currentUser?.uid.toString(),
                        binding.email.text.toString(),
                        binding.password.text.toString()
                    )

                    startActivity(Intent(this, MainActivity::class.java))

                } else {
                    Toast.makeText(this, "User creation failed!", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding.run {
                signUpButton.isEnabled =
                    validateEmailAddress() && validatePassword() && validatePasswordConfirmation()
                emailLayout.helperText =
                    if (!validateEmailAddress())
                        resources.getString(R.string.required)
                    else null
                passwordLayout.helperText =
                    if (!validatePassword())
                        resources.getString(R.string.password_helper_text)
                    else null
                confirmPasswordLayout.helperText =
                    if (!validatePasswordConfirmation())
                        resources.getString(R.string.password_mismatch)
                    else null
            }
        }

        override fun afterTextChanged(p0: Editable?) = Unit
    }

    private fun validateEmailAddress(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString()).matches()
    }

    private fun validatePassword(): Boolean {
        return binding.password.text.toString().length >= 8
    }

    private fun validatePasswordConfirmation(): Boolean {
        return binding.password.text.toString() == binding.confirmPassword.text.toString()
    }

    private fun saveNewUser(userId: String, email: String, password: String) {
        val user = User(email, password)
        Toast.makeText(this, "Method called!", Toast.LENGTH_SHORT).show()
        database.child(userId).setValue(user).addOnSuccessListener {
            Toast.makeText(this, "User saved!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "User isn't saved!", Toast.LENGTH_SHORT).show()
        }
    }
}