package com.walid.ecommerce.presentation.search




import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.walid.ecommerce.common.Resource
import com.walid.ecommerce.data.model.Product
import com.walid.ecommerce.domain.usecase.product.SearchProductUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

 @get:Rule
 val instantTaskExecutorRule = InstantTaskExecutorRule()

 private val testDispatcher = StandardTestDispatcher()

 @Mock
 private lateinit var searchProductUseCase: SearchProductUseCase

 private lateinit var viewModel: SearchViewModel

 // Sample test products
 private val testProducts = listOf(
  Product(
   id = 1,
   category = "Electronics",
   count = 10,
   description = "Test description 1",
   image = "image1.jpg",
   imageTwo = "image1_2.jpg",
   imageThree = "image1_3.jpg",
   price = 100.0,
   rate = 4.5,
   title = "Test Product 1",
   saleState = 1,
   isFavorite = true,
   salePrice = 85.0
  ),
  Product(
   id = 2,
   category = "Clothing",
   count = 20,
   description = "Test description 2",
   image = "image2.jpg",
   imageTwo = "image2_2.jpg",
   imageThree = "image2_3.jpg",
   price = 50.0,
   rate = 4.0,
   title = "Test Product 2",
   saleState = 0,
   isFavorite = false,
   salePrice = null
  )
 )

 @Before
 fun setup() {
  MockitoAnnotations.openMocks(this)
  Dispatchers.setMain(testDispatcher)
  viewModel = SearchViewModel(searchProductUseCase)
 }

 @After
 fun tearDown() {
  Dispatchers.resetMain()
 }

 @Test
 fun `searchProduct - should show loading state first`() = runTest {
  // Given
  val query = "test"
  val expectedResponse = Resource.Success(testProducts)
  whenever(searchProductUseCase(query)).thenReturn(expectedResponse)

  // When
  val states = mutableListOf<Resource<List<Product>>>()
  viewModel.products.observeForever { states.add(it) }

  viewModel.searchProduct(query)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(states[0] is Resource.Loading)
  assert(states[1] == expectedResponse)
 }

 @Test
 fun `searchProduct - success with products`() = runTest {
  // Given
  val query = "test"
  val expectedResponse = Resource.Success(testProducts)
  whenever(searchProductUseCase(query)).thenReturn(expectedResponse)

  // When
  viewModel.searchProduct(query)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.products.value == expectedResponse)
  assert((viewModel.products.value as Resource.Success).data.size == 2)

  val firstProduct = (viewModel.products.value as Resource.Success).data.first()
  assert(firstProduct.title == "Test Product 1")
  assert(firstProduct.saleState == 1)
  assert(firstProduct.salePrice == 85.0)
  assert(firstProduct.isFavorite)
 }

 @Test
 fun `searchProduct - success with empty list`() = runTest {
  // Given
  val query = "nonexistent"
  val expectedResponse = Resource.Success(emptyList<Product>())
  whenever(searchProductUseCase(query)).thenReturn(expectedResponse)

  // When
  viewModel.searchProduct(query)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.products.value == expectedResponse)
  assert((viewModel.products.value as Resource.Success).data.isEmpty())
 }

 @Test
 fun `searchProduct - network error`() = runTest {
  // Given
  val query = "test"
  val exception = IOException("Network error")
  val expectedResponse = Resource.Error(exception)
  whenever(searchProductUseCase(query)).thenReturn(expectedResponse)

  // When
  viewModel.searchProduct(query)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.products.value == expectedResponse)
 }

 @Test
 fun `searchProduct - general error`() = runTest {
  // Given
  val query = "test"
  val exception = RuntimeException("Something went wrong")
  val expectedResponse = Resource.Error(exception)
  whenever(searchProductUseCase(query)).thenReturn(expectedResponse)

  // When
  viewModel.searchProduct(query)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.products.value == expectedResponse)
 }

 @Test
 fun `searchProduct - with empty query`() = runTest {
  // Given
  val query = ""
  val expectedResponse = Resource.Success(emptyList<Product>())
  whenever(searchProductUseCase(query)).thenReturn(expectedResponse)

  // When
  viewModel.searchProduct(query)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.products.value == expectedResponse)
 }
}