package com.example.photoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.photoapp.core.utils.convertStringToDate
import com.example.photoapp.core.utils.normalizedDate
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.example.photoapp.features.faktura.presentation.screen.FakturaScreenViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.intArrayOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
@Config(sdk = [33])
@RunWith(RobolectricTestRunner::class)
class FakturaScreenViewModelTest {

    companion object { // mockuje firebase usera przed włączeniem testu, poniważ klasy z modelu mają domyślną funkcję pozyskania ID
        @JvmStatic
        @BeforeClass
        fun globalFirebaseMock() {
            mockkStatic(FirebaseAuth::class)

            val fakeUser = mockk<FirebaseUser>()
            every { fakeUser.uid } returns "test-user-id"
            every { FirebaseAuth.getInstance().currentUser } returns fakeUser
        }

        @JvmStatic
        @AfterClass
        fun removeFirebaseMock() {
            unmockkStatic(FirebaseAuth::class) // zawsze unmock
        }
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val fakturaRepository: FakturaRepository = mockk(relaxed = true)
    private lateinit var viewModel: FakturaScreenViewModel

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = FakturaScreenViewModel(
            repository=fakturaRepository,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.setMain(Dispatchers.Default)
    }

    @Test
    fun `toggleDeleteMode should enable delete mode when initially false`() = runTest {
        // Given
        Assert.assertFalse(viewModel.isDeleteMode.value) // isDeleteMode is false

        // When
        viewModel.toggleDeleteMode()

        // Then
        Assert.assertTrue(viewModel.isDeleteMode.value)
    }

    @Test
    fun `toggleDeleteMode should disable delete mode when initially true and clear selected items`() = runTest {
        // Given
        viewModel.setTestData(isdeleteMode = true)
        Assert.assertTrue(viewModel.isDeleteMode.value) // isDeleteMode is false

        // When
        viewModel.toggleDeleteMode()

        // Then
        Assert.assertFalse(viewModel.isDeleteMode.value)
    }

    @Test
    fun `toggleItemSelection should add item if item was not selected`() = runTest {
        // Given
        Assert.assertTrue(viewModel.selectedItems.isEmpty())
        val faktura = Faktura.default()

        // When
        viewModel.toggleItemSelection(faktura)

        // Then
        Assert.assertTrue(viewModel.selectedItems.contains(faktura))
    }

    @Test
    fun `toggleItemSelection should remove item if item was already selected`() = runTest {
        // Given
        val faktura = Faktura.default()
        viewModel.setTestData(selectedItems = listOf(faktura))

        // When
        viewModel.toggleItemSelection(faktura)

        // Then
        Assert.assertFalse(viewModel.selectedItems.contains(faktura))
    }

    @Test
    fun `deleteSelectedItems should delete all selected items from repository`() = runTest {
        val latch = CountDownLatch(1)

        // Given
        val f1 = Faktura.default().copy(id = "1")
        val f2 = Faktura.default().copy(id = "2")
        val f3 = Faktura.default().copy(id = "3")
        viewModel.setTestData(isdeleteMode = true, selectedItems = listOf(f1, f2, f3))
        every { fakturaRepository.deleteFaktura(any()) } returns Unit

        // When
        viewModel.deleteSelectedItems()
        latch.countDown()
        latch.await(1, TimeUnit.SECONDS)

        // Then
        verify(exactly = 3) { fakturaRepository.deleteFaktura(any()) }
    }

    @Test
    fun `deleteSelectedItems should clear selected items after deletion`() = runTest {
        // Given
        val f1 = Faktura.default().copy(id = "1")
        val f2 = Faktura.default().copy(id = "2")
        val f3 = Faktura.default().copy(id = "3")
        viewModel.setTestData(isdeleteMode = true, selectedItems = listOf(f1, f2, f3))
        every { fakturaRepository.deleteFaktura(any()) } returns Unit

        // When
        viewModel.deleteSelectedItems()

        withTimeout(2000) {
            while (viewModel.selectedItems.isNotEmpty()) {
                delay(50)
            }
        }

        // Then
        Assert.assertTrue(viewModel.selectedItems.isEmpty())
    }

    @Test
    fun `deleteSelectedItems should turn off delete mode after deletion`() = runTest {
        // Given
        val f1 = Faktura.default().copy(id = "1")
        val f2 = Faktura.default().copy(id = "2")
        val f3 = Faktura.default().copy(id = "3")
        viewModel.setTestData(isdeleteMode = true, selectedItems = listOf(f1, f2, f3))
        every { fakturaRepository.deleteFaktura(any()) } returns Unit

        // When
        viewModel.deleteSelectedItems()

        withTimeout(2000) {
            while (viewModel.selectedItems.isNotEmpty()) {
                delay(50)
            }
        }

        // Then
        Assert.assertFalse(viewModel.isDeleteMode.value)
    }

    @Test
    fun `applyFilters should update groupedFaktury based on filtered list`() = runTest {
        // Given
        val f1 = Faktura.default().copy(id = "1", dataWystawienia = convertStringToDate("2000-01-01"))
        val f2 = Faktura.default().copy(id = "2", dataWystawienia = convertStringToDate("2001-01-01"))
        val f3 = Faktura.default().copy(id = "3", dataWystawienia = convertStringToDate("2001-01-01"))
        var list = listOf(f1, f2, f3)

        // When
        viewModel.applyFilters(list)

        // Then
        val dates = viewModel.groupedFaktury.value.keys.filterNotNull().toList()
        val group2000 = viewModel.groupedFaktury.value[convertStringToDate("2000-01-01")]!!
        val group2001 = viewModel.groupedFaktury.value[convertStringToDate("2001-01-01")]!!

        Assert.assertTrue(dates[0] > dates[1] )
        Assert.assertEquals(1, group2000.size)
        Assert.assertEquals(2, group2001.size)
    }

    @Test
    fun `getGroupedFakturaList should group faktury by normalized date descending`() = runTest {
        // Given
        val f1 = Faktura.default().copy(id = "1", dataWystawienia = convertStringToDate("2002-01-01"))
        val f2 = Faktura.default().copy(id = "2", dataWystawienia = convertStringToDate("2004-01-01"))
        val f3 = Faktura.default().copy(id = "3", dataWystawienia = convertStringToDate("2004-01-01"))
        var list = listOf(f1, f2, f3)

        // When
        val result = viewModel.getGroupedFakturaList(list)

        // Then
        val dates = result.keys.filterNotNull().toList()
        val group2002 = result[convertStringToDate("2002-01-01")]!!
        val group2004 = result[convertStringToDate("2004-01-01")]!!

        Assert.assertTrue(dates[0] > dates[1] )
        Assert.assertEquals(1, group2002.size)
        Assert.assertEquals(2, group2004.size)
    }

    @Test
    fun `getGroupedFakturaList should handle empty list and return empty map`() = runTest {
        // Given

        // When
        val result = viewModel.getGroupedFakturaList(emptyList())

        // Then
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun `setGroupedFaktura should update groupedFaktury flow value`() = runTest {
        // Given
        val f1 = Faktura.default().copy(id = "1", dataWystawienia = convertStringToDate("2002-01-01"))

        // When
        val GFL = viewModel.getGroupedFakturaList(listOf(f1))
        viewModel.setGroupedFaktura(GFL)

        // Then
        viewModel.groupedFaktury.test {
            val value = awaitItem()
            Assert.assertEquals(1, value.size) // size of our groupedFaktura == 1
        }
    }

    @Test
    fun `getCurrentlyShowingList should return flattened list of all grouped faktury`() = runTest {
        // Given
        val f1 = Faktura.default().copy(id = "1", dataWystawienia = convertStringToDate("2020-01-01"))
        val f2 = Faktura.default().copy(id = "2", dataWystawienia = convertStringToDate("2020-01-02"))
        val f3 = Faktura.default().copy(id = "3", dataWystawienia = convertStringToDate("2020-01-01"))

        val groupedMap = mapOf(
            convertStringToDate("2020-01-02")?.normalizedDate() to listOf(f2),
            convertStringToDate("2020-01-01")?.normalizedDate() to listOf(f1, f3)
        )

        viewModel.setGroupedFaktura(groupedMap)

        // When
        val result = viewModel.getCurrentlyShowingList()

        // Then
        Assert.assertEquals(3, result.size)
        Assert.assertTrue(result.containsAll(listOf(f1, f2, f3)))
    }


    @Test
    fun `getCurrentlyShowingList should return empty list when groupedFaktury is empty`() = runTest {
        // Given
        viewModel.setGroupedFaktura(emptyMap())

        // When
        val result = viewModel.getCurrentlyShowingList()

        // Then
        Assert.assertTrue(result.isEmpty())
    }


    @Test
    fun `deleteSelectedItems should do nothing if no items selected`() = runTest {
        val latch = CountDownLatch(1)
        // Given
        viewModel.setTestData(isdeleteMode = true, selectedItems = emptyList())
        every { fakturaRepository.deleteFaktura(any()) } returns Unit

        // When
        viewModel.deleteSelectedItems()
        latch.countDown()
        latch.await()

        // Then
        verify(exactly = 0) { fakturaRepository.deleteFaktura(any()) }

    }
}