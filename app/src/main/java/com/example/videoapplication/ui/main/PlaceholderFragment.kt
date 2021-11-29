package com.example.videoapplication.ui.main

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.example.videoapplication.databinding.FragmentMainBinding
import com.example.videoapplication.model.Results
import com.example.videoapplication.ui.model.SharedViewModel
import com.example.videoapplication.util.Status
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val sharedViewModel by sharedViewModel<SharedViewModel>()
    private val pageViewModel by viewModel<PageViewModel>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root = binding.root

        binding.apply {
            swipeRefreshLayout.setOnRefreshListener {
                sharedViewModel.getUrls()
            }
            //Observe the results of getUrl request
            sharedViewModel.resultsModel.observe(viewLifecycleOwner, {
                when (it.status) {
                    Status.SUCCESS -> {
                        //downLoadVideo if successful
                        downloadVideo(it.data.results)
                        swipeRefreshLayout.isRefreshing = false
                    }
                    Status.LOADING -> {
                        swipeRefreshLayout.isRefreshing = true
                    }
                    Status.ERROR -> {
                        showError(it.messageId)
                    }
                }
            })

            //If video downloaded or already exists in the directory show it.
            pageViewModel.filePath.observe(viewLifecycleOwner, {
                when (it.status) {
                    Status.SUCCESS -> {
                        showVideo(it.data)
                        swipeRefreshLayout.isRefreshing = false
                    }
                    Status.LOADING -> {
                        swipeRefreshLayout.isRefreshing = true
                    }
                    Status.ERROR -> {
                        showError(it.messageId)
                    }
                }
            })
        }

        return root
    }

    private fun showError(@StringRes messageId: Int) {
        binding.apply {
            errorText.text = getString(messageId)
            errorText.visibility = View.VISIBLE
            videoView.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showVideo(url: String) {
        binding.apply {
            val mediaController = MediaController(requireContext())
            mediaController.setAnchorView(videoView)
            //specify the location of media file
            val uri: Uri = Uri.parse(url)
            //Setting MediaController and URI, then starting the videoView
            videoView.setMediaController(mediaController)
            videoView.setVideoURI(uri)
            videoView.visibility = View.VISIBLE
//            videoView.start()
        }
    }


    private fun downloadVideo(results: Results) {
        var videoUrl = ""
        when (arguments?.getInt(ARG_SECTION_NUMBER)) {
            1 -> videoUrl = results.src
            2 -> videoUrl = results.single
            3 -> videoUrl = results.split_v
            4 -> videoUrl = results.split_h
        }
        binding.sectionLabel.text = videoUrl
        pageViewModel.getVideo(videoUrl)
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.videoView.start()
    }

    override fun onResume() {
        super.onResume()
        binding.videoView.requestFocus()
        binding.videoView.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.videoView.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.videoView.stopPlayback()
        _binding = null
    }
}