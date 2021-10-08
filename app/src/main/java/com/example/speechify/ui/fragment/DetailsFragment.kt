package com.example.speechify.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.speechify.R
import com.example.speechify.ui.player.MainActivityViewModel
import kotlinx.android.synthetic.main.fragment_details.*


class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activityViewModel.currentSongDetails.observe(viewLifecycleOwner) {
            titleTv.text = it.title
            detailsTv.text = it.details
            songImage.setImageResource(it.imageFile)
        }
    }

    companion object {
        fun newInstance(songId: Int): DetailsFragment {
            return DetailsFragment().apply {
                arguments = Bundle().apply { putInt("songId", songId) }
            }
        }
    }
}