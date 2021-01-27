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
    private var firebaseAuth: FirebaseAuth? = null

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
                    firebaseAuth?.createUserWithEmailAndPassword(email, password)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Sign up was successful!", Toast.LENGTH_SHORT).show()
                                val intentToMain = Intent(this, MainActivity::class.java)
                                startActivity(intentToMain)
                            } else {
                                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    binding.tePassword.setText("")
                    binding.tePasswordConfirm.setText("")
                    Toast.makeText(this, "Make sure your passwords match!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fill in all the fields!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancelSignUp.setOnClickListener{
            finish()
        }
    }
}
