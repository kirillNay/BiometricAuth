package nay.kirill.beomitric_auth.impl.cryptography

import java.nio.charset.Charset
import javax.crypto.Cipher

internal object CryptographyManager {

    fun processCypher(data: String, cipher: Cipher): String = cipher.doFinal(data.asByteArray()).asString()

    private fun String.asByteArray() = toByteArray(Charset.forName("UTF-8"))

    private fun ByteArray.asString() = String(this, Charset.forName("UTF-8"))

}
