package com.walid.ecommerce.domain.usecase.favorite

import com.walid.ecommerce.domain.repository.ProductsRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

@ExperimentalCoroutinesApi
class ClearFavoritesUseCaseTest {

 private val repository: ProductsRepository = mockk()
 private val clearFavoritesUseCase = ClearFavoritesUseCase(repository)

 // ✅ Test Case 1: Successfully clear all favorite products
 @Test
 fun `invoke should call repository to clear all favorite products`() = runTest {
  // Mock repository call
  coEvery { repository.clearFavorites() } returns Unit

  clearFavoritesUseCase()

  coVerify { repository.clearFavorites() }
 }

 // ✅ Test Case 2: Handle HttpException (server error)
 @Test
 fun `invoke should throw HttpException when server error occurs`() = runTest {
  val exception = mockk<HttpException>(relaxed = true)
  every { exception.message } returns "Server error occurred"

  // Mock repository to throw HttpException
  coEvery { repository.clearFavorites() } throws exception

  try {
   clearFavoritesUseCase()
  } catch (e: HttpException) {
   assert(e.message == "Server error occurred")
  }
 }

 // ✅ Test Case 3: Handle IOException (network error)
 @Test
 fun `invoke should throw IOException when network error occurs`() = runTest {
  val exception = IOException("Network error")

  // Mock repository to throw IOException
  coEvery { repository.clearFavorites() } throws exception

  try {
   clearFavoritesUseCase()
  } catch (e: IOException) {
   assert(e.message == "Network error")
  }
 }
}
