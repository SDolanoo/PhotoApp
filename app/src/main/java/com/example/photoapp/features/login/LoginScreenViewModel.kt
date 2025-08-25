package com.example.photoapp.features.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.core.database.data.entities.Uzytkownik
import com.example.photoapp.core.utils.convertDateToString
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.Date

class LoginScreenViewModel(
    private val auth: FirebaseAuth = Firebase.auth // val loadingState = MutableStateFlow(LoadingState.IDLE)
): ViewModel() {


    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading


    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        home: () -> Unit,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Dolan", "signInWithEmailAndPassword: Logging in! ${task.result}")
                        home()
                    } else {
                        val exception = task.exception
                        val errorMessage = when (exception) {
                            is FirebaseAuthInvalidUserException -> "Użytkownik nie istnieje"
                            is FirebaseAuthInvalidCredentialsException -> "Nieprawidłowy email lub hasło"
                            is FirebaseAuthException -> "Błąd logowania: ${exception.message}"
                            else -> "Coś poszło nie tak"
                        }
                        Log.e("Dolan", "signInWithEmailAndPassword: $errorMessage", exception)
                        onError(errorMessage)
                    }
                }
        } catch (ex: Exception) {
            val errorMessage = "Wyjątek logowania: ${ex.message ?: "Nieznany błąd"}"
            Log.e("Dolan", "signInWithEmailAndPassword: $errorMessage", ex)
            onError(errorMessage)
        }
    }




    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        home: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val displayName = task.result?.user?.email?.split('@')?.get(0)
                        createUser(displayName)
                        home()
                    } else {
                        val errorMessage = when (val exception = task.exception) {
                            is FirebaseAuthUserCollisionException -> "Ten email jest już zarejestrowany"
                            is FirebaseAuthWeakPasswordException -> "Hasło jest za słabe"
                            is FirebaseAuthInvalidCredentialsException -> "Nieprawidłowy email"
                            is FirebaseAuthException -> "Błąd autoryzacji: ${exception.message}"
                            else -> "Coś poszło nie tak"
                        }
                        Log.e("FB", "createUserWithEmailAndPassword: $errorMessage", task.exception)
                        onError(errorMessage)
                    }
                    _loading.value = false
                }
        }
    }


    private fun createUser(displayName: String?) {
        val now = Date()
        val userId = auth.currentUser?.uid

        val user = Uzytkownik(
            id = null,
            userId = userId.toString(),
            displayName = displayName.orEmpty(),
            createdAt = convertDateToString(now),
            lastLogin = convertDateToString(now),
            plan = "free",
            isActive = true,
            supportNotes = "",
            invoiceCount = 0,
            email = auth.currentUser?.email.orEmpty(),
            phone = auth.currentUser?.phoneNumber.orEmpty()
        ).toMap()


        FirebaseFirestore.getInstance().collection("users")
            .add(user)
    }
}