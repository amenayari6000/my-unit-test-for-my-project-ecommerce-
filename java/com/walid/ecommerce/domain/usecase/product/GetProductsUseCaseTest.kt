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
class GetProductsUseCaseTest {

 private lateinit var getProductsUseCase: GetProductsUseCase
 private val repository: ProductsRepository = mockk()

 @Before
 fun setUp() {
  getProductsUseCase = GetProductsUseCase(repository)
 }

 // ✅ Test case 1: Check if use case returns a successful product list with all fields
 @Test
 fun `invoke should return product list when successful`() = runTest {
  // Given: Mock repository returns a predefined product list with all fields
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
   ,
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
    saleState = 0,  // saleState = 0 (not on sale)
    isFavorite = false,
    salePrice = 800.0
   )
  )


  coEvery { repository.getProducts() } returns expectedProducts

  // When: The use case is called
  val result = getProductsUseCase()

  // Then: It should return all products, including those with saleState = 0 and saleState = 1
  assertEquals(Resource.Success(expectedProducts), result)

  // Verify that getProducts() was actually called in the repository
  coVerify { repository.getProducts() }
 }

 //  Test case 2: Simulate an HTTP error (e.g., 500 Server Error)
 @Test
 fun `invoke should return error when HttpException occurs`() = runTest {
  // Given: Mock repository throws an HttpException
  val exception = mockk<HttpException>()
  coEvery { repository.getProducts() } throws exception

  // When: The use case is called
  val result = getProductsUseCase()

  // Then: The result should be Resource.Error containing the exception
  assertEquals(Resource.Error(exception), result)

 }

 //  Test case 3: Simulate a network failure (IOException, e.g., no internet)
 @Test
 fun `invoke should return error when IOException occurs`() = runTest {
  // Given: Mock repository throws an IOException (network issue)
  val exception = IOException("Network error")
  coEvery { repository.getProducts() } throws exception

  // When: The use case is called
  val result = getProductsUseCase()

  // Then: The result should be Resource.Error containing the exception
  assertTrue(result is Resource.Error)
  assertEquals(exception, (result as Resource.Error).throwable)



 }
}
