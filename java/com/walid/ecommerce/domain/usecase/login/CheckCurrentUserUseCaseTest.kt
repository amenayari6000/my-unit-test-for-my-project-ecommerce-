package com.walid.ecommerce.domain.usecase.login

import com.walid.ecommerce.domain.repository.Authenticator
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
class CheckCurrentUserUseCaseTest {

 private val authenticator: Authenticator = mockk()
 private val checkCurrentUserUseCase = CheckCurrentUserUseCase(authenticator)

 // ✅ Test Case 1: User exists
 @Test
 fun `invoke should return true when current user exists`() = runTest {
  coEvery { authenticator.isCurrentUserExist() } returns true

  val result = checkCurrentUserUseCase()

  assertEquals(true, result)
  coVerify { authenticator.isCurrentUserExist() }
 }

 // ✅ Test Case 2: User does not exist
 @Test
 fun `invoke should return false when current user does not exist`() = runTest {
  coEvery { authenticator.isCurrentUserExist() } returns false

  val result = checkCurrentUserUseCase()

  assertEquals(false, result)
  coVerify { authenticator.isCurrentUserExist() }
 }
}
