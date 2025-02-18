package com.walid.ecommerce.domain.usecase.bag

import com.walid.ecommerce.common.Resource
import com.walid.ecommerce.domain.repository.ProductsRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

@ExperimentalCoroutinesApi
class GetBagProductsCountUseCaseTest {

 private val repository: ProductsRepository = mockk()
 private val getBagProductsCountUseCase = GetBagProductsCountUseCase(repository)

 // ✅ Test Case 1: Successfully get the count of products in the bag
 @Test
 fun `invoke should return success with product count`() = runTest {
  val expectedCount = 5 // Example count

  // Mock repository response
  coEvery { repository.getBagProductsCount() } returns expectedCount

  val result = getBagProductsCountUseCase()

  assertEquals(Resource.Success(expectedCount), result)
  coVerify { repository.getBagProductsCount() }
 }

 // ✅ Test Case 2: Handle HttpException (server error)
 @Test
 fun `invoke should return error when HttpException occurs`() = runTest {
  val exception = mockk<HttpException>(relaxed = true)
  every { exception.message } returns "Server error occurred"

  // Mock repository to throw HttpException
  coEvery { repository.getBagProductsCount() } throws exception

  val result = getBagProductsCountUseCase()

  assertEquals(Resource.Error(exception), result) // Pass the actual exception
 }

 // ✅ Test Case 3: Handle IOException (network error)
 @Test
 fun `invoke should return error when IOException occurs`() = runTest {
  val exception = IOException("Network error")

  // Mock repository to throw IOException
  coEvery { repository.getBagProductsCount() } throws exception

  val result = getBagProductsCountUseCase()

  assertEquals(Resource.Error(exception), result)
 }
}
