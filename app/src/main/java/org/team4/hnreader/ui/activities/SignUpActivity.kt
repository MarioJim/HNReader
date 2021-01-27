package org.team4.hnreader.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import org.team4.hnreader.databinding.ActivitySignUpBinding
import org.team4.hnreader.ui.MainActivity

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Sign Up"

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnSignUp.setOnClickListener {
            val email = binding.teEmail.text.toString()
            val password = binding.tePassword.text.toString()
            val passwordConfirmation = binding.tePasswordConfirm.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && passwordConfirmation.isNotEmpty()) {
                if (password == passwordConfirmation) {
                    signUp(email, password)
                } else {
                    binding.tePassword.setText("")
                    binding.tePasswordConfirm.setText("")
                    displayToast("Make sure your passwords match!")
                }
            } else {
                displayToast("Fill in all the fields!")
            }
        }

        binding.btnCancelSignUp.setOnClickListener {
            finish()
        }
    }

    private fun signUp(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    displayToast("Sign up was successful!")
                    val intentToMain = Intent(this, MainActivity::class.java)
                    startActivity(intentToMain)
                } else {
                    task.exception?.message?.let { displayToast(it) }
                }
            }
    }

    private fun displayToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
