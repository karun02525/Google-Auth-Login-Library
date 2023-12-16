package com.googlelogin

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


@Stable
class SignInState(open: Boolean = false, signOut: Boolean = false) {
    var opened by mutableStateOf(open)
        private set

    var signOut by mutableStateOf(signOut)
        private set

    fun open() {
        opened = true
    }

    fun signOut(){
        signOut=true
    }

    internal fun close() {
        opened = false
    }
}
