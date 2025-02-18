package com.walid.ecommerce.presentation.bag

import androidx.arch.core.executor.testing.InstantTaskExecutorRule

import com.walid.ecommerce.common.Resource
import com.walid.ecommerce.data.model.CRUDResponse
import com.walid.ecommerce.data.model.Product
import com.walid.ecommerce.domain.usecase.bag.DeleteFromBagUseCase
import com.walid.ecommerce.domain.usecase.bag.GetBagProductsUseCase
import com.walid.ecommerce.testing.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Rule
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
@OptIn(ExperimentalCoroutinesApi::class)
class BagViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    lateinit var getBagProductsUseCase: GetBagProductsUseCase

    @Mock
    lateinit var deleteFromBagUseCase: DeleteFromBagUseCase


    private lateinit var bagViewModel: BagViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher) // Set up coroutine dispatcher
        MockitoAnnotations.openMocks(this)
        bagViewModel = BagViewModel(getBagProductsUseCase, deleteFromBagUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset dispatcher after test
    }

    @Test
    fun `test getBagProducts returns data successfully`() = runTest {
        // Mock response for products
        val mockProducts = listOf(
            Product(1, "Electronics", 10, "Laptop", "image_url_1", "image_url_2", "image_url_3", 1200.0, 4.5, "Laptop X200", 1, true, 999.0),
            Product(2, "Electronics", 10, "Laptop", "image_url_4", "image_url_5", "image_url_6", 800.0, 3.8, "Laptop Y100", 0, false, 800.0)
        )
        val mockedResponse = Resource.Success(mockProducts)

        // Simulate the response
        `when`(getBagProductsUseCase()).thenReturn(mockedResponse)

        // Trigger the ViewModel to load the products
        bagViewModel.getBagProducts()

        // Wait for coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert that LiveData contains the correct data
        val result = bagViewModel.bagProducts.getOrAwaitValue()
        assertEquals(mockedResponse, result)
    }

    @Test
    fun `test deleteFromBag calls use case and updates CRUD response`() = runTest {
        val mockedResponse = Resource.Success(CRUDResponse(200, "Product deleted successfully"))

        // Simulate the delete use case
        `when`(deleteFromBagUseCase.invoke(1)).thenReturn(mockedResponse)

        // Trigger the delete action
        bagViewModel.deleteFromBag(1)

        // Wait for coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert that the LiveData is updated with the correct CRUD response
        val result = bagViewModel.crudResponse.getOrAwaitValue()
        if (result is Resource.Success) {
            assertEquals("Product deleted successfully", result.data.message)
        } else {
            throw AssertionError("Expected Resource.Success but got $result")
        }
    }

    @Test
    fun `test increase updates totalAmount`() = runTest {
        val priceToAdd = 10.0

        // Increase total amount
        bagViewModel.increase(priceToAdd)

        // Wait for coroutines to complete
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert that the totalAmount LiveData is updated correctly
        val result = bagViewModel.totalAmount.getOrAwaitValue()
        assertEquals(priceToAdd, result)
    }

    @Test
    fun `test decrease updates totalAmount`() = runTest {
        val priceToSubtract = 5.0

        // First, increase the total amount to ensure non-negative results
        bagViewModel.increase(10.0)

        // Now, decrease the amount
        bagViewModel.decrease(priceToSubtract)

        // Wait for coroutines to complete
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert the totalAmount LiveData reflects the correct decreased value
        val result = bagViewModel.totalAmount.getOrAwaitValue()
        assertEquals(5.0, result) // The result should be 10.0 - 5.0 = 5.0
    }

    @Test
    fun `test resetTotalAmount resets value to zero`() = runTest {
        // Reset totalAmount to 0
        bagViewModel.resetTotalAmount()

        // Wait for coroutines to complete
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert that totalAmount is reset
        val result = bagViewModel.totalAmount.getOrAwaitValue()
        assertEquals(0.0, result)
    }

    @Test
    fun `test deleteFromBag handles error response`() = runTest {
        val errorResponse = Resource.Error(Throwable("Network error"))

        // Simulate the error in delete operation
        `when`(deleteFromBagUseCase.invoke(1)).thenReturn(errorResponse)

        // Trigger the delete operation
        bagViewModel.deleteFromBag(1)

        // Wait for coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert that the LiveData contains the error response
        val result = bagViewModel.crudResponse.getOrAwaitValue()

        // Check if the result is an error
        assert(result is Resource.Error) { "Expected Resource.Error but got $result" }

        if (result is Resource.Error) {
            assertEquals("Network error", result.throwable.message)
        }
    }//
    /*The reason you're using getOrAwaitValue() in the first
    BagViewModelTest class is that you're working with LiveData,
     and this method is a helper function to synchronously fetch the current value of LiveData during the test.
LiveData in Android is typically observed asynchronously,
meaning the value may not be immediately available.
To ensure that your tests can synchronously access the value emitted by LiveData, you use getOrAwaitValue(). This method blocks the test thread until the LiveData has been updated, ensuring you get the final value to assert in your test.
In the second CategoryProductsViewModelTest,
 you don't use getOrAwaitValue() because your test
 logic is based on observing LiveData directly through
  the assertEquals() checks, with the assumption that
  your LiveData's state is being updated correctly as a
  result of the getProducts() or getProductsByCategory() methods.
Why Use getOrAwaitValue()?
LiveData Observation: LiveData is designed to notify
 its observers asynchronously. However, in unit tests,
  you need to block execution until the LiveData is updated.
Ensures the value is ready: The getOrAwaitValue() method waits for LiveData to emit a value and returns it when it's available.
If you want to unify this approach across both test classes, you could apply getOrAwaitValue() to observe LiveData for both tests, ensuring you retrieve the values correctly.
In summary:

First test (BagViewModelTest): getOrAwaitValue() is used because you are testing the LiveData's value synchronously.
Second test (CategoryProductsViewModelTest): You're directly asserting the value after waiting for the coroutine to finish. You could also introduce getOrAwaitValue() to ensure uniformity.


*/





}
