package com.walid.ecommerce.presentation.signup





import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.walid.ecommerce.common.Resource
import com.walid.ecommerce.data.model.User
import com.walid.ecommerce.domain.usecase.login.CheckCurrentUserUseCase
import com.walid.ecommerce.domain.usecase.login.SignUpUseCase
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {

 @get:Rule
 val instantTaskExecutorRule = InstantTaskExecutorRule()

 private val testDispatcher = StandardTestDispatcher()

 @Mock
 private lateinit var signUpUseCase: SignUpUseCase

 @Mock
 private lateinit var checkCurrentUserUseCase: CheckCurrentUserUseCase

 private lateinit var viewModel: SignUpViewModel

 private val testUser = User(


  email = "test@example.com",
  nickname = "Test User",
  phoneNumber = "1234567890"
 )
 private val testPassword = "password123"

 @Before
 fun setup() {
  MockitoAnnotations.openMocks(this)
  Dispatchers.setMain(testDispatcher)
  viewModel = SignUpViewModel(signUpUseCase, checkCurrentUserUseCase)
 }

 @After
 fun tearDown() {
  Dispatchers.resetMain()
 }

 @Test
 fun `signUp - should show loading state first`() = runTest {
  // Given
  val expectedResponse = Resource.Success(Unit)
  whenever(signUpUseCase(testUser, testPassword)).thenReturn(expectedResponse)

  // When
  val states = mutableListOf<Resource<Unit>>()
  viewModel.result.observeForever { states.add(it) }

  viewModel.signUpWithEmailAndPassword(testUser, testPassword)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(states[0] is Resource.Loading)
  assert(states[1] == expectedResponse)
 }

 @Test
 fun `signUp - successful registration`() = runTest {
  // Given
  val expectedResponse = Resource.Success(Unit)
  whenever(signUpUseCase(testUser, testPassword)).thenReturn(expectedResponse)

  // When
  viewModel.signUpWithEmailAndPassword(testUser, testPassword)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.result.value == expectedResponse)
  verify(signUpUseCase).invoke(testUser, testPassword)
 }

 @Test
 fun `signUp - invalid credentials error`() = runTest {
  // Given
  val exception = FirebaseAuthInvalidCredentialsException("ERROR_INVALID_CREDENTIAL", "Invalid credentials")
  val expectedResponse = Resource.Error(exception)
  whenever(signUpUseCase(testUser, testPassword)).thenReturn(expectedResponse)

  // When
  viewModel.signUpWithEmailAndPassword(testUser, testPassword)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.result.value == expectedResponse)
 }

 @Test
 fun `signUp - user already exists error`() = runTest {
  // Given
  val exception = FirebaseAuthUserCollisionException("ERROR_USER_COLLISION", "User already exists")
  val expectedResponse = Resource.Error(exception)
  whenever(signUpUseCase(testUser, testPassword)).thenReturn(expectedResponse)

  // When
  viewModel.signUpWithEmailAndPassword(testUser, testPassword)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.result.value == expectedResponse)
 }

 @Test
 fun `signUp - network error`() = runTest {
  // Given
  val exception = IOException("Network error")
  val expectedResponse = Resource.Error(exception)
  whenever(signUpUseCase(testUser, testPassword)).thenReturn(expectedResponse)

  // When
  viewModel.signUpWithEmailAndPassword(testUser, testPassword)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.result.value == expectedResponse)
 }

 @Test
 fun `signUp - general error`() = runTest {
  // Given
  val exception = RuntimeException("Unknown error")
  val expectedResponse = Resource.Error(exception)
  whenever(signUpUseCase(testUser, testPassword)).thenReturn(expectedResponse)

  // When
  viewModel.signUpWithEmailAndPassword(testUser, testPassword)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.result.value == expectedResponse)
 }

 @Test
 fun `checkCurrentUser - returns true if user is logged in`() = runTest {
  // Given
  whenever(checkCurrentUserUseCase()).thenReturn(true)

  // When
  viewModel.checkCurrentUser()
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.checkCurrentUser.value == true)
 }

 @Test
 fun `checkCurrentUser - returns false if user is not logged in`() = runTest {
  // Given
  whenever(checkCurrentUserUseCase()).thenReturn(false)

  // When
  viewModel.checkCurrentUser()
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.checkCurrentUser.value == false)
 }
}