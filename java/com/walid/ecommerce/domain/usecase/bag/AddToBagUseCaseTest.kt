package com.walid.ecommerce.domain.usecase.bag



import com.walid.ecommerce.common.Resource
import com.walid.ecommerce.data.model.CRUDResponse
import com.walid.ecommerce.data.model.Product
import com.walid.ecommerce.domain.repository.Authenticator
import com.walid.ecommerce.domain.repository.ProductsRepository

import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

@ExperimentalCoroutinesApi
class AddToBagUseCaseTest {

 private val repository: ProductsRepository = mockk()
 private val authenticator: Authenticator = mockk()
 private val addToBagUseCase = AddToBagUseCase(repository, authenticator)

 // ✅ Optimized product list (both saleState = 1 and saleState = 0)
 private val expectedProducts = listOf(
  Product(
   id = 1,
   category = "Electronics",
   count = 10,
   description = "Latest model laptop",
   image = "image_url_1",
   imageTwo = "image_url_2",
   imageThree = "image_url_3",
   price = 1200.0,
   rate = 4.5,
   title = "Laptop X200",
   saleState = 1,  // Product is on sale
   isFavorite = true,
   salePrice = 999.0
  ),
  Product(
   id = 2,
   category = "Electronics",
   count = 10,
   description = "Old model laptop",
   image = "image_url_4",
   imageTwo = "image_url_5",
   imageThree = "image_url_6",
   price = 800.0,
   rate = 3.8,
   title = "Laptop Y100",
   saleState = 0,  // Product is NOT on sale
   isFavorite = false,
   salePrice = 800.0
  )
 )

 // ✅ Test case 1: Successfully add a sale product (saleState = 1)
 @Test
 fun `invoke should return success when adding a sale product`() = runTest {
  val saleProduct = expectedProducts[0] // Reusing product from list
  val expectedResponse = CRUDResponse(status = 1, message = "Product added to bag")  // Use status instead of success

  // Mock the getFirebaseUserUid to return a mock user ID
  coEvery { authenticator.getFirebaseUserUid() } returns "mockedUserId"

  // Mock the repository response for adding to bag
  coEvery { repository.addToBag(saleProduct) } returns expectedResponse

  val result = addToBagUseCase(saleProduct)

  assertEquals(Resource.Success(expectedResponse), result)
  coVerify { repository.addToBag(saleProduct) }
 }

 // ✅ Test case 2: Successfully add a regular product (saleState = 0)
 @Test
 fun `invoke should return success when adding a regular product`() = runTest {
  val regularProduct = expectedProducts[1] // Reusing product from list
  val expectedResponse = CRUDResponse(status = 1, message = "Product added to bag")  // Use status instead of success

  // Mock the getFirebaseUserUid to return a mock user ID
  coEvery { authenticator.getFirebaseUserUid() } returns "mockedUserId"

  // Mock the repository response for adding to bag
  coEvery { repository.addToBag(regularProduct) } returns expectedResponse

  val result = addToBagUseCase(regularProduct)

  assertEquals(Resource.Success(expectedResponse), result)
  coVerify { repository.addToBag(regularProduct) }
 }

 // ✅ Test case 3: Handle HttpException (server error)

 @Test
 fun `invoke should return error when HttpException occurs`() = runTest {
  val product = expectedProducts[0] // Use any product from list
  val exception = mockk<HttpException>(relaxed = true) // Relaxed mock to avoid missing setup for unlocked methods

  // Mock getMessage() method directly
  every { exception.message } returns "Server error occurred"

  // Mock the getFirebaseUserUid to return a mock user ID
  coEvery { authenticator.getFirebaseUserUid() } returns "mockedUserId"

  // Mock the repository response for adding to bag to throw an HttpException
  coEvery { repository.addToBag(product) } throws exception

  // Run the use case
  val result = addToBagUseCase(product)

  // Verify that the result matches the expected error response
  assertEquals(Resource.Error(exception), result) // Pass the exception object, not just the message
 }


 // ✅ Test case 4: Handle IOException (network error)
 @Test
 fun `invoke should return error when IOException occurs`() = runTest {
  val product = expectedProducts[1] // Use any product from list
  val exception = IOException("Network error")

  // Mock the getFirebaseUserUid to return a mock user ID
  coEvery { authenticator.getFirebaseUserUid() } returns "mockedUserId"

  coEvery { repository.addToBag(product) } throws exception

  val result = addToBagUseCase(product)

  assertEquals(Resource.Error(exception), result)
 }
}
