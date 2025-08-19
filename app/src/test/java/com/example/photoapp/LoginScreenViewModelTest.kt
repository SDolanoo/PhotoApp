package com.example.photoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.photoapp.features.login.LoginScreenViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.intArrayOf

@OptIn(ExperimentalCoroutinesApi::class)
@Config(sdk = [33])
@RunWith(RobolectricTestRunner::class)
class LoginScreenViewModelTest {

    private lateinit var auth: FirebaseAuth
    private lateinit var authResult: AuthResult
    private lateinit var authTask: Task<AuthResult>
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var viewModel: LoginScreenViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        auth = mockk()
        authResult = mockk()
        authTask = mockk(relaxed = true)
        firebaseUser = mockk()

        viewModel = LoginScreenViewModel(auth)
    }

    @After
    fun tearDown() {
        Dispatchers.setMain(Dispatchers.Default)
        unmockkAll()
    }

    @Test
    fun `signInWithEmailAndPassword should call home when sign in is successful`() = runTest {
        val latch = CountDownLatch(1)

        // Given
        val email = "test@example.com"
        val password = "password"
        val homeCalled = AtomicBoolean(false)

        every { auth.signInWithEmailAndPassword(email, password) } returns authTask
        every { authTask.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<AuthResult>>()
            every { authTask.isSuccessful } returns true
            listener.onComplete(authTask)
            authTask
        }

        // When
        viewModel.signInWithEmailAndPassword(email, password) {
            homeCalled.set(true)
            latch.countDown()
        }

        advanceUntilIdle()
        latch.await(3, TimeUnit.SECONDS)

        // Then
        Assert.assertTrue(homeCalled.get())
    }

    @Test
    fun `createUserWithEmailAndPassword should create user and call home when successful`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password"
        val homeCalled = AtomicBoolean(false)

        val task: Task<AuthResult> = mockk()
        val user: FirebaseUser = mockk()

        every { auth.currentUser?.uid } returns "uid123"
        every { user.uid } returns "uid123"
        every { auth.createUserWithEmailAndPassword(email, password) } returns task
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<AuthResult>>()
            every { task.isSuccessful } returns true
            every { task.result } returns authResult
            every { authResult.user } returns user
            every { user.email } returns email
            every { user.uid } returns "uid123"
            every { auth.currentUser } returns user
            every { auth.currentUser?.email } returns email
            every { auth.currentUser?.phoneNumber } returns null // aa moja gowa

            listener.onComplete(task)
            task
        }

        // Mock Firestore add
        mockkStatic(FirebaseFirestore::class)
        val collection = mockk<CollectionReference>(relaxed = true)
        every { FirebaseFirestore.getInstance().collection("users") } returns collection
        every { collection.add(any()) } returns mockk()

        // When
        viewModel.createUserWithEmailAndPassword(email, password) {
            homeCalled.set(true)
        }

        // Then
        Assert.assertTrue(homeCalled.get())
        Assert.assertEquals(false, viewModel.loading.value)
    }

    @Test
    fun `signInWithEmailAndPassword should NOT call home when task is not successful`() = runTest {
        // Given
        val email = "wrong@example.com"
        val password = "wrongPass"
        val homeCalled = AtomicBoolean(false)

        every { auth.signInWithEmailAndPassword(email, password) } returns authTask
        every { authTask.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<AuthResult>>()
            every { authTask.isSuccessful } returns false
            listener.onComplete(authTask)
            authTask
        }

        // When
        viewModel.signInWithEmailAndPassword(email, password) {
            homeCalled.set(true)
        }

        // Then
        Assert.assertFalse(homeCalled.get())
    }

    @Test
    fun `createUserWithEmailAndPassword should NOT call home on failure`() = runTest {
        // Given
        val email = "fail@example.com"
        val password = "fail123"
        val homeCalled = AtomicBoolean(false)

        every { auth.createUserWithEmailAndPassword(email, password) } returns authTask
        every { authTask.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<AuthResult>>()
            every { authTask.isSuccessful } returns false
            listener.onComplete(authTask)
            authTask
        }

        // When
        viewModel.createUserWithEmailAndPassword(email, password) {
            homeCalled.set(true)
        }

        // Then
        Assert.assertFalse(homeCalled.get())
        Assert.assertEquals(false, viewModel.loading.value)
    }

    @Test
    fun `createUserWithEmailAndPassword should do nothing if loading is true`() = runTest {
        // Given
        val email = "skip@example.com"
        val password = "skip123"

        // Wymuszamy loading = true
        val loadingField = LoginScreenViewModel::class.java.getDeclaredField("_loading")
        loadingField.isAccessible = true
        loadingField.set(viewModel, MutableLiveData(true))

        // Then: auth.createUserWithEmailAndPassword nie zostaje wywołane
        // When
        viewModel.createUserWithEmailAndPassword(email, password) {}

        verify(exactly = 0) {
            auth.createUserWithEmailAndPassword(any(), any())
        }
    }


}
