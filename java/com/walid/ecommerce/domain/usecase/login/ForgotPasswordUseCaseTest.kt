package com.walid.ecommerce.domain.usecase.login
import com.walid.ecommerce.common.Resource
import com.walid.ecommerce.domain.repository.Authenticator
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class ForgotPasswordUseCaseTest {

    private lateinit var forgotPasswordUseCase: ForgotPasswordUseCase
    private lateinit var authenticator: Authenticator

    @Before
    fun setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this)
        authenticator = mock(Authenticator::class.java)

        // Initialize use case
        forgotPasswordUseCase = ForgotPasswordUseCase(authenticator)
    }

    @Test
    fun `invoke should return Resource Success when sendPasswordResetEmail is successful`() = runTest {
        // Given
        val email = "test@example.com"

        // Mock the suspend function to return Unit (success)
        // Use Void.TYPE instead of Unit to avoid mismatch between Kotlin Unit and Java Void
        whenever(authenticator.sendPasswordResetEmail(email)).thenReturn(null)  // 'null' represents Void in this case

        // When
        val result = forgotPasswordUseCase.invoke(email)

        // Then
        assertTrue(result is Resource.Success)  // Ensure it returns Success
        assertEquals(Unit, (result as Resource.Success).data) // Expecting Unit (Void in Java)

        // Verify the method was called
        verify(authenticator).sendPasswordResetEmail(email)
    }

    @Test
    fun `invoke should return Resource Error when sendPasswordResetEmail throws an exception`() = runTest {
        // Given
        val email = "test@example.com"
        val exception = RuntimeException("Reset failed")

        // Mock exception case
        whenever(authenticator.sendPasswordResetEmail(email)).thenThrow(exception)

        // When
        val result = forgotPasswordUseCase.invoke(email)

        // Then
        assertTrue(result is Resource.Error)
        assertEquals(exception, (result as Resource.Error).throwable)

        // Verify method was called
        verify(authenticator).sendPasswordResetEmail(email)
    }
}
/** not use void in kotlin class as :
class ForgotPasswordUseCase @Inject constructor(
    private val authenticator: Authenticator
) {
    suspend operator fun invoke(email: String): Resource<Void> {

        return try {
            Resource.Loading
            Resource.Success(authenticator.sendPasswordResetEmail(email))
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}  instead use Unit like that :
class ForgotPasswordUseCase @Inject constructor(
    private val authenticator: Authenticator
) {
    suspend operator fun invoke(email: String): Resource<Unit> {
        return try {
            Resource.Loading
            authenticator.sendPasswordResetEmail(email) // ✅ This is a Void method
            Resource.Success(Unit) // ✅ Return Unit to represent success
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}***because  in class ForgotPasswordUseCaseTest unit  in kotlin
 Unit is used as a return type to represent a method that doesn't
 return any meaningful value, similar to void in Java. But the key difference is that Kotlin's
  Unit can still be passed around as a value, whereas
  Java's Void is more of a placeholder and doesn't hold any value.
  means in unit i can  pass Resource.Success but with void can't hold any things means passed any thing Resource.Success is failed also test
 */