package nay.kirill.beomitric_auth.impl.biometric

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import nay.kirill.beomitric_auth.R
import nay.kirill.beomitric_auth.api.BiometricType
import nay.kirill.beomitric_auth.impl.BiometricStoreManagerImpl
import javax.crypto.Cipher

internal object BiometricUtils {

    fun createBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: (Cipher?) -> Unit,
        onFailed: (error: Throwable) -> Unit
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess.invoke(result.cryptoObject?.cipher)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onFailed.invoke(BiometricStoreManagerImpl.BiometricAuthException(errorMessage = errString.toString()))
            }
        }

        return BiometricPrompt(activity, executor, callback)
    }

    fun createPromptInfo(
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
