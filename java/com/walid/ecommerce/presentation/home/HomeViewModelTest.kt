package com.walid.ecommerce.presentation.home
import androidx.arch.core.executor.testing.InstantTaskExecutorRule

import com.walid.ecommerce.common.Resource
import com.walid.ecommerce.data.model.Product
import com.walid.ecommerce.data.model.User
import com.walid.ecommerce.domain.usecase.bag.GetBagProductsCountUseCase
import com.walid.ecommerce.domain.usecase.category.GetCategoriesUseCase
import com.walid.ecommerce.domain.usecase.favorite.AddToFavoritesUseCase
import com.walid.ecommerce.domain.usecase.favorite.DeleteFromFavoritesUseCase
import com.walid.ecommerce.domain.usecase.login.GetCurrentUserUseCase
import com.walid.ecommerce.domain.usecase.product.GetSaleProductsUseCase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    // Rule to ensure LiveData operates synchronously in tests
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Mocked use case dependencies
    @Mock
    private lateinit var getSaleProductsUseCase: GetSaleProductsUseCase

    @Mock
    private lateinit var getCategoriesUseCase: GetCategoriesUseCase

    @Mock
    private lateinit var getBagProductsCountUseCase: GetBagProductsCountUseCase

    @Mock
    private lateinit var addToFavoritesUseCase: AddToFavoritesUseCase

    @Mock
    private lateinit var deleteFromFavoritesUseCase: DeleteFromFavoritesUseCase

    @Mock
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    // Instance of the ViewModel to be tested
    private lateinit var homeViewModel: HomeViewModel

    // Test dispatcher for controlling coroutine execution
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        // Set the main dispatcher to the test dispatcher
        Dispatchers.setMain(testDispatcher)

        // Initialize the ViewModel with mocked dependencies
        homeViewModel = HomeViewModel(
            getSaleProductsUseCase,
            getCategoriesUseCase,
            getBagProductsCountUseCase,
            addToFavoritesUseCase,
            deleteFromFavoritesUseCase,
            getCurrentUserUseCase
        )
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher to the original Main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load user, sale products, bag products count, and categories`() = runTest {
        // Setup mocks
        val mockUser = Resource.Success(User("userTest@gmail.com", "userName", "55225541"))
        val mockProducts = Resource.Success(listOf(
            Product(
                1, "Category1", 10, "Description", "Image1", "Image2", "Image3",
                100.0, 4.5, "Product1", 1, false, null
            )
        ))
        val mockBagCount = Resource.Success(5)
        val mockCategories = Resource.Success(listOf("Category1", "Category2"))

        `when`(getCurrentUserUseCase()).thenReturn(mockUser)
        `when`(getSaleProductsUseCase()).thenReturn(mockProducts)
        `when`(getBagProductsCountUseCase()).thenReturn(mockBagCount)
        `when`(getCategoriesUseCase()).thenReturn(mockCategories)

        // Initialize ViewModel after setting up mocks
        homeViewModel = HomeViewModel(
            getSaleProductsUseCase,
            getCategoriesUseCase,
            getBagProductsCountUseCase,
            addToFavoritesUseCase,
            deleteFromFavoritesUseCase,
            getCurrentUserUseCase
        )

        // Advance dispatcher before setting observers
        testDispatcher.scheduler.advanceUntilIdle()

        // Then verify the values
        Assert.assertEquals(mockUser, homeViewModel.user.value)
        Assert.assertEquals(mockProducts, homeViewModel.saleProducts.value)
        Assert.assertEquals(mockBagCount, homeViewModel.bagProductsCount.value)
        Assert.assertEquals(mockCategories, homeViewModel.categories.value)
    }
    @Test
    fun `addToFavorite should invoke addToFavoritesUseCase`() = runTest {
        // Given: A sample product to add to favorites
        val product = Product(
            1, "Category1", 10, "Description", "Image1", "Image2", "Image3",
            100.0, 4.5, "Product1", 1, false, null
        )

        // When: The addToFavorite function is called
        homeViewModel.addToFavorite(product)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Verify that the addToFavoritesUseCase was invoked with the correct product
        verify(addToFavoritesUseCase).invoke(product)
    }

    @Test
    fun `deleteFromFavorites should invoke deleteFromFavoritesUseCase`() = runTest {
        // Given: A sample product ID to delete from favorites
        val productId = 1

        // When: The deleteFromFavorites function is called
        homeViewModel.deleteFromFavorites(productId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Verify that the deleteFromFavoritesUseCase was invoked with the correct product ID
        verify(deleteFromFavoritesUseCase).invoke(productId)
    }
}
