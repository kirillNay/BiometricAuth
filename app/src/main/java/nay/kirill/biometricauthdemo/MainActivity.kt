package nay.kirill.biometricauthdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
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
        val encryptButton = findViewById<View>(R.id.encryptButton)
        val decryptButton = findViewById<View>(R.id.decryptButton)

        val textToEncryptView = findViewById<EditText>(R.id.textToEncrypt)

        val isBiometricAvailable = biometricStoreManager.isBiometricAvailable(context = this, biometricType = BiometricType.SECOND_CLASS)

        biometricUnavailable.isVisible = !isBiometricAvailable
        authenticateButton.isVisible = isBiometricAvailable
        encryptButton.isVisible = isBiometricAvailable
        decryptButton.isVisible = isBiometricAvailable
        textToEncryptView.isVisible = isBiometricAvailable

        if (isBiometricAvailable) {
            authenticateButton.setOnClickListener {
                biometricStoreManager.authenticate(
                    activity = this,
                    onSuccess = Toast.makeText(this, "Аутентификация успешна!", Toast.LENGTH_LONG)::show,
                    onFailed = { error ->
                        Toast.makeText(this, "Аутентификация завершилась с ошибкой ${error.message}", Toast.LENGTH_LONG).show()
                    }
                )
            }

            encryptButton.setOnClickListener {
                biometricStoreManager.encryptWithBiometric(
                    activity = this,
                    text = textToEncryptView.text.toString(),
                    onSuccess = Toast.makeText(this, "Текст зашифрован", Toast.LENGTH_LONG)::show,
                    onFailed = { error ->
                        Toast.makeText(this, "Не удалось зашифровать текст. ${error.message}", Toast.LENGTH_LONG).show()
                    }
                )
            }

            decryptButton.setOnClickListener {
                biometricStoreManager.decryptWithBiometric(
                    activity = this,
                    onSuccess = { text ->
                        Toast.makeText(this, "Текст расшифрован: $text", Toast.LENGTH_LONG).show()
                    },
                    onFailed = { error ->
                        Toast.makeText(this, "Не удалось расшифровать текст. ${error.message}", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }
}
