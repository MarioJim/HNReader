package org.team4.hnreader.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.databinding.FragmentStoryBinding

class StoryAdapter(
    private val openCommentsRVCallback: (story: Story) -> Unit,
) : ListAdapter<Story, StoryViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = FragmentStoryBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false,
        )
        return StoryViewHolder(binding, false, openCommentsRVCallback)
    }

    override fun onBindViewHolder(viewHolder: StoryViewHolder, position: Int) =
        viewHolder.bindTo(getItem(position))

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
