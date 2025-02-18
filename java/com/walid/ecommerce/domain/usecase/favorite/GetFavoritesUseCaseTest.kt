package com.walid.ecommerce.domain.usecase.favorite

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
class GetFavoritesUseCaseTest {

 private val repository: ProductsRepository = mockk()
 private val getFavoritesUseCase = GetFavoritesUseCase(repository)

 // ✅ Test Case 1: Successfully retrieve favorite products
 @Test
 fun `invoke should return success with favorite products`() = runTest {
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

  coEvery { repository.getFavorites() } returns expectedProducts

  val result = getFavoritesUseCase()

  assertEquals(Resource.Success(expectedProducts), result)
  coVerify { repository.getFavorites() }
 }

 // ✅ Test Case 2: Handle HttpException (server error)
 @Test
 fun `invoke should return error when HttpException occurs`() = runTest {
  val exception = mockk<HttpException>(relaxed = true)
  every { exception.message } returns "Server error occurred"

  coEvery { repository.getFavorites() } throws exception

  val result = getFavoritesUseCase()

  assertEquals(Resource.Error(exception), result)
 }

 // ✅ Test Case 3: Handle IOException (network error)
 @Test
 fun `invoke should return error when IOException occurs`() = runTest {
  val exception = IOException("Network error")

  coEvery { repository.getFavorites() } throws exception

  val result = getFavoritesUseCase()

  assertEquals(Resource.Error(exception), result)
 }
}
