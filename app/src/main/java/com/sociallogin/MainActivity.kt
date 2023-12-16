package com.sociallogin

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.googlelogin.GoogleUser
import com.googlelogin.SignInWithGoogle
import com.googlelogin.getUserFromTokenId
import com.googlelogin.rememberOneTapSignInState
import com.sociallogin.ui.theme.SocialLoginTheme

class MainActivity : ComponentActivity() {
    //  https://developers.google.com/identity/oauth2/web/guides/get-google-api-clientid
    //https://stackoverflow.com/questions/27609442/how-to-get-the-sha-1-fingerprint-certificate-in-android-studio-for-debug-mode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SocialLoginTheme {
                val state = rememberOneTapSignInState()
                var user: GoogleUser? by remember { mutableStateOf(null) }
                SignInWithGoogle(
                    state = state,
                     clientId = "YOUR_CLIENT_ID",
                    onTokenIdReceived = {
                        user = getUserFromTokenId(tokenId = it)
                        Log.d("MainActivity", user.toString())
                    },
                    onDialogDismissed = {
                        Log.d("MainActivity", it)
                    }
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.weight(2f),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = { state.open() },
                            enabled = !state.opened
                        ) {
                            Text(text = "Sign in")
                        }
                    }
                    Column(
                        modifier = Modifier
                            .weight(10f)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (user != null) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("EMAIL: ")
                                    }
                                    append(user!!.email)
                                }
                            )
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("EMAIL VERIFIED: ")
                                    }
                                    append(user!!.emailVerified.toString())
                                }
                            )
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("FULL NAME: ")
                                    }
                                    append(user!!.fullName)
                                }
                            )
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("GIVEN NAME: ")
                                    }
                                    append(user!!.givenName)
                                }
                            )
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("FAMILY NAME: ")
                                    }
                                    append(user!!.familyName)
                                }
                            )
                            AsyncImage(
                                model = user!!.picture,
                                contentDescription = null,
                                modifier = Modifier.size(100.dp)
                            )
                        } else {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                text = "loading..."
                            )
                        }
                    }
                }
            }
        }
    }
}


