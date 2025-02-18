package com.walid.ecommerce.domain.usecase.favorite

import com.walid.ecommerce.domain.repository.ProductsRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

@ExperimentalCoroutinesApi
class DeleteFromFavoritesUseCaseTest {

 private val repository: ProductsRepository = mockk()
 private val deleteFromFavoritesUseCase = DeleteFromFavoritesUseCase(repository)

 // ✅ Test Case 1: Successfully remove product from favorites
 @Test
 fun `invoke should call repository to delete product from favorites`() = runTest {
  val productId = 1

  // Mock repository call
  coEvery { repository.deleteFromFavorites(productId) } returns Unit

  deleteFromFavoritesUseCase(productId)

  coVerify { repository.deleteFromFavorites(productId) }
 }

 // ✅ Test Case 2: Handle HttpException (server error)
 @Test
 fun `invoke should throw HttpException when server error occurs`() = runTest {
  val productId = 2
  val exception = mockk<HttpException>(relaxed = true)
  every { exception.message } returns "Server error occurred"

  // Mock repository to throw HttpException
  coEvery { repository.deleteFromFavorites(productId) } throws exception

  try {
   deleteFromFavoritesUseCase(productId)
  } catch (e: HttpException) {
   assert(e.message == "Server error occurred")
  }
 }

 // ✅ Test Case 3: Handle IOException (network error)
 @Test
 fun `invoke should throw IOException when network error occurs`() = runTest {
  val productId = 3
  val exception = IOException("Network error")

  // Mock repository to throw IOException
  coEvery { repository.deleteFromFavorites(productId) } throws exception

  try {
   deleteFromFavoritesUseCase(productId)
  } catch (e: IOException) {
   assert(e.message == "Network error")
  }
 }
}
