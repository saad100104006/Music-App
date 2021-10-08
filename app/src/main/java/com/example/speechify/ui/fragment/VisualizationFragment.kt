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
import com.example.speechify.ui.SongActivity
import com.example.speechify.ui.player.MainActivityViewModel
import com.gauravk.audiovisualizer.visualizer.BlastVisualizer
import kotlinx.android.synthetic.main.fragment_visualization.*


class VisualizationFragment : Fragment() {

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    lateinit var mVisualizer: BlastVisualizer

    companion object {
        fun newInstance(songId: Int): VisualizationFragment {
            return VisualizationFragment().apply {
                arguments = Bundle().apply { putInt("songId", songId) }
            }
        }

        private const val TAG = "DeliveryDetailsFragment"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(
                R.layout.fragment_visualization, container,
                false
        )
        mVisualizer = view.findViewById(R.id.blast);
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activityViewModel.currentSongDetails.observe(viewLifecycleOwner) {
            visulaizeTitle.text = it.title
            visalizeDetails.text = it.details
            try {
                val audioSessionId: Int = SongActivity.player.audioSessionId
                if (audioSessionId != -1) mVisualizer.setAudioSessionId(audioSessionId)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mVisualizer.release()
    }
}