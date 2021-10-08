package com.example.speechify.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speechify.R
import com.example.speechify.ktx.toMinSec
import com.example.speechify.ktx.visibleIfOrInvisible
import com.example.speechify.ui.player.*
import com.example.speechify.ui.playlist.PlayListAdapter
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.userChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import kotlinx.android.synthetic.main.fragment_play_list.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PlayListFragment : Fragment(R.layout.fragment_play_list) {

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = PlayListAdapter()
        upNextRecyclerView.adapter = adapter
        upNextRecyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        upNextRecyclerView.layoutManager = layoutManager

        activityViewModel.songs.observe(viewLifecycleOwner) {
            Log.d(TAG, "onActivityCreated: submit list")
            adapter.submitList(it)
        }

        activityViewModel.currentSongDetails.observe(viewLifecycleOwner) { currentSong ->
            nowPlayingTitleTv.text = currentSong.title
            nowPlayingDetailsTv.text = currentSong.details
            currentSongImage.setImageResource(currentSong.imageFile)


            val songList = activityViewModel.songs.value ?: emptyList()

            Log.d(TAG, "onActivityCreated: currentSong $currentSong")

            // don't do anything if song list is not loaded
            // as we expect a list with at least one item
            val currentSongIndex = songList.indexOfFirst {
                currentSong.id == it.id
            }

            val position: Int = (currentSongIndex + 1) % songList.size

            Handler(Looper.getMainLooper()).postDelayed({
                layoutManager.smoothScrollToPosition(upNextRecyclerView, null, songList.size + 1)
                Handler(Looper.getMainLooper()).postDelayed({
                    layoutManager.smoothScrollToPosition(upNextRecyclerView, null, position)
                }, 50)
            }, 50)
        }


        activityViewModel.getIsPlaying().observe(viewLifecycleOwner) {
            playImage.visibleIfOrInvisible(!it)
            pauseImage.visibleIfOrInvisible(it)
        }

        activityViewModel.getPlayerProgress().observe(viewLifecycleOwner) {
            seekBar.progress = it.progress
            currentProgressTimeTv.text = it.currentPosition.toMinSec()
            totalTimeTv.text = it.duration.toMinSec()
        }

        seekBar.userChanges()
            .skipInitialValue()
            .subscribe({
                activityViewModel.initiateSharedAction(Seek(it))
            }, Throwable::printStackTrace)
            .addTo(compositeDisposable)


        Observable.merge(
            listOf(
                playImage.clicks().map { Play },
                pauseImage.clicks().map { Pause },
                playNextImage.clicks().map { PlayNext },
                playPreviousImage.clicks().map { PlayPrevious },
                fastForwardImage.clicks().map { FastForward },
                fastBackwardImage.clicks().map { FastBackward },
            )
        )
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .subscribe({
                activityViewModel.initiateSharedAction(it)
            }, Throwable::printStackTrace)
            .addTo(compositeDisposable)


        closeImageView.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribe({
                findNavController().navigateUp()
            }, Throwable::printStackTrace)
            .addTo(compositeDisposable)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    companion object {
        private const val TAG = "PlayListFragment"
    }
}