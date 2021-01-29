package org.team4.hnreader.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.remote.ApiRequestQueue
import org.team4.hnreader.databinding.FragmentBestStoriesBinding
import org.team4.hnreader.ui.callbacks.StoryIdsSourceAndClickHandler

class BestStoriesFragment : Fragment(), StoryIdsSourceAndClickHandler {
    private var _binding: FragmentBestStoriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBestStoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun fetchStoryIds(
        responseCallback: (List<Int>) -> Unit,
        errorCallback: (Exception) -> Unit,
    ) {
        ApiRequestQueue.getInstance().fetchBestStoriesIds(responseCallback, errorCallback)
    }

    override fun openComments(story: Story) {
        val directions = BestStoriesFragmentDirections
            .actionBestStoriesFragmentToCommentsRecyclerViewFragment(story)
        findNavController().navigate(directions)
    }
}