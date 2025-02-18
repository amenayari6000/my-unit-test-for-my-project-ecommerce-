package com.walid.ecommerce.presentation.profile
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.walid.ecommerce.common.Resource
import com.walid.ecommerce.data.model.User
import com.walid.ecommerce.domain.usecase.login.GetCurrentUserUseCase
import com.walid.ecommerce.domain.usecase.login.SignOutUseCase
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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

 @get:Rule
 val instantTaskExecutorRule = InstantTaskExecutorRule()

 private val testDispatcher = StandardTestDispatcher()

 @Mock
 private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

 @Mock
 private lateinit var signOutUseCase: SignOutUseCase

 private lateinit var viewModel: ProfileViewModel

 private val testUser = User(
  email = "test@example.com",
  nickname = "TestUser",
  phoneNumber = "+1234567890"
 )

 @Before
 fun setup() {
  MockitoAnnotations.openMocks(this)
  Dispatchers.setMain(testDispatcher)
 }

 @After
 fun tearDown() {
  Dispatchers.resetMain()
 }

 @Test
 fun `getCurrentUser - initial state should be Loading`() = runTest {
  // When
  viewModel = ProfileViewModel(getCurrentUserUseCase, signOutUseCase)

  // Then
  assert(viewModel.currentUser.value is Resource.Loading)
 }

 @Test
 fun `getCurrentUser - success case`() = runTest {
  // Given
  val expectedResponse = Resource.Success(testUser)
  whenever(getCurrentUserUseCase()).thenReturn(expectedResponse)

  // When
  viewModel = ProfileViewModel(getCurrentUserUseCase, signOutUseCase)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.currentUser.value == expectedResponse)
 }

 @Test
 fun `getCurrentUser - error case`() = runTest {
  // Given
  val exception = RuntimeException("Failed to get user")
  val expectedResponse = Resource.Error(exception)
  whenever(getCurrentUserUseCase()).thenReturn(expectedResponse)

  // When
  viewModel = ProfileViewModel(getCurrentUserUseCase, signOutUseCase)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.currentUser.value == expectedResponse)
 }

 @Test
 fun `getCurrentUser - network error case`() = runTest {
  // Given
  val exception = java.io.IOException("No internet connection")
  val expectedResponse = Resource.Error(exception)
  whenever(getCurrentUserUseCase()).thenReturn(expectedResponse)

  // When
  viewModel = ProfileViewModel(getCurrentUserUseCase, signOutUseCase)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.currentUser.value == expectedResponse)
 }

 @Test
 fun `signOut - should call signOutUseCase`() = runTest {
  // Given
  viewModel = ProfileViewModel(getCurrentUserUseCase, signOutUseCase)

  // When
  viewModel.signOut()
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  verify(signOutUseCase).invoke()
 }
}