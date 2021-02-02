package org.team4.hnreader.ui.destinations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.remote.FirestoreHelper
import org.team4.hnreader.databinding.FragmentBookmarksStoriesBinding
import org.team4.hnreader.databinding.FragmentStoryBinding
import org.team4.hnreader.ui.callbacks.StoryRecyclerViewCallbacks

class BookmarksStoriesFragment : Fragment(), StoryRecyclerViewCallbacks {
    private var _binding: FragmentBookmarksStoriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBookmarksStoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun fetchStoryIds(
        responseCallback: (List<Int>) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        FirestoreHelper.getInstance().getStoriesFromBookmarks ({
            responseCallback(it)
        }, {
            errorCallback(it)
        })
    }

    override fun openStoryDetails(story: Story, binding: FragmentStoryBinding) {
        val directions = BookmarksStoriesFragmentDirections
            .actionBookmarksStoriesFragmentToStoryDetailsFragment(story)
        val extras = FragmentNavigatorExtras(
            binding.storyFragmentContainer to "container_${story.id}",
        )
        findNavController().navigate(directions, extras)
    }
}
