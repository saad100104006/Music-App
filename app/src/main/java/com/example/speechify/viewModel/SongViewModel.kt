package com.example.speechify.ui.player

import android.util.Log
import androidx.lifecycle.*
import com.example.speechify.data.PlayerProgress
import com.example.speechify.data.SongModel
import com.example.speechify.ktx.combineWith
import com.example.speechify.repository.SongRepository
import com.example.speechify.util.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: SongRepository
) : ViewModel() {

    init {
        repository.updateDatabase()
    }

    private val isPlaying = MutableLiveData<Boolean>()
    private val currentMediaId = MutableLiveData<Int>()
    private val playerProgress = MutableLiveData<PlayerProgress>()

    val events = LiveEvent<Events>()
    val songs = repository.getSongs()
    var currentSongDetails = currentMediaId.switchMap {
        repository.getSongDetails(it)
    }


        val nextSongDetails: LiveData<SongModel?> =
            currentMediaId.combineWith(songs) { currentPlayingSongId: Int?, songList: List<SongModel>? ->
                if (currentPlayingSongId == null || songList == null)
                    return@combineWith null

                val indexOfCurrentlyPlayingSong: Int = songList.indexOfFirst {
                    it.id == currentPlayingSongId
                }

                return@combineWith songList[(indexOfCurrentlyPlayingSong + 1) % songList.size]
            }



    fun getIsPlaying(): LiveData<Boolean> = isPlaying
    fun getPlayerProgress(): LiveData<PlayerProgress> = playerProgress

    fun initiateActions(actions: Actions) {
        when (actions) {
            is SetPlayingStatus -> isPlaying.postValue(actions.status)
            is SetCurrentMediaId -> currentMediaId.postValue(actions.id)
            is SetProgress -> playerProgress.postValue(
                PlayerProgress(
                    actions.progress,
                    actions.currentPosition,
                    actions.duration
                )
            )
        }
    }

    fun initiateSharedAction(sharedActions: SharedActions) {
        Log.d(TAG, "initiateSharedAction: $sharedActions")
        when (sharedActions) {
            Play -> events.postValue(PlayEvent)
            Pause -> events.postValue(PauseEvent)
            PlayNext -> events.postValue(PlayNextEvent)
            PlayPrevious -> events.postValue(PlayPreviousEvent)
            FastForward -> events.postValue(FastForwardEvent)
            FastBackward -> events.postValue(FastBackwardEvent)
            is Seek -> events.postValue(SeekEvent(sharedActions.progress))
        }
    }


    override fun onCleared() {
        super.onCleared()
        repository.clearResources()
    }

    companion object {
        private const val TAG = "MainActivityViewModel"
    }
}

sealed class Actions
data class SetPlayingStatus(val status: Boolean) : Actions()
data class SetProgress(val progress: Int, val currentPosition: Long, val duration: Long) : Actions()
data class SetCurrentMediaId(val id: Int) : Actions()

sealed class SharedActions
object Play : SharedActions()
object Pause : SharedActions()
object PlayNext : SharedActions()
object PlayPrevious : SharedActions()
object FastForward : SharedActions()
object FastBackward : SharedActions()
data class Seek(val progress: Int) : SharedActions()


sealed class Events
object PlayEvent : Events()
object PauseEvent : Events()
object PlayNextEvent : Events()
object PlayPreviousEvent : Events()
object FastForwardEvent : Events()
object FastBackwardEvent : Events()
data class SeekEvent(val progress: Int) : Events()


