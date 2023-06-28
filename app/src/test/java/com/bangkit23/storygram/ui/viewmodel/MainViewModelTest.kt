package com.bangkit23.storygram.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.bangkit23.storygram.data.RepositoryMain
import com.bangkit23.storygram.data.Result
import com.bangkit23.storygram.data.remote.response.ApiResponse
import com.bangkit23.storygram.data.remote.response.ListStoryItem
import com.bangkit23.storygram.ui.adapter.StoryAdapter
import com.bangkit23.storygram.utils.DataDummy
import com.bangkit23.storygram.utils.MainDispatcherRule
import com.bangkit23.storygram.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.File


@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()


    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var repositoryMain: RepositoryMain


    //Test Tambah Story
    @ExperimentalCoroutinesApi
    @Test
    fun `When Get AddNewStory Success Should Not Null and Return Success`() = runTest {
        //Dummy data
        val token = "token"
        val imageFile = File("test_image.png")
        val imageRequestBody = imageFile.asRequestBody("image/png".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)
        val desc = "Sample description".toRequestBody("text/plain".toMediaTypeOrNull())


        val expectedResponseValue = ApiResponse(false, "Story added successfully")

        val expectedResponse = MutableLiveData<Result<ApiResponse>>()
        expectedResponse.value = Result.Success(expectedResponseValue) //set expected respond value

        //when " repositoryMain.addNewStory()" is called this will return expectedResponse.value"
        //this called stub
        `when`(
            repositoryMain.addNewStory(
                token,
                imagePart,
                desc
            )
        ).thenReturn(expectedResponse.value)

        val mainViewModel = MainViewModel(repositoryMain)
        val actualResponse = mainViewModel.addNewStory(token, imagePart, desc).getOrAwaitValue()

        Mockito.verify(repositoryMain).addNewStory(token, imagePart, desc) //verify that "addNewStory()" in repositoryMain is invoked during test

        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Success)
        assertEquals(expectedResponseValue, (actualResponse as Result.Success).data) //compare the expected response value with the actual response value

    }

    @ExperimentalCoroutinesApi
    @Test
    fun `When AddNewStory Network Error Should Return Error`()= runTest{

        //Dummy data
        val token = "token"
        val imageFile = File("test_image.png")
        val imageRequestBody = imageFile.asRequestBody("image/png".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)
        val desc = "Sample description".toRequestBody("text/plain".toMediaTypeOrNull())

        val expectedResponse = MutableLiveData<Result<ApiResponse>>()
        expectedResponse.value = Result.Error("Error") //set expected respond value

        `when`(
            repositoryMain.addNewStory(
                token,
                imagePart,
                desc
            )
        ).thenReturn(expectedResponse.value)

        val mainViewModel = MainViewModel(repositoryMain)
        val actualResponse = mainViewModel.addNewStory(token, imagePart, desc).getOrAwaitValue()
        Mockito.verify(repositoryMain).addNewStory(token, imagePart, desc) //verify that "addNewStory()" in repositoryMain is invoked during test

        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Error)
    }


    //Test buat paging
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value=data

        `when`(repositoryMain.loadListStory("token")).thenReturn(expectedStory)

        val mainViewModel2 = MainViewModel(repositoryMain)
        val actualStory: PagingData<ListStoryItem> = mainViewModel2.loadStory("token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)


        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size,differ.snapshot().size)
        assertEquals(dummyStory[0],differ.snapshot()[0])
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get Story Empty Should Return No Data`()= runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedData = MutableLiveData<PagingData<ListStoryItem>>()
        expectedData.value = data

        `when`(repositoryMain.loadListStory("token")).thenReturn(expectedData)

        val mainViewModel2 = MainViewModel(repositoryMain)
        val actualData: PagingData<ListStoryItem> = mainViewModel2.loadStory("token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualData)
        assertEquals(0, differ.snapshot().size)
    }



}

class StoryPagingSource: PagingSource<Int, LiveData<List<ListStoryItem>>>(){
    companion object {
        fun snapshot(item: List<ListStoryItem>): PagingData<ListStoryItem>{
            return PagingData.from(item)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(),0,1)
    }

}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}