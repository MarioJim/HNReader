package org.team4.hnreader.ui.callbacks

import org.team4.hnreader.data.model.Story
import org.team4.hnreader.databinding.FragmentStoryBinding

interface StoryRecyclerViewCallbacks {
    fun fetchStoryIds(responseCallback: (List<Int>) -> Unit, errorCallback: (Exception) -> Unit)
    fun openStoryDetails(story: Story, binding: FragmentStoryBinding)
}
