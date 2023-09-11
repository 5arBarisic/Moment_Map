package com.example.momentmap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import com.example.momentmap.databinding.ActivitySignInBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.signInButton.isEnabled = false
        binding.email.addTextChangedListener(textWatcher)
        binding.password.addTextChangedListener(textWatcher)

        binding.signInButton.setOnClickListener {
            logIn()
        }
        binding.signUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun logIn() {
        auth.signInWithEmailAndPassword(
            binding.email.text.toString(),
            binding.password.text.toString()
        ).addOnCompleteListener { task: Task<AuthResult> ->
            if (task.isSuccessful) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Login failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(text: Editable?) = Unit

        override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) =
            Unit

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            binding.run {
                signInButton.isEnabled =
                    validateEmailAddress() && validatePassword()
                emailLayout.helperText =
                    if (!validateEmailAddress())
                        resources.getString(R.string.required)
                    else null
                passwordLayout.helperText =
                    if (!validatePassword())
                        resources.getString(R.string.required)
                    else null
            }
        }
    }

    private fun validateEmailAddress(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString()).matches()
    }

    private fun validatePassword(): Boolean {
        return binding.password.text.toString().length >= 8
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}