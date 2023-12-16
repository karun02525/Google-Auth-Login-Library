package com.googlelogin

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes


@Composable
fun rememberOneTapSignInState(): SignInState {
    return rememberSaveable(
        saver = SignInStateSaver
    ) { SignInState() }
}

private const val TAG = "OneTapCompose"

@Composable
fun SignInWithGoogle(
    state: SignInState,
    clientId: String,
    rememberAccount: Boolean = true,
    nonce: String? = null,
    onTokenIdReceived: (String) -> Unit,
    onDialogDismissed: (String) -> Unit,
) {
    val context = LocalContext.current
    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        try {
            if (result.resultCode == Activity.RESULT_OK) {
                val oneTapClient = Identity.getSignInClient(context)
                val credentials = oneTapClient.getSignInCredentialFromIntent(result.data)
                val tokenId = credentials.googleIdToken
                if (tokenId != null) {
                    onTokenIdReceived(tokenId)
                    state.close()
                }
            } else {
                onDialogDismissed("Dialog Closed.")
                state.close()
            }
        } catch (e: ApiException) {
            Log.e(TAG, "${e.message}")
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    onDialogDismissed("Dialog Canceled.")
                    state.close()
                }

                CommonStatusCodes.NETWORK_ERROR -> {
                    onDialogDismissed("Network Error.")
                    state.close()
                }

                else -> {
                    onDialogDismissed(e.message.toString())
                    state.close()
                }
            }
        }
    }

    LaunchedEffect(key1 = state.opened) {
        if (state.opened) {
            signIn(
                context = context,
                clientId = clientId,
                rememberAccount = rememberAccount,
                nonce = nonce,
                launchActivityResult = { intentSenderRequest ->
                    activityLauncher.launch(intentSenderRequest)
                },
                onError = {
                    onDialogDismissed(it)
                    state.close()
                }
            )

        }
    }

}

private fun signIn(
    context: Context,
    clientId: String,
    rememberAccount: Boolean,
    nonce: String?,
    launchActivityResult: (IntentSenderRequest) -> Unit,
    onError: (String) -> Unit
) {
    val oneTapClient = Identity.getSignInClient(context)
    val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setNonce(nonce)
                .setServerClientId(clientId)
                .setFilterByAuthorizedAccounts(rememberAccount)
                .build()
        )
        .setAutoSelectEnabled(true)
        .build()

    oneTapClient.beginSignIn(signInRequest)
        .addOnSuccessListener { result ->
            try {
                launchActivityResult(
                    IntentSenderRequest.Builder(
                        result.pendingIntent.intentSender
                    ).build()
                )
            } catch (e: Exception) {
                onError(e.message.toString())
                Log.e(TAG, "${e.message}")
            }
        }
        .addOnFailureListener {
            signUp(
                context = context,
                clientId = clientId,
                nonce = nonce,
                launchActivityResult = launchActivityResult,
                onError = onError
            )
            Log.e(TAG, "${it.message}")
        }
}



private fun signUp(
    context: Context,
    clientId: String,
    nonce: String?,
    launchActivityResult: (IntentSenderRequest) -> Unit,
    onError: (String) -> Unit
) {
    val oneTapClient = Identity.getSignInClient(context)
    val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setNonce(nonce)
                .setServerClientId(clientId)
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .build()

    oneTapClient.beginSignIn(signInRequest)
        .addOnSuccessListener { result ->
            try {
                launchActivityResult(
                    IntentSenderRequest.Builder(
                        result.pendingIntent.intentSender
                    ).build()
                )
            } catch (e: Exception) {
                onError(e.message.toString())
                Log.e(TAG, "${e.message}")
            }
        }
        .addOnFailureListener {
            onError("Google Account not Found.")
            Log.e(TAG, "${it.message}")
        }

    oneTapClient.signOut()
        .addOnSuccessListener { result ->
            try {
                oneTapClient.signOut()
            } catch (e: Exception) {
                onError(e.message.toString())
                Log.e(TAG, "${e.message}")
            }
        }
        .addOnFailureListener {
            onError("Google Account not Found.")
            Log.e(TAG, "${it.message}")
        }


}


private val SignInStateSaver: Saver<SignInState, Boolean> = Saver(
    save = { state -> state.opened },
    restore = { opened -> SignInState(open = opened) },
)
