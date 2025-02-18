package com.walid.ecommerce.domain.usecase.category


import com.walid.ecommerce.common.Resource
import com.walid.ecommerce.domain.repository.ProductsRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

@ExperimentalCoroutinesApi
class GetCategoriesUseCaseTest {

 private val repository: ProductsRepository = mockk()
 private val getCategoriesUseCase = GetCategoriesUseCase(repository)

 // ✅ Test Case 1: Successfully get the list of categories
 @Test
 fun `invoke should return success with category list`() = runTest {
  val expectedCategories = listOf("Electronics", "Clothing", "Home Appliances")

  // Mock repository response
  coEvery { repository.getCategories() } returns expectedCategories

  val result = getCategoriesUseCase()

  assertEquals(Resource.Success(expectedCategories), result)
  coVerify { repository.getCategories() }
 }

 // ✅ Test Case 2: Handle HttpException (server error)
 @Test
 fun `invoke should return error when HttpException occurs`() = runTest {
  val exception = mockk<HttpException>(relaxed = true)
  every { exception.message } returns "Server error occurred"

  // Mock repository to throw HttpException
  coEvery { repository.getCategories() } throws exception

  val result = getCategoriesUseCase()

  assertEquals(Resource.Error(exception), result) // Pass the actual exception
 }

 // ✅ Test Case 3: Handle IOException (network error)
 @Test
 fun `invoke should return error when IOException occurs`() = runTest {
  val exception = IOException("Network error")

  // Mock repository to throw IOException
  coEvery { repository.getCategories() } throws exception

  val result = getCategoriesUseCase()

  assertEquals(Resource.Error(exception), result)
 }
}
