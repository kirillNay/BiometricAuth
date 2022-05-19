package nay.kirill.beomitric_auth.impl

import android.util.Base64

fun String.asByteArray(): ByteArray = Base64.decode(this, Base64.DEFAULT)

fun ByteArray.asString(): String = Base64.encodeToString(this, Base64.DEFAULT)
