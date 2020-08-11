package dev.lynnik.biometricauth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var biometricManager: BiometricManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBiometric()
        setupUi()
    }

    private fun setupBiometric() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, promptCallback)
        promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build()
        biometricManager = BiometricManager.from(this)
    }

    private fun setupUi() {
        signInButton.setOnClickListener { view ->
            authenticate()
            view.performHapticFeedback(
                    HapticFeedbackConstants.KEYBOARD_TAP,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
        }
    }

    private fun authenticate() {
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                toast("No biometric features available on this device")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                toast("Biometric features are currently unavailable")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                toast("The user hasn't associated any biometric credentials with their account")
            }
        }
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private val promptCallback = object : BiometricPrompt.AuthenticationCallback() {
        // is called when an unrecoverable error occurs
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            toast("Authentication error: $errString")
        }

        // is called when the user has been authenticated
        // using a credential that the device recognizes
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            toast("Authentication succeeded!")
        }

        // is called when the user is rejected,
        // for example when a non-enrolled fingerprint is placed on the sensor,
        // but unlike with onAuthenticationError(), the user can continue trying to authenticate
        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            toast("Authentication failed")
        }
    }
}