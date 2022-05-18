package nay.kirill.beomitric_auth

import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK

/**
 * Defines type of biometric system by its security efficiency
 */
enum class BiometricType(val authenticator: Int) {

    /**
     * Biometric systems of the first class can be used to reach Android KeyStore
     */
    FIRST_CLASS(authenticator = BIOMETRIC_STRONG),

    /**
     * Biometric systems of the second class can be used just to authenticate user.
     * All systems of second class also includes systems of the first one.
     */
    SECOND_CLASS(authenticator = BIOMETRIC_WEAK)

}
