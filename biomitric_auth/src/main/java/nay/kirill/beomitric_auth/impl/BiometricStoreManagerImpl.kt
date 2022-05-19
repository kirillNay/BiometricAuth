package nay.kirill.beomitric_auth.impl

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import nay.kirill.beomitric_auth.R
import nay.kirill.beomitric_auth.api.BiometricStoreManager
import nay.kirill.beomitric_auth.api.BiometricType
import nay.kirill.beomitric_auth.impl.cryptography.CipherManager
import nay.kirill.beomitric_auth.impl.cryptography.CryptographyManager
import nay.kirill.beomitric_auth.impl.storage.DataStorage
import javax.crypto.Cipher

internal class BiometricStoreManagerImpl : BiometricStoreManager {

    override fun isBiometricAvailable(
        context: Context,
        biometricType: BiometricType
    ): Boolean = BiometricManager.from(context).canAuthenticate(biometricType.authenticator) == BiometricManager.BIOMETRIC_SUCCESS

    override fun authenticate(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onFailed: (error: Throwable) -> Unit
    ) {
        val promptInfo = createPromptInfo(activity, biometricType = BiometricType.SECOND_CLASS)
        val biometricPrompt = createBiometricPrompt(activity, onSuccess = { onSuccess.invoke() }, onFailed)
        biometricPrompt.authenticate(promptInfo)
    }

    override fun encryptWithBiometric(
        activity: FragmentActivity,
        text: String,
        onSuccess: () -> Unit,
        onFailed: (error: Throwable) -> Unit
    ) {
        val promptInfo = createPromptInfo(activity, biometricType = BiometricType.FIRST_CLASS)
        val biometricPrompt = createBiometricPrompt(
            activity = activity,
            onSuccess = { cipher ->
                try {
                    if (cipher == null) throw EmptyCipherException()

                    val encryptedValue = CryptographyManager.processCypher(text, cipher)
                    val initializeVector = cipher.iv.asString()

                    activity.lifecycleScope.launchWhenCreated {
                        DataStorage(context = activity)
                            .saveData(data = DataStorage.EncryptedData(encryptedValue, initializeVector))
                        onSuccess.invoke()
                    }
                } catch (e: Throwable) {
                    onFailed.invoke(e)
                }
            },
            onFailed = onFailed
        )
        val cipher = CipherManager.getCipher(cryptographyMode = CipherManager.CryptographyMode.ENCRYPTION)

        biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    }

    override fun decryptWithBiometric(
        activity: FragmentActivity,
        onSuccess: (decryptedText: String) -> Unit,
        onFailed: (error: Throwable) -> Unit
    ) {
        activity.lifecycleScope.launchWhenCreated {
            DataStorage(context = activity).getData().collect { encryptedData ->
                try {
                    if (encryptedData == null) throw EmptyDataToDecryptException()

                    val promptInfo = createPromptInfo(activity, biometricType = BiometricType.FIRST_CLASS)

                    val biometricPrompt = createBiometricPrompt(
                        activity = activity,
                        onSuccess = { cipher ->
                            when (cipher) {
                                null -> onFailed.invoke(EmptyCipherException())
                                else -> {
                                    val decryptedText = CryptographyManager.processCypher(encryptedData.encryptedValue, cipher)
                                    onSuccess.invoke(decryptedText)
                                }
                            }
                        },
                        onFailed = onFailed
                    )
                    val authenticateCipher = CipherManager.getCipher(
                        cryptographyMode = CipherManager.CryptographyMode.DECRYPTION,
                        initializeVector = encryptedData.initializeVector.asByteArray()
                    )
                    biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(authenticateCipher))
                } catch (e: Throwable) {
                    onFailed.invoke(e)
                }
            }
        }
    }

    private fun createBiometricPrompt(
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
                onFailed.invoke(BiometricAuthException(errorMessage = errString.toString()))
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

    class EmptyDataToDecryptException : IllegalStateException("No data has been encrypted yet")

    class EmptyCipherException : IllegalStateException("Cipher can't be null")

    class BiometricAuthException(errorMessage: String) :
        IllegalStateException("Biometric authentication failed with message: $errorMessage")

}
