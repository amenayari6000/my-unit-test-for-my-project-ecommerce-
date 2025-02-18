package com.walid.ecommerce.presentation.paymentsuccess

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.walid.ecommerce.common.Resource
import com.walid.ecommerce.data.model.CRUDResponse
import com.walid.ecommerce.domain.usecase.bag.ClearBagUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentSuccessViewModelTest {

 @get:Rule
 val instantTaskExecutorRule = InstantTaskExecutorRule()

 private val testDispatcher = StandardTestDispatcher()

 @Mock
 private lateinit var clearBagUseCase: ClearBagUseCase

 private lateinit var viewModel: PaymentSuccessViewModel

 @Before
 fun setup() {
  MockitoAnnotations.openMocks(this)
  Dispatchers.setMain(testDispatcher)
  viewModel = PaymentSuccessViewModel(clearBagUseCase)
 }

 @After
 fun tearDown() {
  Dispatchers.resetMain()
 }

 @Test
 fun `clearBag success scenario`() = runTest {
  // Given
  val successResponse = CRUDResponse(1, "Success")
  val expectedResponse = Resource.Success(successResponse)
  whenever(clearBagUseCase()).thenReturn(expectedResponse)

  // When
  viewModel.clearBag()

  // Then
  val results = mutableListOf<Resource<CRUDResponse>>()
  viewModel.result.observeForever { results.add(it) }

  testDispatcher.scheduler.advanceUntilIdle()

  assert(results[0] == Resource.Loading)
  assert(results[1] == expectedResponse)
 }

 @Test
 fun `clearBag error scenario`() = runTest {
  // Given
  val exception = RuntimeException("Network error")
  val expectedResponse = Resource.Error(exception)
  whenever(clearBagUseCase()).thenReturn(expectedResponse)

  // When
  viewModel.clearBag()

  // Then
  val results = mutableListOf<Resource<CRUDResponse>>()
  viewModel.result.observeForever { results.add(it) }

  testDispatcher.scheduler.advanceUntilIdle()

  assert(results[0] == Resource.Loading)
  assert(results[1] == expectedResponse)
 }

 @Test
 fun `clearBag network error scenario`() = runTest {
  // Given
  val networkException = java.io.IOException("No internet connection")
  val expectedResponse = Resource.Error(networkException)
  whenever(clearBagUseCase()).thenReturn(expectedResponse)

  // When
  viewModel.clearBag()

  // Then
  val results = mutableListOf<Resource<CRUDResponse>>()
  viewModel.result.observeForever { results.add(it) }

  testDispatcher.scheduler.advanceUntilIdle()

  assert(results[0] == Resource.Loading)
  assert(results[1] == expectedResponse)
 }
}