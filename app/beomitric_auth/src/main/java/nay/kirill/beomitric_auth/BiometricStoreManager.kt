package nay.kirill.beomitric_auth

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentActivity

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
        onFailed: (error: AuthError) -> Unit
    )

    data class AuthError(
        val errorCode: Int,
        val message: String
    )

}
