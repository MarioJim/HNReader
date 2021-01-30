package org.team4.hnreader.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.databinding.FragmentStoryBinding

class StoryAdapter(
    private val stories: List<Story>,
    private val openCommentsRVCallback: (story: Story) -> Unit,
) : RecyclerView.Adapter<StoryViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = FragmentStoryBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false,
        )
        return StoryViewHolder(binding, openCommentsRVCallback)
    }

    override fun onBindViewHolder(viewHolder: StoryViewHolder, position: Int) =
        viewHolder.bindTo(stories[position])

    override fun getItemCount() = stories.size
}
