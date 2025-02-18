package com.walid.ecommerce.presentation.home.categories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.walid.ecommerce.common.Resource
import com.walid.ecommerce.data.model.Product
import com.walid.ecommerce.domain.usecase.product.GetProductsByCategoryUseCase
import com.walid.ecommerce.domain.usecase.product.GetProductsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CategoryProductsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var getProductsUseCase: GetProductsUseCase

    @Mock
    private lateinit var getProductsByCategoryUseCase: GetProductsByCategoryUseCase

    private lateinit var viewModel: CategoryProductsViewModel
    private val testDispatcher = StandardTestDispatcher()

    // Sample test data
    private val sampleProducts = listOf(
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
            count = 10,
            description = "Old model laptop",
            image = "image_url_4",
            imageTwo = "image_url_5",
            imageThree = "image_url_6",
            price = 800.0,
            rate = 3.8,
            title = "Laptop Y100",
            saleState = 0,
            isFavorite = false,
            salePrice = 800.0
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CategoryProductsViewModel(getProductsUseCase, getProductsByCategoryUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getProducts should update LiveData with Loading then Success state`() = runTest {
        // Given
        val expectedSuccess = Resource.Success(sampleProducts)
        `when`(getProductsUseCase()).thenReturn(expectedSuccess)

        // When
        viewModel.getProducts()

        // Then - First emission should be Loading
        assertEquals(Resource.Loading, viewModel.products.value)

        // Advance coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - Second emission should be Success with products
        assertEquals(expectedSuccess, viewModel.products.value)
        verify(getProductsUseCase).invoke()
    }

    @Test
    fun `getProducts should handle error state`() = runTest {
        // Given
        val errorMessage = "An error occurred"
        val expectedError = Resource.Error(Throwable(errorMessage))
        `when`(getProductsUseCase()).thenReturn(expectedError)

        // When
        viewModel.getProducts()

        // Then - First emission should be Loading
        assertEquals(Resource.Loading, viewModel.products.value)

        // Advance coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - Second emission should be Error
        assertEquals(expectedError, viewModel.products.value)
        verify(getProductsUseCase).invoke()
    }

    @Test
    fun `getProductsByCategory should update LiveData with Loading then Success state`() = runTest {
        // Given
        val category = "Electronics"
        val expectedSuccess = Resource.Success(sampleProducts)
        `when`(getProductsByCategoryUseCase(category)).thenReturn(expectedSuccess)

        // When
        viewModel.getProductsByCategory(category)

        // Then - First emission should be Loading
        assertEquals(Resource.Loading, viewModel.products.value)

        // Advance coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - Second emission should be Success with products
        assertEquals(expectedSuccess, viewModel.products.value)
        verify(getProductsByCategoryUseCase).invoke(category)
    }

   
    @Test
    fun `getProductsByCategory should handle error state`() = runTest {
        // Given
        val category = "Electronics"
        val errorMessage = "An error occurred"
        val expectedError = Resource.Error(Throwable(errorMessage))
        `when`(getProductsByCategoryUseCase(category)).thenReturn(expectedError)  // Fixed this line

        // When
        viewModel.getProductsByCategory(category)

        // Then - First emission should be Loading
        assertEquals(Resource.Loading, viewModel.products.value)

        // Advance coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - Second emission should be Error
        assertEquals(expectedError, viewModel.products.value)
        verify(getProductsByCategoryUseCase).invoke(category)
    }
}