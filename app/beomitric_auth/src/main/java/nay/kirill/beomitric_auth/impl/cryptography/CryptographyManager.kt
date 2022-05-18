package nay.kirill.beomitric_auth.impl.cryptography

import java.nio.charset.Charset
import javax.crypto.Cipher

internal object CryptographyManager {

    fun encryptData(text: String, cipher: Cipher): ByteArray =
        cipher.doFinal(text.toByteArray(Charset.forName("UTF-8")))

    fun decryptData(data: ByteArray, cipher: Cipher): String {
        val plaintext = cipher.doFinal(data)
        return String(plaintext, Charset.forName("UTF-8"))
    }

}
