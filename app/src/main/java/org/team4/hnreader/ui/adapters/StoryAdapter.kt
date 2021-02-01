package org.team4.hnreader.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.databinding.FragmentStoryBinding
import org.team4.hnreader.ui.callbacks.OpenStoryDetails

class StoryAdapter(private val openStoryDetails: OpenStoryDetails) :
    ListAdapter<Story, StoryViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) =
        StoryViewHolder(FragmentStoryBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false,
        ))

    override fun onBindViewHolder(viewHolder: StoryViewHolder, position: Int) =
        viewHolder.listBindTo(getItem(position), openStoryDetails)

    fun resetList(callback: Runnable) = submitList(emptyList(), callback)

    fun extendList(stories: List<Story>, callback: Runnable) =
        submitList(currentList + stories, callback)

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Story, newItem: Story) =
                oldItem.equalsDisplayedItem(newItem)
        }
    }
}
