package com.walid.ecommerce.domain.usecase.bag

import com.walid.ecommerce.common.Resource
import com.walid.ecommerce.data.model.CRUDResponse
import com.walid.ecommerce.domain.repository.ProductsRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

@ExperimentalCoroutinesApi
class ClearBagUseCaseTest {

 private val repository: ProductsRepository = mockk()
 private val clearBagUseCase = ClearBagUseCase(repository)

 // ✅ Test Case 1: Successfully clear bag
 @Test
 fun `invoke should return success when clearing bag`() = runTest {
  val expectedResponse = CRUDResponse(status = 1, message = "Bag cleared successfully")

  // Mock repository response
  coEvery { repository.clearBag() } returns expectedResponse

  val result = clearBagUseCase()

  assertEquals(Resource.Success(expectedResponse), result)
  coVerify { repository.clearBag() }
 }

 // ✅ Test Case 2: Handle HttpException (server error)
 @Test
 fun `invoke should return error when HttpException occurs`() = runTest {
  val exception = mockk<HttpException>(relaxed = true)
  every { exception.message } returns "Server error occurred"

  // Mock repository to throw HttpException
  coEvery { repository.clearBag() } throws exception

  val result = clearBagUseCase()

  assertEquals(Resource.Error(exception), result) // Pass the actual exception
 }

 // ✅ Test Case 3: Handle IOException (network error)
 @Test
 fun `invoke should return error when IOException occurs`() = runTest {
  val exception = IOException("Network error")

  // Mock repository to throw IOException
  coEvery { repository.clearBag() } throws exception

  val result = clearBagUseCase()

  assertEquals(Resource.Error(exception), result)
 }
}
