package com.example.speechify.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.speechify.R
import com.example.speechify.ui.SongActivity.Companion.player
import com.example.speechify.ui.player.MainActivityViewModel
import com.example.speechify.ui.player.lyrics.lyricsParser.Lyrics
import com.example.speechify.ui.player.lyrics.lyricsParser.LyricsHelper
import com.example.speechify.ui.player.lyrics.lyricsParser.LyricsView
import kotlinx.android.synthetic.main.fragment_lyrics.*


class LyricsFragment : Fragment() {

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    var mLrcView: LyricsView? = null

    companion object {
        fun newInstance(songId: Int): LyricsFragment {
            return LyricsFragment().apply {
                arguments = Bundle().apply { putInt("songId", songId) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(
            R.layout.fragment_lyrics, container,
            false
        )
        mLrcView = view.findViewById(R.id.lrc_view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activityViewModel.getPlayerProgress().observe(viewLifecycleOwner) {
            mLrcView?.updateTime(it.currentPosition) }


        activityViewModel.currentSongDetails.observe(viewLifecycleOwner) {
            titleName.text = it.title
            detailsTitle.text = it.details
            val lyricsName = it.fileName
            val lyrics =
                activity?.let { LyricsHelper.parseLrcFromAssets(it, "$lyricsName.lrc") }
            mLrcView?.setLrcData(lyrics as MutableList<Lyrics>?)
            mLrcView?.setOnPlayIndicatorLineListener(onPlayIndicatorLineListener = object :
                LyricsView.OnPlayIndicatorLineListener {
                override fun onPlay(time: Long, content: String?) {
                    player.seekTo(time)
                }
            })
        }
    }

}