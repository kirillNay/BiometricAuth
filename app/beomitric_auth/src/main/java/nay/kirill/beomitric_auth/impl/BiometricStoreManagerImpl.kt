package nay.kirill.beomitric_auth.impl

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import nay.kirill.beomitric_auth.R
import nay.kirill.beomitric_auth.api.BiometricStoreManager
import nay.kirill.beomitric_auth.api.BiometricType
import nay.kirill.beomitric_auth.impl.cryptography.CipherManager
import nay.kirill.beomitric_auth.impl.cryptography.CryptographyManager
import javax.crypto.Cipher

internal class BiometricStoreManagerImpl : BiometricStoreManager {

    override fun isBiometricAvailable(
        context: Context,
        biometricType: BiometricType
    ): Boolean = BiometricManager.from(context).canAuthenticate(biometricType.authenticator) == BiometricManager.BIOMETRIC_SUCCESS

    override fun authenticate(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onFailed: (error: BiometricStoreManager.AuthError) -> Unit
    ) {
        val promptInfo = createPromptInfo(activity, biometricType = BiometricType.SECOND_CLASS)
        val biometricPrompt = createBiometricPrompt(activity, onSuccess = { onSuccess.invoke() }, onFailed)
        biometricPrompt.authenticate(promptInfo)
    }

    override fun encryptWithBiometric(
        activity: FragmentActivity,
        text: String,
        onSuccess: () -> Unit,
        onFailed: (error: BiometricStoreManager.AuthError) -> Unit
    ) {
        val promptInfo = createPromptInfo(activity, biometricType = BiometricType.FIRST_CLASS)
        val biometricPrompt = createBiometricPrompt(
            activity = activity,
            onSuccess = { cipher ->
                CryptographyManager.encryptData(text, cipher)

                // TODO save encrypted data

                onSuccess.invoke()
            },
            onFailed = onFailed
        )
        val cipher = CipherManager.getCipher(cryptographyMode = CipherManager.CryptographyMode.ENCRYPTION)

        biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    }

    override fun decryptWithBiometric(
        activity: FragmentActivity,
        onSuccess: (decryptedText: String) -> Unit,
        onFailed: (error: BiometricStoreManager.AuthError) -> Unit
    ) {
        val promptInfo = createPromptInfo(activity, biometricType = BiometricType.FIRST_CLASS)
        val biometricPrompt = createBiometricPrompt(
            activity = activity,
            onSuccess = { cipher ->
                //TODO get saved data
            },
            onFailed = onFailed
        )
        val cipher = CipherManager.getCipher(cryptographyMode = CipherManager.CryptographyMode.DECRYPTION)

        biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    }

    private fun createBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: (Cipher) -> Unit,
        onFailed: (error: BiometricStoreManager.AuthError) -> Unit
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                result.cryptoObject?.cipher?.let(onSuccess)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onFailed.invoke(BiometricStoreManager.AuthError(errorCode = errorCode, message = errString.toString()))
            }
        }

        return BiometricPrompt(activity, executor, callback)
    }

    private fun createPromptInfo(
        context: Context,
        biometricType: BiometricType
    ): BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder()
        .apply {
            setTitle(context.getString(R.string.android_biometric_dialog_title))
            setDescription(context.getString(R.string.android_biometric_dialog_description))
            setConfirmationRequired(false)
            setNegativeButtonText(context.getString(R.string.android_biometric_dialog_negative_button))
            setAllowedAuthenticators(biometricType.authenticator)
        }
        .build()

}
