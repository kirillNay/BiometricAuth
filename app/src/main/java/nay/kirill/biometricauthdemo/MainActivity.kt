package nay.kirill.biometricauthdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import nay.kirill.beomitric_auth.api.BiometricStoreManager
import nay.kirill.beomitric_auth.api.BiometricType

class MainActivity : AppCompatActivity() {

    private val biometricStoreManager by lazy(BiometricStoreManager.Companion::create)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBiometricUI()
    }

    private fun setupBiometricUI() {
        val biometricUnavailable = findViewById<View>(R.id.biometricUnavailableMessage)
        val authenticateButton = findViewById<View>(R.id.authenticateButton)

        val isBiometricAvailable = biometricStoreManager.isBiometricAvailable(context = this, biometricType = BiometricType.SECOND_CLASS)

        biometricUnavailable.isVisible = !isBiometricAvailable
        authenticateButton.isVisible = isBiometricAvailable

        if (isBiometricAvailable) {
            authenticateButton.setOnClickListener {
                biometricStoreManager.authenticate(
                    activity = this,
                    onSuccess = Toast.makeText(this, "Authentication success!", Toast.LENGTH_LONG)::show,
                    onFailed = { error ->
                        Toast.makeText(this, "Authentication failed with error: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }
}
