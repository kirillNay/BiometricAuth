package nay.kirill.beomitric_auth.impl.cryptography

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import nay.kirill.beomitric_auth.impl.cryptography.CipherManager.EncryptionConst.ENCRYPTION_ALGORITHM
import nay.kirill.beomitric_auth.impl.cryptography.CipherManager.EncryptionConst.ENCRYPTION_BLOCK_MODE
import nay.kirill.beomitric_auth.impl.cryptography.CipherManager.EncryptionConst.ENCRYPTION_PADDING
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec

internal object CipherManager {

    private const val SECRET_KEY = "90Ea7rxQRtCd"

    private const val KEY_SIZE = 256
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"

    private object EncryptionConst {
        const val ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        const val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        const val ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    }

    fun getCipher(cryptographyMode: CryptographyMode, initializeVector: ByteArray? = null): Cipher {
        val cipher = createCipher()
        val secretKey = getOrCreateSecretKey(SECRET_KEY)
        when (initializeVector) {
            null -> cipher.init(cryptographyMode.cipherMode, secretKey)
            else -> cipher.init(cryptographyMode.cipherMode, secretKey, GCMParameterSpec(128, initializeVector))
        }

        return cipher
    }

    private fun createCipher(): Cipher {
        val transformation = "$ENCRYPTION_ALGORITHM/$ENCRYPTION_BLOCK_MODE/$ENCRYPTION_PADDING"
        return Cipher.getInstance(transformation)
    }

    private fun getOrCreateSecretKey(keyName: String): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        keyStore.getKey(keyName, null)?.let { return it as SecretKey }

        val paramsBuilder = KeyGenParameterSpec.Builder(
            keyName,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
        paramsBuilder.apply {
            setBlockModes(ENCRYPTION_BLOCK_MODE)
            setEncryptionPaddings(ENCRYPTION_PADDING)
            setKeySize(KEY_SIZE)
            setUserAuthenticationRequired(true)
        }

        val keyGenParams = paramsBuilder.build()
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        keyGenerator.init(keyGenParams)
        return keyGenerator.generateKey()
    }

    internal enum class CryptographyMode(val cipherMode: Int) {

        ENCRYPTION(cipherMode = Cipher.ENCRYPT_MODE),

        DECRYPTION(cipherMode = Cipher.DECRYPT_MODE)
    }

}
