package com.example.speechify.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.speechify.*
import com.example.speechify.data.FragmentWithTitle
import com.example.speechify.ktx.toMinSec
import com.example.speechify.ktx.visibleIfOrInvisible
import com.example.speechify.ui.*
import com.example.speechify.ui.adapter.PlayerViewPagerAdapter
import com.example.speechify.ui.player.*
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.userChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import kotlinx.android.synthetic.main.fragment_player.*
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class PlayerFragment : Fragment(R.layout.fragment_player) {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val activityViewModel: MainActivityViewModel by activityViewModels()


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val fragmentList = listOf(
            FragmentWithTitle(DetailsFragment.newInstance(1), "Details"),
            FragmentWithTitle(LyricsFragment.newInstance(1), "Lyrics"),
            FragmentWithTitle(VisualizationFragment.newInstance(1), "Visualization"),
        )

        val viewPagerAdapter = PlayerViewPagerAdapter(this)
        viewPagerAdapter.addFragments(fragmentList.map { it.fragment })
        viewPager.adapter = viewPagerAdapter

        myTab.setOnTabClickListener {
            viewPager.currentItem = it
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                myTab.setSelectedTab(position)
            }
        })

        activityViewModel.getIsPlaying().observe(viewLifecycleOwner) {
            playImage.visibleIfOrInvisible(!it)
            pauseImage.visibleIfOrInvisible(it)
        }

        activityViewModel.getPlayerProgress().observe(viewLifecycleOwner) {
            seekBar.progress = it.progress
            currentProgressTimeTv.text = it.currentPosition.toMinSec()
            totalTimeTv.text = it.duration.toMinSec()
        }

        activityViewModel.nextSongDetails.observe(viewLifecycleOwner) {
            if (it == null)
                return@observe

            nextTv.text = "Up Next: ${it.title}"
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

        nextTvContainer.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribe({
                findNavController().navigate(PlayerFragmentDirections.actionPlayerFragmentToPlayListFragment())
            }, Throwable::printStackTrace)
            .addTo(compositeDisposable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }
}