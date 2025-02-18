package com.walid.ecommerce.presentation.favorites

import androidx.arch.core.executor.testing.InstantTaskExecutorRule

import androidx.lifecycle.Observer
import com.walid.ecommerce.common.Resource

import com.walid.ecommerce.domain.usecase.login.ForgotPasswordUseCase
import com.walid.ecommerce.presentation.forgotpassword.ForgotPasswordViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi


import kotlinx.coroutines.test.StandardTestDispatcher

import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock


import org.mockito.Mockito.verify

import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


@ExperimentalCoroutinesApi
 class ForgotPasswordViewModelTest {

  private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel

  @Mock
  private lateinit var forgotPasswordUseCase: ForgotPasswordUseCase  // Mock the use case

  @Mock
  lateinit var observer: Observer<Resource<Unit>>  // Mocked Observer

  private val testDispatcher = StandardTestDispatcher()  // Use StandardTestDispatcher for coroutines

  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()  // Ensures LiveData works in the test

  @Before
  fun setup() {
   MockitoAnnotations.openMocks(this)  // Initializes the mocks

   // Set the dispatcher for coroutines
   kotlinx.coroutines.Dispatchers.setMain(testDispatcher)

   // Initialize the ViewModel with the mocked use case
   forgotPasswordViewModel = ForgotPasswordViewModel(forgotPasswordUseCase)
  }

  @Test
  fun `test sendPasswordResetEmail success`() = runTest {
   // Arrange: Mock the use case to return a success response
   val email = "test@example.com"
   val mockSuccessResponse = Resource.Success(Unit)
   `when`(forgotPasswordUseCase.invoke(email)).thenReturn(mockSuccessResponse)

   // Act: Observe the result and call the method
   forgotPasswordViewModel.result.observeForever(observer)
   forgotPasswordViewModel.sendPasswordResetEmail(email)

   // Ensure coroutines are finished and all updates have been processed
   testDispatcher.scheduler.advanceUntilIdle()

   // Assert: Verify the results
   verify(observer).onChanged(Resource.Loading)  // It should first emit Loading
   verify(observer).onChanged(mockSuccessResponse)  // Then it should emit Success

   // Verify that the use case was actually invoked
   verify(forgotPasswordUseCase).invoke(email)

   // Cleanup: Remove observer to avoid memory leaks
   forgotPasswordViewModel.result.removeObserver(observer)
  }

  @Test
  fun `test sendPasswordResetEmail failure`() = runTest {
   // Arrange: Mock the use case to return an error response
   val email = "test@example.com"
   val mockErrorResponse = Resource.Error(Throwable("Network error"))
   `when`(forgotPasswordUseCase.invoke(email)).thenReturn(mockErrorResponse)

   // Act: Observe the result and call the method
   forgotPasswordViewModel.result.observeForever(observer)
   forgotPasswordViewModel.sendPasswordResetEmail(email)

   // Ensure coroutines are finished and all updates have been processed
   testDispatcher.scheduler.advanceUntilIdle()

   // Assert: Verify the results
   verify(observer).onChanged(Resource.Loading)  // It should first emit Loading
   verify(observer).onChanged(mockErrorResponse)  // Then it should emit Error

   // Verify that the use case was actually invoked
   verify(forgotPasswordUseCase).invoke(email)

   // Cleanup: Remove observer to avoid memory leaks
   forgotPasswordViewModel.result.removeObserver(observer)
  }


}
