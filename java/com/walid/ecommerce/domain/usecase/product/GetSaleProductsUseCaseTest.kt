package com.walid.ecommerce.domain.usecase.product


import com.walid.ecommerce.common.Resource
import com.walid.ecommerce.data.model.Product
import com.walid.ecommerce.domain.repository.ProductsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

@ExperimentalCoroutinesApi
class GetSaleProductsUseCaseTest {

 // The use case to be tested
 private lateinit var getSaleProductsUseCase: GetSaleProductsUseCase

 // Mocked repository to simulate data fetching without making actual API calls
 private val repository: ProductsRepository = mockk()

 @Before
 fun setUp() {
  // Initialize the use case with the mocked repository before running each test
  getSaleProductsUseCase = GetSaleProductsUseCase(repository)
 }

 // ✅ Test case 1: Ensure that the use case correctly returns a list of sale products when successful
 @Test
 fun `invoke should return sale product list when successful`() = runTest {
  // Given: Define a sample sale product list with all fields populated
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
   )

  )

  // Mock repository behavior: When getSaleProducts() is called, return the expectedProducts list
  coEvery { repository.getSaleProducts() } returns expectedProducts.filter { it.saleState == 1 }

  // When: The use case is invoked
  val result = getSaleProductsUseCase()

  // Then: Ensure the result is a success with the expected sale product list
  // Verify that getSaleProducts() was actually called in the repository
  // Then: It should return only products with saleState = 1
  assertEquals(Resource.Success(expectedProducts), result)
  coVerify { repository.getSaleProducts() }
 }

 // ✅ Test case 2: Ensure that the use case returns an error when an HTTP exception occurs (e.g., API failure)
 @Test
 fun `invoke should return error when HttpException occurs`() = runTest {
  // Given: Simulate an HTTP exception (e.g., server error)
  val exception = mockk<HttpException>()
  coEvery { repository.getSaleProducts() } throws exception

  // When: The use case is invoked
  val result = getSaleProductsUseCase()

  // Then: Ensure the result is an error containing the exception
  assertEquals(Resource.Error(exception), result)
 }

 // ✅ Test case 3: Ensure that the use case returns an error when an IOException occurs (e.g., network failure)
 @Test
 fun `invoke should return error when IOException occurs`() = runTest {
  // Given: Simulate a network failure by throwing an IOException
  val exception = IOException("Network error")
  coEvery { repository.getSaleProducts() } throws exception

  // When: The use case is invoked
  val result = getSaleProductsUseCase()

  // Then: Ensure the result is an error and contains the correct exception
  assertTrue(result is Resource.Error)
  assertEquals(exception, (result as Resource.Error).throwable)
 }
}
