package nay.kirill.beomitric_auth

import android.content.Context

/**
 * Provides API for using biometric authentication capabilities
 */
interface BiometricStoreManager {

    /**
     * Checks if biometric scanners are ready to use and user registered biometric data
     */
    fun isBiometricAvailable(
        context: Context,
        biometricType: BiometricType = BiometricType.FIRST_CLASS
    ): Boolean

}
