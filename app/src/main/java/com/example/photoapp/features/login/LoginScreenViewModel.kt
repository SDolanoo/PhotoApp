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
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor (): ViewModel() {
    // val loadingState = MutableStateFlow(LoadingState.IDLE)
    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading


    fun signInWithEmailAndPassword(email: String, password: String, home: () -> Unit )
            = viewModelScope.launch {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Log.d("Dolan", "signInWithEmailAndPassword: Yayayay! ${task.result.toString()}")
                        //Todo: take them home
                        home()
                    }else {
                        Log.d("Dolan", "signInWithEmailAndPassword: ${task.result.toString()}")
                    }
                }
        } catch (ex: Exception) {
            Log.d("Dolan", "signInWithEmailAndPassword: ${ex.message}")
        }


    }



    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        home: () -> Unit) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //me
                        val displayName = task.result?.user?.email?.split('@')?.get(0)
                        createUser(displayName)
                        home()
                    }else {
                        Log.d("FB", "createUserWithEmailAndPassword: ${task.result.toString()}")

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