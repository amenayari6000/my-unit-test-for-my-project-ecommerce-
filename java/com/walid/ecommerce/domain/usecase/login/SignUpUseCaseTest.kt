package com.walid.ecommerce.domain.usecase.login

import com.walid.ecommerce.common.Resource
import com.walid.ecommerce.data.model.User
import com.walid.ecommerce.domain.repository.Authenticator
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.whenever

class SignUpUseCaseTest {

    private lateinit var signUpUseCase: SignUpUseCase
    private lateinit var authenticator: Authenticator

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        authenticator = mock(Authenticator::class.java)

        signUpUseCase = SignUpUseCase(authenticator)
    }

    @Test
    fun `invoke should return Resource Success when signUpWithEmailAndPassword is successful`() = runTest {
        // Given
        val user = User(email = "test@example.com", nickname = "TestUser", phoneNumber = "123456789")
        val password = "password123"

        // Mock successful signup by ensuring the function runs without throwing an exception
        whenever(authenticator.signUpWithEmailAndPassword(user, password)).thenReturn(Unit)

        // When
        val result = signUpUseCase.invoke(user, password)

        // Then
        assertTrue(result is Resource.Success)

        // Verify that signUpWithEmailAndPassword() was called with correct parameters
        verify(authenticator).signUpWithEmailAndPassword(user, password)
    }

    @Test
    fun `invoke should return Resource Error when signUpWithEmailAndPassword throws an exception`() = runTest {
        // Given
        val user = User(email = "test@example.com", nickname = "TestUser", phoneNumber = "123456789")
        val password = "password123"
        val exception = RuntimeException("Signup failed")

        // Mock exception case
        whenever(authenticator.signUpWithEmailAndPassword(user, password)).thenThrow(exception)

        // When
        val result = signUpUseCase.invoke(user, password)

        // Then
        assertTrue(result is Resource.Error)
        assertEquals(exception, (result as Resource.Error).throwable)

        // Verify that signUpWithEmailAndPassword() was called
        verify(authenticator).signUpWithEmailAndPassword(user, password)
    }
}
