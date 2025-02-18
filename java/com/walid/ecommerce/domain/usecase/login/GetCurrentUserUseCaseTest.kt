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

class GetCurrentUserUseCaseTest {

    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
    private lateinit var authenticator: Authenticator

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        authenticator = mock(Authenticator::class.java)
        getCurrentUserUseCase = GetCurrentUserUseCase(authenticator)
    }

    @Test
    fun `invoke should return Resource Success when getCurrentUser is successful`() = runTest {
        // Given
        val user = User(email = "test@example.com", nickname = "testUser", phoneNumber = "123456789")
        `when`(authenticator.getCurrentUser()).thenReturn(user)

        // When
        val result = getCurrentUserUseCase.invoke()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(user, (result as Resource.Success).data)

        // Verify that getCurrentUser() was called
        verify(authenticator).getCurrentUser()
    }

    @Test
    fun `invoke should return Resource Error when getCurrentUser throws an exception`() = runTest {
        // Given
        val exception = RuntimeException("User not found")
        `when`(authenticator.getCurrentUser()).thenThrow(exception)

        // When
        val result = getCurrentUserUseCase.invoke()

        // Then
        assertTrue(result is Resource.Error)
        assertEquals(exception, (result as Resource.Error).throwable) // Updated to match Resource.Error's property name

        // Verify that getCurrentUser() was called
        verify(authenticator).getCurrentUser()
    }
}
