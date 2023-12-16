package com.googlelogin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun OneTapSignInWithGoogleButton(
    clientId: String,
    state: SignInState = rememberOneTapSignInState(),
    rememberAccount: Boolean = true,
    nonce: String? = null,
    onTokenIdReceived: ((String) -> Unit)? = null,
    onUserReceived: ((GoogleUser) -> Unit)? = null,
    onDialogDismissed: ((String) -> Unit)? = null,
    iconOnly: Boolean = false,
    theme: GoogleButtonTheme = if (isSystemInDarkTheme()) GoogleButtonTheme.Dark
    else GoogleButtonTheme.Light,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = when (theme) {
            GoogleButtonTheme.Light -> Color.White
            GoogleButtonTheme.Dark -> Color(0xFF131314)
            GoogleButtonTheme.Neutral -> Color(0xFFF2F2F2)
        },
        contentColor = when (theme) {
            GoogleButtonTheme.Dark -> Color(0xFFE3E3E3)
            else -> Color(0xFF1F1F1F)
        },
    ),
    border: BorderStroke? = when (theme) {
        GoogleButtonTheme.Light -> BorderStroke(
            width = 1.dp,
            color = Color(0xFF747775),
        )

        GoogleButtonTheme.Dark -> BorderStroke(
            width = 1.dp,
            color = Color(0xFF8E918F),
        )

        GoogleButtonTheme.Neutral -> null
    },
    shape: Shape = ButtonDefaults.shape,
    onClickSignOut: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    SignInWithGoogle(
        state = state,
        clientId = clientId,
        rememberAccount = rememberAccount,
        nonce = nonce,
        onTokenIdReceived = { tokenId ->
            onTokenIdReceived?.invoke(tokenId)
            getUserFromTokenId(tokenId = tokenId)?.let { googleUser ->
                onUserReceived?.invoke(googleUser)
            }
        },
        onDialogDismissed = { message ->
            onDialogDismissed?.invoke(message)
        }
    )

    Button(
        modifier = Modifier.width(if (iconOnly) 40.dp else Dp.Unspecified),
        onClick = {
            state.open()
            onClick?.invoke()
        },
        shape = shape,
        colors = colors,
        contentPadding = PaddingValues(horizontal = if (iconOnly) 9.5.dp else 12.dp),
        border = border,
    ) {
        Box(
            modifier = Modifier
                .padding(end = if (iconOnly) 0.dp else 10.dp)
                .paint(painter = painterResource(id = R.drawable.google_logo))
        )
        if (!iconOnly) {
            Text(
                text = "Sign in with Google",
                maxLines = 1,
                fontFamily = RobotoFontFamily,
            )
        }
    }

    Button(
        modifier = Modifier.width(if (iconOnly) 40.dp else Dp.Unspecified),
        onClick = {
            state.signOut()
            onClickSignOut?.invoke()
        },
        shape = shape,
        colors = colors,
        contentPadding = PaddingValues(horizontal = if (iconOnly) 9.5.dp else 12.dp),
        border = border,
    ) {
        Box(
            modifier = Modifier
                .padding(end = if (iconOnly) 0.dp else 10.dp)
                .paint(painter = painterResource(id = R.drawable.google_logo))
        )
        if (!iconOnly) {
            Text(
                text = "Logout",
                maxLines = 1,
                fontFamily = RobotoFontFamily,
            )
        }
    }
}

enum class GoogleButtonTheme { Light, Dark, Neutral }

private val RobotoFontFamily = FontFamily(
    Font(
        resId = R.font.roboto_medium,
        weight = FontWeight.Medium,
        style = FontStyle.Normal
    )
)
