package com.walid.ecommerce.domain.usecase.login

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.walid.ecommerce.common.Resource
import com.walid.ecommerce.domain.repository.Authenticator
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class SignInUseCaseTest {

    private lateinit var signInUseCase: SignInUseCase
    private lateinit var authenticator: Authenticator
    private lateinit var authResult: AuthResult
    private lateinit var firebaseUser: FirebaseUser

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        authenticator = mock(Authenticator::class.java)
        authResult = mock(AuthResult::class.java)
        firebaseUser = mock(FirebaseUser::class.java)

        signInUseCase = SignInUseCase(authenticator)
    }

    @Test
    fun `invoke should return Resource Success when signInWithEmailAndPassword is successful`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"

        `when`(authenticator.signInWithEmailAndPassword(email, password)).thenReturn(authResult)
        `when`(authResult.user).thenReturn(firebaseUser)

        // When
        val result = signInUseCase.invoke(email, password)

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(firebaseUser, (result as Resource.Success).data)

        // Verify that signInWithEmailAndPassword() was called
        verify(authenticator).signInWithEmailAndPassword(email, password)
    }

    @Test
    fun `invoke should return Resource Error when signInWithEmailAndPassword throws an exception`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val exception = RuntimeException("Invalid credentials")

        `when`(authenticator.signInWithEmailAndPassword(email, password)).thenThrow(exception)

        // When
        val result = signInUseCase.invoke(email, password)

        // Then
        assertTrue(result is Resource.Error)
        assertEquals(exception, (result as Resource.Error).throwable) // Updated to match Resource.Error's property name

        // Verify that signInWithEmailAndPassword() was called
        verify(authenticator).signInWithEmailAndPassword(email, password)
    }
}
