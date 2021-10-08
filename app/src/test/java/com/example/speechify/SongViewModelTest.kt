package com.example.speechify


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import com.example.speechify.data.PlayerProgress
import com.example.speechify.data.SongModel
import com.example.speechify.db.service.SongService
import com.example.speechify.repository.SongRepository
import com.example.speechify.ui.player.MainActivityViewModel
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.internal.schedulers.ExecutorScheduler
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executor


class SongViewModelTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Mock
    var lifecycleOwner: LifecycleOwner? = null

    var lifecycle: Lifecycle? = null

    @Mock
    lateinit var songService: SongService

    @Mock
    lateinit var observer: Observer<PlayerProgress>

    var viewModel: MainActivityViewModel = Mockito.mock(MainActivityViewModel::class.java)



    @Before
    fun setUpRxSchedulers() {
        MockitoAnnotations.initMocks(this)
        lifecycle = LifecycleRegistry(lifecycleOwner!!)
        val immediate = object : Scheduler() {
            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker(Executor { it.run() }, true, true)
            }
        }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediate }

    }

    @Test
    fun song_detail_test(){
        val song  =  MutableLiveData(SongModel( 3,
                "Let The Bodies Hit The Floor Alone",
                "Drowning Pool |  Sinner | 2001",
                "let_the_bodies_hit_the_floor",
                R.raw.let_the_bodies_hit_the_floor,
                R.drawable.bodies)
        )
        val repository = SongRepository(songService)

        Mockito.`when`(viewModel.currentSongDetails ).thenReturn(song)
        Mockito.`when`(repository.getSongDetails(3)).thenReturn(song)

        Assert.assertEquals(repository.getSongDetails(3),viewModel.currentSongDetails)

    }

    @Test
    fun get_songs_test(){

        val songList  =  MutableLiveData<List<SongModel>>(listOf(SongModel( 3,
                "Let The Bodies Hit The Floor Alone",
                "Drowning Pool |  Sinner | 2001",
                "let_the_bodies_hit_the_floor",
                R.raw.let_the_bodies_hit_the_floor,
                R.drawable.bodies),
                SongModel( 3,
                "Let The Bodies Hit The Floor Alone",
                "Drowning Pool |  Sinner | 2001",
                "let_the_bodies_hit_the_floor",
                R.raw.let_the_bodies_hit_the_floor,
                R.drawable.bodies))
        )


        val repository = SongRepository(songService)

        Mockito.`when`(repository.getSongs()).thenReturn(songList)
        Mockito.`when`(viewModel.songs).thenReturn(songList)

        Assert.assertEquals(repository.getSongs(),viewModel.songs)

    }

    @Test
    fun get_song_is_playing_test(){
        val isSongPlaying:LiveData<Boolean> = MutableLiveData(true)

        Mockito.`when`(viewModel.getIsPlaying()).thenReturn(isSongPlaying)

        Assert.assertEquals(isSongPlaying,viewModel.getIsPlaying())

    }
}