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
class SearchProductUseCaseTest {

 private lateinit var searchProductUseCase: SearchProductUseCase
 private val repository: ProductsRepository = mockk()

 @Before
 fun setUp() {
  // Initialize the use case with the mocked repository before running each test
  searchProductUseCase = SearchProductUseCase(repository)
 }

 // ✅ Test case 1: Should return products when search is successful
 @Test
 fun `invoke should return product list when search is successful`() = runTest {
  val searchWord = "Laptop" // Example search word
  val expectedProducts = listOf(
   Product(
    id = 1,
    category = "Desktop",
    count = 10,
    description = "High-performance laptop",
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

  // Mock the repository to return products that match the search keyword
  coEvery { repository.searchProduct(searchWord) } returns expectedProducts

  // When: The use case is invoked
  val result = searchProductUseCase(searchWord)

  // Then: It should return a Resource.Success with the expected products
  assertEquals(Resource.Success(expectedProducts), result)

  // Verify that searchProduct() was called with the correct search keyword
  coVerify { repository.searchProduct(searchWord) }
 }

 // ✅ Test case 2: Should return error when HttpException occurs
 @Test
 fun `invoke should return error when HttpException occurs`() = runTest {
  val searchWord = "Laptop"
  val exception = mockk<HttpException>()

  // Mock repository behavior: throw HttpException
  coEvery { repository.searchProduct(searchWord) } throws exception

  // When: The use case is invoked
  val result = searchProductUseCase(searchWord)

  // Then: It should return a Resource.Error with the exception
  assertEquals(Resource.Error(exception), result)
 }

 // ✅ Test case 3: Should return error when IOException occurs
 @Test
 fun `invoke should return error when IOException occurs`() = runTest {
  val searchWord = "Laptop"
  val exception = IOException("Network error")

  // Mock repository behavior: throw IOException
  coEvery { repository.searchProduct(searchWord) } throws exception

  // When: The use case is invoked
  val result = searchProductUseCase(searchWord)

  // Then: It should return a Resource.Error with the exception
  assertTrue(result is Resource.Error)
  assertEquals(exception, (result as Resource.Error).throwable)
 }

 // ✅ Test case 4: Should return empty list if no products match the search word
 @Test
 fun `invoke should return empty list when no products match the search word`() = runTest {
  val searchWord = "NonExistentProduct"
  val expectedProducts = emptyList<Product>()

  // Mock repository to return an empty list for a non-existent product search
  coEvery { repository.searchProduct(searchWord) } returns expectedProducts

  // When: The use case is invoked
  val result = searchProductUseCase(searchWord)

  // Then: It should return a Resource.Success with an empty list
  assertEquals(Resource.Success(expectedProducts), result)

  // Verify that searchProduct() was called with the correct search keyword
  coVerify { repository.searchProduct(searchWord) }
 }
}

