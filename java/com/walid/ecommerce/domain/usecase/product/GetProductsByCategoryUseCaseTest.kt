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
class GetProductsByCategoryUseCaseTest {

    private lateinit var getProductsByCategoryUseCase: GetProductsByCategoryUseCase
    private val repository: ProductsRepository = mockk()

    @Before
    fun setUp() {
        getProductsByCategoryUseCase = GetProductsByCategoryUseCase(repository)
    }

    // ✅ Test case 1: Should return products of the specified category when successful
    @Test
    fun `invoke should return products of the specified category when successful`() = runTest {
        val category = "Desktop" // Example category
        val expectedProducts = listOf(
            Product(
                id = 1,
                category = category,
                count = 10,
                description = "High-performance desktop",
                image = "image_url_1",
                imageTwo = "image_url_2",
                imageThree = "image_url_3",
                price = 1500.0,
                rate = 4.8,
                title = "Desktop X500",
                saleState = 1,
                isFavorite = true,
                salePrice = 1299.0
            )
        )

        // Mock the repository to return a list of products for the category "Desktop"
        coEvery { repository.getProductsByCategory(category) } returns expectedProducts

        // When: The use case is invoked
        val result = getProductsByCategoryUseCase(category)

        // Then: It should return a Resource.Success with the expected products
        assertEquals(Resource.Success(expectedProducts), result)

        // Verify that getProductsByCategory() was called with the correct category
        coVerify { repository.getProductsByCategory(category) }
    }

    // ✅ Test case 2: Should return error when HttpException occurs
    @Test
    fun `invoke should return error when HttpException occurs`() = runTest {
        val category = "Desktop"
        val exception = mockk<HttpException>()

        // Mock repository behavior: throw HttpException
        coEvery { repository.getProductsByCategory(category) } throws exception

        // When: The use case is invoked
        val result = getProductsByCategoryUseCase(category)

        // Then: It should return a Resource.Error with the exception
        assertEquals(Resource.Error(exception), result)
    }

    // ✅ Test case 3: Should return error when IOException occurs
    @Test
    fun `invoke should return error when IOException occurs`() = runTest {
        val category = "Desktop"
        val exception = IOException("Network error")

        // Mock repository behavior: throw IOException
        coEvery { repository.getProductsByCategory(category) } throws exception

        // When: The use case is invoked
        val result = getProductsByCategoryUseCase(category)

        // Then: It should return a Resource.Error with the exception
        assertTrue(result is Resource.Error)
        assertEquals(exception, (result as Resource.Error).throwable)
    }

    // ✅ Test case 4: Should return empty list if category doesn't exist
    @Test
    fun `invoke should return empty list when category does not exist`() = runTest {
        val category = "NonExistentCategory"
        val expectedProducts = emptyList<Product>()

        // Mock repository to return an empty list when category doesn't exist
        coEvery { repository.getProductsByCategory(category) } returns expectedProducts

        // When: The use case is invoked
        val result = getProductsByCategoryUseCase(category)

        // Then: It should return a Resource.Success with an empty list
        assertEquals(Resource.Success(expectedProducts), result)

        // Verify that getProductsByCategory() was called with the correct category
        coVerify { repository.getProductsByCategory(category) }
    }
}
