package com.walid.ecommerce.presentation.signin
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseUser
import com.walid.ecommerce.common.Resource
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.walid.ecommerce.domain.usecase.login.SignInUseCase
import org.mockito.kotlin.any

@OptIn(ExperimentalCoroutinesApi::class)
//@OptIn(ExperimentalCoroutinesApi::class) explicitly opts into using
// experimental APIs, while @ExperimentalCoroutinesApi marks
// an API as experimental, requiring users to opt in before using
//@ExperimentalCoroutinesApi
class SignInViewModelTest {

 @get:Rule
 val instantTaskExecutorRule = InstantTaskExecutorRule()

 private val testDispatcher = StandardTestDispatcher()

 @Mock
 private lateinit var signInUseCase: SignInUseCase

 @Mock
 private lateinit var firebaseUser: FirebaseUser

 private lateinit var viewModel: SignInViewModel

 private val testEmail = "test@example.com"
 private val testPassword = "password123"

 @Before
 fun setup() {
  MockitoAnnotations.openMocks(this)
  Dispatchers.setMain(testDispatcher)
  viewModel = SignInViewModel(signInUseCase)
 }

 @After
 fun tearDown() {
  Dispatchers.resetMain()
 }

 @Test
 fun `signIn - should show loading state first`() = runTest {
  // Given
  val expectedResponse = Resource.Success(firebaseUser)
  whenever(signInUseCase(testEmail, testPassword)).thenReturn(expectedResponse)

  // When
  val states = mutableListOf<Resource<FirebaseUser>>()
  viewModel.result.observeForever { states.add(it) }

  viewModel.signInWithEmailAndPassword(testEmail, testPassword)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(states[0] is Resource.Loading)
  assert(states[1] == expectedResponse)
 }

 @Test
 fun `signIn - successful authentication`() = runTest {
  // Given
  val expectedResponse = Resource.Success(firebaseUser)
  whenever(signInUseCase(testEmail, testPassword)).thenReturn(expectedResponse)

  // When
  viewModel.signInWithEmailAndPassword(testEmail, testPassword)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.result.value == expectedResponse)
  verify(signInUseCase).invoke(testEmail, testPassword)
 }

 @Test
 fun `signIn - invalid credentials error`() = runTest {
  // Given
  val exception = FirebaseAuthInvalidCredentialsException("ERROR_INVALID_CREDENTIAL", "Invalid password")
  val expectedResponse = Resource.Error(exception)
  whenever(signInUseCase(testEmail, testPassword)).thenReturn(expectedResponse)

  // When
  viewModel.signInWithEmailAndPassword(testEmail, testPassword)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.result.value == expectedResponse)
 }

 @Test
 fun `signIn - user not found error`() = runTest {
  // Given
  val exception = FirebaseAuthInvalidUserException("ERROR_USER_NOT_FOUND", "No user found")
  val expectedResponse = Resource.Error(exception)
  whenever(signInUseCase(testEmail, testPassword)).thenReturn(expectedResponse)

  // When
  viewModel.signInWithEmailAndPassword(testEmail, testPassword)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.result.value == expectedResponse)
 }

 @Test
 fun `signIn - network error`() = runTest {
  // Given
  val exception = java.io.IOException("Network error")
  val expectedResponse = Resource.Error(exception)
  whenever(signInUseCase(testEmail, testPassword)).thenReturn(expectedResponse)

  // When
  viewModel.signInWithEmailAndPassword(testEmail, testPassword)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.result.value == expectedResponse)
 }

 @Test
 fun `signIn - general error`() = runTest {
  // Given
  val exception = RuntimeException("Unknown error")
  val expectedResponse = Resource.Error(exception)
  whenever(signInUseCase(testEmail, testPassword)).thenReturn(expectedResponse)

  // When
  viewModel.signInWithEmailAndPassword(testEmail, testPassword)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.result.value == expectedResponse)
 }

 @Test
 fun `signIn - empty email`() = runTest {
  // Given
  val emptyEmail = ""
  val exception = IllegalArgumentException("Email cannot be empty")
  val expectedResponse = Resource.Error(exception)
  whenever(signInUseCase(emptyEmail, testPassword)).thenReturn(expectedResponse)

  // When
  viewModel.signInWithEmailAndPassword(emptyEmail, testPassword)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.result.value == expectedResponse)
 }

 @Test
 fun `signIn - empty password`() = runTest {
  // Given
  val emptyPassword = ""
  val exception = IllegalArgumentException("Password cannot be empty")
  val expectedResponse = Resource.Error(exception)
  whenever(signInUseCase(testEmail, emptyPassword)).thenReturn(expectedResponse)

  // When
  viewModel.signInWithEmailAndPassword(testEmail, emptyPassword)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(viewModel.result.value == expectedResponse)
 }

 @Test
 fun `signIn - consecutive calls should update loading state`() = runTest {
  // Given
  val expectedResponse = Resource.Success(firebaseUser)
  whenever(signInUseCase(any(), any())).thenReturn(expectedResponse)

  // When
  val states = mutableListOf<Resource<FirebaseUser>>()
  viewModel.result.observeForever { states.add(it) }

  viewModel.signInWithEmailAndPassword(testEmail, testPassword)
  testDispatcher.scheduler.advanceUntilIdle()

  viewModel.signInWithEmailAndPassword(testEmail, testPassword)
  testDispatcher.scheduler.advanceUntilIdle()

  // Then
  assert(states[0] is Resource.Loading)
  assert(states[1] == expectedResponse)
  assert(states[2] is Resource.Loading)
  assert(states[3] == expectedResponse)
 }
}