package com.example.speechify.ui

import android.Manifest
import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import com.example.speechify.*
import com.example.speechify.ui.player.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import java.io.File
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class SongActivity : AppCompatActivity(R.layout.activity_main) {
    private val viewModel: MainActivityViewModel by viewModels()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        player = SimpleExoPlayer.Builder(this).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
        }

        viewModel.songs.observe(this) { songList ->
            val mediaItemList: List<MediaItem> = songList.map {
                val uri = getUriFromRawFile(it.rawFileId)
                MediaItem.Builder().setUri(uri).setMediaId("${it.id}").build()
            }

            player.apply {
                addMediaItems(mediaItemList)
                prepare()
            }

            requestRuntimePermission()
        }

        viewModel.events.observe(this) {
            when (it) {
                PlayEvent -> player.play()
                PauseEvent -> player.pause()
                FastForwardEvent -> player.seekTo(player.currentPosition + 10000)
                FastBackwardEvent -> player.seekTo(player.currentPosition - 10000)
                PlayNextEvent -> player.next()
                PlayPreviousEvent -> player.previous()
                is SeekEvent -> player.seekTo((player.duration / 100) * it.progress)
            }
        }

        player.addListener(object : Player.EventListener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                viewModel.initiateActions(SetCurrentMediaId(mediaItem?.mediaId?.toIntOrNull() ?: 0))
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                viewModel.initiateActions(SetPlayingStatus(isPlaying))
            }
        })

        // interval observable to keep track of player progress changed
        Observable.interval(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val duration = player.duration
                    val currentPosition = player.currentPosition
                    val progress = ((currentPosition * 100) / duration).toInt()
                    viewModel.initiateActions(SetProgress(progress, currentPosition, duration))
                }, Throwable::printStackTrace)
                .addTo(compositeDisposable)


    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        compositeDisposable.clear()
    }


    /**
     * Returns uri for a given raw file id
     */
    private fun getUriFromRawFile(@RawRes rawFile: Int): Uri {
        return Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE
                        + File.pathSeparator + File.separator + File.separator
                        + this.packageName
                        + File.separator
                        + rawFile
        )
    }

    private fun requestRuntimePermission() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) = Unit

                    override fun onPermissionDenied(response: PermissionDeniedResponse) =
                            Toast.makeText(applicationContext,"Visualization will not work without permission",Toast.LENGTH_SHORT).show()

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) =
                            Unit
                }).check()
    }

    companion object {
        lateinit var player: SimpleExoPlayer
    }


}