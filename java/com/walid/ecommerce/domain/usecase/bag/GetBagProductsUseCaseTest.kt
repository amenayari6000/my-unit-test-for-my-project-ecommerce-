package com.walid.ecommerce.domain.usecase.bag

import com.walid.ecommerce.common.Resource
import com.walid.ecommerce.data.model.Product
import com.walid.ecommerce.domain.repository.ProductsRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

@ExperimentalCoroutinesApi
class GetBagProductsUseCaseTest {

 private val repository: ProductsRepository = mockk()
 private val getBagProductsUseCase = GetBagProductsUseCase(repository)

 // ✅ Test Case 1: Successfully get the list of products in the bag
 @Test
 fun `invoke should return success with product list`() = runTest {
  val expectedProducts = listOf(
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
    saleState = 1,
    isFavorite = true,
    salePrice = 999.0
   ),
   Product(
    id = 2,
    category = "Electronics",
    count = 5,
    description = "Smartphone with high-end camera",
    image = "image_url_4",
    imageTwo = "image_url_5",
    imageThree = "image_url_6",
    price = 900.0,
    rate = 4.7,
    title = "Phone Z500",
    saleState = 0,
    isFavorite = false,
    salePrice = 900.0
   )
  )

  // Mock repository response
  coEvery { repository.getBagProducts() } returns expectedProducts

  val result = getBagProductsUseCase()

  assertEquals(Resource.Success(expectedProducts), result)
  coVerify { repository.getBagProducts() }
 }

 // ✅ Test Case 2: Handle HttpException (server error)
 @Test
 fun `invoke should return error when HttpException occurs`() = runTest {
  val exception = mockk<HttpException>(relaxed = true)
  every { exception.message } returns "Server error occurred"

  // Mock repository to throw HttpException
  coEvery { repository.getBagProducts() } throws exception

  val result = getBagProductsUseCase()

  assertEquals(Resource.Error(exception), result) // Pass the actual exception
 }

 // ✅ Test Case 3: Handle IOException (network error)
 @Test
 fun `invoke should return error when IOException occurs`() = runTest {
  val exception = IOException("Network error")

  // Mock repository to throw IOException
  coEvery { repository.getBagProducts() } throws exception

  val result = getBagProductsUseCase()

  assertEquals(Resource.Error(exception), result)
 }
}
