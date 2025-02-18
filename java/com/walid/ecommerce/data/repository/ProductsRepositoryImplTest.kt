package com.walid.ecommerce.data.repository

import com.walid.ecommerce.data.model.Product
import com.walid.ecommerce.domain.datasource.local.LocalDataSource
import com.walid.ecommerce.domain.datasource.remote.RemoteDataSource
import com.walid.ecommerce.domain.repository.ProductsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProductsRepositoryImplTest {

    private lateinit var productsRepository: ProductsRepository

    @Mock
    private lateinit var remoteDataSource: RemoteDataSource

    @Mock
    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this)

        // Create the repository instance with mocked dependencies
        productsRepository = ProductsRepositoryImpl(remoteDataSource, localDataSource)
    }

    @Test
    fun `getProducts should return mapped products list`() = runTest {
        // Given
        val mockProducts = listOf(
            Product(1, "Category1", 10, "Description", "Image1", "Image2", "Image3", 100.0, 4.5, "Product1", 1, false, null),
            Product(2, "Category2", 5, "Description", "Image1", "Image2", "Image3", 200.0, 4.0, "Product2", 0, false, null)
        )
        val mockFavorites = listOf("Product1", "Product3")

        // Mock remoteDataSource and localDataSource
        `when`(remoteDataSource.getProducts()).thenReturn(mockProducts)
        `when`(localDataSource.getFavoritesNamesList()).thenReturn(mockFavorites)

        // When
        val result = productsRepository.getProducts()

       // Then
        assertEquals(2, result.size)  // There should be 2 products returned.

        // Assert Product1: Should have salePrice (on sale)
       // assertEquals(85.0, result[0].salePrice)  // Product1 should have a salePrice (85% of 100.0)

        // Assert Product2: Should have no salePrice (not on sale)
       // assertNull(result[1].salePrice)
        // Verify mock method calls
        verify(remoteDataSource).getProducts()

    }

    @Test
    fun `getSaleProducts should return sale products list`() = runTest {
        // Given
        val mockSaleProducts = listOf(
            Product(1, "Category1", 10, "Description", "Image1", "Image2", "Image3", 100.0, 4.5, "SaleProduct1", 1, false, null),

        )
        val mockFavorites = listOf("SaleProduct1", "SaleProduct3")

        // Mock remoteDataSource and localDataSource
        `when`(remoteDataSource.getSaleProducts()).thenReturn(mockSaleProducts)
        `when`(localDataSource.getFavoritesNamesList()).thenReturn(mockFavorites)

        // When
        val result = productsRepository.getSaleProducts()

        // Then
        assertEquals(1, result.size)

        // Assert Product1: Should have salePrice (on sale)
        assertEquals(85.0, result[0].salePrice)  // Product1 should have a salePrice (85% of 100.0)

        verify(remoteDataSource).getSaleProducts()

    }

    @Test
    fun `addToBag should call remoteDataSource addToBag`() = runTest {
        // Given
        val product = Product(1, "Category1", 10, "Description", "Image1", "Image2", "Image3", 100.0, 4.5, "Product1", 1, false, null)

        // When
        productsRepository.addToBag(product)

        // Then
        verify(remoteDataSource).addToBag(product)
    }

    @Test
    fun `getCategories should call remoteDataSource getCategories`() = runTest {
        // Given
        val categories = listOf("Electronics", "Clothing")
        `when`(remoteDataSource.getCategories()).thenReturn(categories)

        // When
        val result = productsRepository.getCategories()

        // Then
        assertEquals(categories, result)
        verify(remoteDataSource).getCategories()
    }

    @Test
    fun `getBagProductsCount should return count from remoteDataSource`() = runTest {
        // Given
        val count = 5
        `when`(remoteDataSource.getBagProductsCount()).thenReturn(count)

        // When
        val result = productsRepository.getBagProductsCount()

        // Then
        assertEquals(count, result)
        verify(remoteDataSource).getBagProductsCount()
    }

    @Test
    fun `deleteFromBag should call remoteDataSource deleteFromBag`() = runTest {
        // Given
        val productId = 1

        // When
        productsRepository.deleteFromBag(productId)

        // Then
        verify(remoteDataSource).deleteFromBag(productId)
    }

    @Test
    fun `getProductsByCategory should return products from remoteDataSource`() = runTest {
        // Given
        val category = "Electronics"
        val mockProducts = listOf(Product(1, category, 10, "Description", "Image1", "Image2", "Image3", 100.0, 4.5, "Product1", 1, false, null))
        `when`(remoteDataSource.getProductsByCategory(category)).thenReturn(mockProducts)

        // When
        val result = productsRepository.getProductsByCategory(category)

        // Then
        assertEquals(mockProducts, result)
        verify(remoteDataSource).getProductsByCategory(category)
    }

    @Test
    fun `clearBag should call remoteDataSource clearBag`() = runTest {
        // When
        productsRepository.clearBag()

        // Then
        verify(remoteDataSource).clearBag()
    }

    @Test
    fun `searchProduct should return mapped products list`() = runTest {
        // Given
        val mockProducts = listOf(
            Product(1, "Category1", 10, "Description", "Image1", "Image2", "Image3", 100.0, 4.5, "Product1", 1, false, null),
            Product(2, "Category2", 5, "Description", "Image1", "Image2", "Image3", 200.0, 4.0, "Product2", 0, false, null)
        )
        val mockFavorites = listOf("Product1", "Product3")

        // Mock remoteDataSource and localDataSource
        `when`(remoteDataSource.searchProduct("query")).thenReturn(mockProducts)
        `when`(localDataSource.getFavoritesNamesList()).thenReturn(mockFavorites)

        // When
        val result = productsRepository.searchProduct("query")

        // Then
        assertEquals(2, result.size)
        assertTrue(result[0].isFavorite)  // Product1 should be favorite
        assertTrue(!result[1].isFavorite)  // Product2 should not be favorite
        verify(remoteDataSource).searchProduct("query")
        verify(localDataSource).getFavoritesNamesList()
    }

    @Test
    fun `addToFavorites should call localDataSource addToFavorites`() = runTest {
        // Given
        val product = Product(1, "Category1", 10, "Description", "Image1", "Image2", "Image3", 100.0, 4.5, "Product1", 1, false, null)

        // When
        productsRepository.addToFavorites(product)

        // Then
        verify(localDataSource).addToFavorites(product)
    }

    @Test
    fun `getFavorites should return list from localDataSource`() = runTest {
        // Given
        val mockFavorites = listOf(Product(1, "Category1", 10, "Description", "Image1", "Image2", "Image3", 100.0, 4.5, "Product1", 1, true, 85.0))
        `when`(localDataSource.getFavorites()).thenReturn(mockFavorites)

        // When
        val result = productsRepository.getFavorites()

        // Then
        assertEquals(mockFavorites, result)
        verify(localDataSource).getFavorites()
    }

    @Test
    fun `deleteFromFavorites should call localDataSource deleteFromFavorites`() = runTest {
        // Given
        val productId = 1

        // When
        productsRepository.deleteFromFavorites(productId)

        // Then
        verify(localDataSource).deleteFromFavorites(productId)
    }

    @Test
    fun `clearFavorites should call localDataSource clearFavorites`() = runTest {
        // When
        productsRepository.clearFavorites()

        // Then
        verify(localDataSource).clearFavorites()
    }
}
