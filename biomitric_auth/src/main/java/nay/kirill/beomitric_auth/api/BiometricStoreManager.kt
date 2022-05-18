package nay.kirill.beomitric_auth.api

import android.content.Context
import androidx.fragment.app.FragmentActivity
import nay.kirill.beomitric_auth.impl.BiometricStoreManagerImpl
import java.lang.IllegalStateException

/**
 * Provides API for using biometric authentication capabilities
 */
interface BiometricStoreManager {

    /**
     * Checks if biometric scanners are ready to use and user registered biometric data
     * @param context is Context of current Activity or Application
     * @param biometricType is the class of biometric system to check availability of
     */
    fun isBiometricAvailable(
        context: Context,
        biometricType: BiometricType = BiometricType.FIRST_CLASS
    ): Boolean

    /**
     * Used to authenticate user using not crypto-base biometric system
     */
    fun authenticate(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onFailed: (error: Throwable) -> Unit = { }
    )

    /**
     * Used to encrypt data via biometric authentication
     * @param text is text needed to encrypt
     */
    fun encryptWithBiometric(
        activity: FragmentActivity,
        text: String,
        onSuccess: () -> Unit = { },
        onFailed: (error: Throwable) -> Unit = { }
    )

    /**
     * Used to decrypt data via biometric authentication
     */
    fun decryptWithBiometric(
        activity: FragmentActivity,
        onSuccess: (decryptedText: String) -> Unit,
        onFailed: (error: Throwable) -> Unit = { }
    )

    companion object {
        fun create(): BiometricStoreManager = BiometricStoreManagerImpl()
    }

}
