package org.team4.hnreader.ui.destinations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.remote.FirestoreHelper
import org.team4.hnreader.databinding.FragmentBookmarksStoriesBinding
import org.team4.hnreader.ui.callbacks.StoryRecyclerViewCallbacks
import org.team4.hnreader.ui.fragments.BookmarksStoriesFragmentDirections

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
        errorCallback: (Exception) -> Unit,
    ) {
        FirestoreHelper.getInstance().getStoriesFromBookmarks {
            responseCallback(it)
        }
    }

    override fun openStoryDetails(story: Story) {
        val directions = BookmarksStoriesFragmentDirections
            .actionBookmarksStoriesFragmentToStoryDetailsFragment(story)
        findNavController().navigate(directions)
    }
}
