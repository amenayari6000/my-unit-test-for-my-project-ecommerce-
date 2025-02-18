package com.walid.ecommerce.domain.usecase.favorite

import com.walid.ecommerce.data.model.Product
import com.walid.ecommerce.domain.repository.ProductsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

@ExperimentalCoroutinesApi
class AddToFavoritesUseCaseTest {

 private val repository: ProductsRepository = mockk()
 private val addToFavoritesUseCase = AddToFavoritesUseCase(repository)

 // ✅ Test Case 1: Successfully add product to favorites
 @Test
 fun `invoke should call repository to add product to favorites`() = runTest {
  val product = Product(
   id = 1,
   category = "Electronics",
   count = 10,
   description = "Smartphone",
   image = "image_url",
   imageTwo = "image_url_2",
   imageThree = "image_url_3",
   price = 999.99,
   rate = 4.5,
   title = "Smartphone X",
   saleState = 1,
   isFavorite = true,
   salePrice = 899.99
  )

  // Mock repository call
  coEvery { repository.addToFavorites(product) } returns Unit

  addToFavoritesUseCase(product)

  coVerify { repository.addToFavorites(product) }
 }

 // ✅ Test Case 2: Handle HttpException (server error)
 @Test
 fun `invoke should throw HttpException when server error occurs`() = runTest {
  val product = Product(
   id = 2,
   category = "Clothing",
   count = 5,
   description = "T-shirt",
   image = "image_url",
   imageTwo = "image_url_2",
   imageThree = "image_url_3",
   price = 19.99,
   rate = 4.2,
   title = "Cool T-Shirt",
   saleState = 0,
   isFavorite = true,
   salePrice = 19.99
  )

  val exception = mockk<HttpException>(relaxed = true)
  every { exception.message } returns "Server error occurred"

  coEvery { repository.addToFavorites(product) } throws exception

  try {
   addToFavoritesUseCase(product)
  } catch (e: HttpException) {
   assert(e.message == "Server error occurred")
  }
 }

 // ✅ Test Case 3: Handle IOException (network error)
 @Test
 fun `invoke should throw IOException when network error occurs`() = runTest {
  val product = Product(
   id = 3,
   category = "Home Appliances",
   count = 2,
   description = "Vacuum Cleaner",
   image = "image_url",
   imageTwo = "image_url_2",
   imageThree = "image_url_3",
   price = 149.99,
   rate = 4.0,
   title = "Vacuum Cleaner 3000",
   saleState = 1,
   isFavorite = false,
   salePrice = 129.99
  )

  val exception = IOException("Network error")

  coEvery { repository.addToFavorites(product) } throws exception

  try {
   addToFavoritesUseCase(product)
  } catch (e: IOException) {
   assert(e.message == "Network error")
  }
 }
}
