package com.googlelogin

import android.util.Log
import androidx.compose.runtime.Immutable
import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT

@Immutable
data class GoogleUser(
    val sub: String?,
    val email: String?,
    val emailVerified: Boolean?,
    val fullName: String?,
    val givenName: String?,
    val familyName: String?,
    val picture: String?,
    val issuedAt: Long?,
    val expirationTime: Long?,
    val locale: String?
)

fun getUserFromTokenId(tokenId: String): GoogleUser? {
    try {
        val jwt = JWT(tokenId)
        return GoogleUser(
            sub = jwt.claims[Model.SUB]?.asString(),
            email = jwt.claims[Model.EMAIL]?.asString(),
            emailVerified = jwt.claims[Model.EMAIL_VERIFIED]?.asBoolean(),
            fullName = jwt.claims[Model.FUll_NAME]?.asString(),
            givenName = jwt.claims[Model.GIVEN_NAME]?.asString(),
            familyName = jwt.claims[Model.FAMILY_NAME]?.asString(),
            picture = jwt.claims[Model.PICTURE]?.asString(),
            issuedAt = jwt.claims[Model.ISSUED_AT]?.asLong(),
            expirationTime = jwt.claims[Model.EXPIRATION_TIME]?.asLong(),
            locale = jwt.claims[Model.LOCALE]?.asString()
        )
    } catch (e: Exception) {
        Log.e("OneTapCompose", e.toString())
        return null
    } catch (e: DecodeException) {
        Log.e("OneTapCompose", e.toString())
        return null
    }
}
