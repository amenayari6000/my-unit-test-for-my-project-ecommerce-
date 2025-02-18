package com.walid.ecommerce.presentation.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.walid.ecommerce.common.Resource
import com.walid.ecommerce.data.model.CRUDResponse
import com.walid.ecommerce.data.model.Product
import com.walid.ecommerce.domain.repository.Authenticator
import com.walid.ecommerce.domain.usecase.bag.AddToBagUseCase
import com.walid.ecommerce.domain.usecase.favorite.AddToFavoritesUseCase
import com.walid.ecommerce.domain.usecase.favorite.DeleteFromFavoritesUseCase
import com.walid.ecommerce.testing.getOrAwaitValue
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: DetailViewModel
    private val addToBagUseCase: AddToBagUseCase = mockk()
    private val addToFavoritesUseCase: AddToFavoritesUseCase = mockk()
    private val deleteFromFavoritesUseCase: DeleteFromFavoritesUseCase = mockk()
    private val authenticator: Authenticator = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Mock the authenticator to return a fixed user ID
        coEvery { authenticator.getFirebaseUserUid() } returns "userId"

        // Configure the SavedStateHandle mock to return a product
        val product = Product(
            id = 1,
            category = "category",
            count = 10,
            description = "description",
            image = "image",
            imageTwo = "imageTwo",
            imageThree = "imageThree",
            price = 100.0,
            rate = 4.5,
            title = "title",
            saleState = 1,
            isFavorite = false,
            salePrice = null
        )
        coEvery { savedStateHandle.get<Product>("product") } returns product

        // Initialize the ViewModel with mocked dependencies
        viewModel = DetailViewModel(
            addToBagUseCase,
            addToFavoritesUseCase,
            deleteFromFavoritesUseCase,
            authenticator,
            savedStateHandle
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addToBag should update crudResponse with Loading and then Success`() = runTest {
        // Arrange
        val product = Product(
            id = 1,
            category = "category",
            count = 10,
            description = "description",
            image = "image",
            imageTwo = "imageTwo",
            imageThree = "imageThree",
            price = 100.0,
            rate = 4.5,
            title = "title",
            saleState = 1,
            isFavorite = false,
            salePrice = null
        )

        // Mock the addToBagUseCase to return a success response
        coEvery { addToBagUseCase(product) } returns Resource.Success(CRUDResponse(200, "Success"))

        // Act
        viewModel.addToBag()

        // Assert
        val crudResponse = viewModel.crudResponse.getOrAwaitValue()
        assertEquals(Resource.Success(CRUDResponse(200, "Success")), crudResponse)

        // Verify that the use case was called with the correct product
        coVerify { addToBagUseCase(product) }
    }

    @Test
    fun `setFavoriteState should add to favorites if not favorite`() = runTest {
        // Arrange
        val product = Product(
            id = 1,
            category = "category",
            count = 10,
            description = "description",
            image = "image",
            imageTwo = "imageTwo",
            imageThree = "imageThree",
            price = 100.0,
            rate = 4.5,
            title = "title",
            saleState = 1,
            isFavorite = false,
            salePrice = null
        )

        // Mock the addToFavoritesUseCase to do nothing (Unit)
        coEvery { addToFavoritesUseCase(product) } returns Unit

        // Act
        viewModel.setFavoriteState()

        // Assert
        assertEquals(true, viewModel.isFavorite.getOrAwaitValue())

        // Verify that the use case was called with the correct product
        coVerify { addToFavoritesUseCase(product) }
    }

    @Test
    fun `setFavoriteState should delete from favorites if favorite`() = runTest {
        // Arrange
        val product = Product(
            id = 1,
            category = "category",
            count = 10,
            description = "description",
            image = "image",
            imageTwo = "imageTwo",
            imageThree = "imageThree",
            price = 100.0,
            rate = 4.5,
            title = "title",
            saleState = 1,
            isFavorite = true, // Explicitly set isFavorite to true
            salePrice = null
        )
// Mock SavedStateHandle to return the product with isFavorite = true
        coEvery { savedStateHandle.get<Product>("product") } returns product

        // Create a new instance of ViewModel with the updated SavedStateHandle
        viewModel = DetailViewModel(
            addToBagUseCase,
            addToFavoritesUseCase,
            deleteFromFavoritesUseCase,
            authenticator,
            savedStateHandle
        )


        // Mock the deleteFromFavoritesUseCase
        coEvery { deleteFromFavoritesUseCase(product.id) } returns Unit


        // Act
        viewModel.setFavoriteState()

        // Assert
        // Verify that the isFavorite state is updated to false
        assertEquals(false, viewModel.isFavorite.getOrAwaitValue())

        // Verify that the use case was called with the correct product ID
        coVerify { deleteFromFavoritesUseCase(product.id) }


        /* explanition why  i create  a new instance of viewmodel:
        Looking at your test and ViewModel code, I can see why the test is failing.
         The issue is in how the test is set up versus how the ViewModel actually works.
          Let me explain the problem and provide a solution.
         The test fails because:

         In your test, you're creating a new Product with isFavorite = true
         However, this product is not actually being used by the ViewModel because you've commented
         out the line that would mock the SavedStateHandle
         The ViewModel is still using the product from the setUp()
                   method, which has isFavorite = false
        When setFavoriteState() is called with isFavorite = false, it adds to favorites and sets isFavorite to true
        That's why your assertion fails - you expected false but got true

           Here's how to fix the test:look test
         */

    }

    @Test
    fun `getProduct should update product LiveData`() = runTest {
        // Arrange
        val product = Product(
            id = 1,
            category = "category",
            count = 10,
            description = "description",
            image = "image",
            imageTwo = "imageTwo",
            imageThree = "imageThree",
            price = 100.0,
            rate = 4.5,
            title = "title",
            saleState = 1,
            isFavorite = false,
            salePrice = null
        )

        // Act
        viewModel.getProduct()

        // Assert
        assertEquals(product, viewModel.product.getOrAwaitValue())
    }
}