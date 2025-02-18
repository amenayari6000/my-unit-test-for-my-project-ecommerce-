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
class DeleteFromBagUseCaseTest {

 private val repository: ProductsRepository = mockk()
 private val deleteFromBagUseCase = DeleteFromBagUseCase(repository)

 // ✅ Test Case 1: Successfully delete product from bag
 @Test
 fun `invoke should return success when product is deleted from bag`() = runTest {
  val productId = 1
  val expectedResponse = CRUDResponse(status = 1, message = "Product removed from bag successfully")

  // Mock repository response
  coEvery { repository.deleteFromBag(productId) } returns expectedResponse

  val result = deleteFromBagUseCase(productId)

  assertEquals(Resource.Success(expectedResponse), result)
  coVerify { repository.deleteFromBag(productId) }
 }

 // ✅ Test Case 2: Handle HttpException (server error)
 @Test
 fun `invoke should return error when HttpException occurs`() = runTest {
  val productId = 2
  val exception = mockk<HttpException>(relaxed = true)
  every { exception.message } returns "Server error occurred"

  // Mock repository to throw HttpException
  coEvery { repository.deleteFromBag(productId) } throws exception

  val result = deleteFromBagUseCase(productId)

  assertEquals(Resource.Error(exception), result) // Pass the actual exception
 }

 // ✅ Test Case 3: Handle IOException (network error)
 @Test
 fun `invoke should return error when IOException occurs`() = runTest {
  val productId = 3
  val exception = IOException("Network error")

  // Mock repository to throw IOException
  coEvery { repository.deleteFromBag(productId) } throws exception

  val result = deleteFromBagUseCase(productId)

  assertEquals(Resource.Error(exception), result)
 }
}
