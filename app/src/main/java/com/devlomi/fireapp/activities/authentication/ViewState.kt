package com.devlomi.fireapp.activities.authentication

import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthCredential

sealed class ViewState {
    data class Verify(val phoneNumber: String) : ViewState()


}