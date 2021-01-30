package org.team4.hnreader.ui.destinations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.remote.ApiRequestQueue
import org.team4.hnreader.databinding.FragmentAskHnBinding
import org.team4.hnreader.ui.callbacks.StoryRecyclerViewCallbacks
import org.team4.hnreader.ui.destinations.AskHNFragmentDirections

class AskHNFragment : Fragment(), StoryRecyclerViewCallbacks {
    private var _binding: FragmentAskHnBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAskHnBinding.inflate(inflater, container, false)
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
        ApiRequestQueue.getInstance().fetchAskStoriesIds(responseCallback, errorCallback)
    }

    override fun openStoryDetails(story: Story) {
        val directions = AskHNFragmentDirections.actionAskHNFragmentToStoryDetailsFragment(story)
        findNavController().navigate(directions)
    }
}
