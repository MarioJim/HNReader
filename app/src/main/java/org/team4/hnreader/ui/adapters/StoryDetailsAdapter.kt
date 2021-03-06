package org.team4.hnreader.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.team4.hnreader.data.model.DisplayedItem
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.databinding.FragmentFlattenedCommentBinding
import org.team4.hnreader.databinding.FragmentStoryBinding

class StoryDetailsAdapter(
    private val storyViewHolderLoadedCallback: () -> Unit,
    private val showCommentMenuCallback: (comment: FlattenedComment) -> Unit,
) : ListAdapter<DisplayedItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is Story -> STORY_VIEW_TYPE
        is FlattenedComment -> FLAT_COMMENT_VIEW_TYPE
        else -> throw Exception("ViewType for item ${getItem(position)} not implemented")
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) = when (viewType) {
        STORY_VIEW_TYPE ->
            StoryViewHolder(FragmentStoryBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false,
            ))
        FLAT_COMMENT_VIEW_TYPE ->
            FlattenedCommentViewHolder(FragmentFlattenedCommentBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false,
            ))
        else -> throw Exception("ViewHolder for viewType $viewType not implemented")
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is StoryViewHolder -> {
                viewHolder.detailsBindTo(getItem(position) as Story)
                storyViewHolderLoadedCallback()
            }
            is FlattenedCommentViewHolder -> viewHolder.bindTo(getItem(position) as FlattenedComment) {
                showCommentMenuCallback(it)
            }
        }
    }

    fun clearAndSetStory(story: Story, callback: Runnable) = submitList(listOf(story), callback)

    fun extendList(items: List<DisplayedItem>, callback: Runnable) =
        submitList(currentList + items, callback)

    companion object {
        private const val STORY_VIEW_TYPE = 1
        private const val FLAT_COMMENT_VIEW_TYPE = 2

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DisplayedItem>() {
            override fun areItemsTheSame(oldItem: DisplayedItem, newItem: DisplayedItem) =
                oldItem.getItemId() == newItem.getItemId()

            override fun areContentsTheSame(oldItem: DisplayedItem, newItem: DisplayedItem) =
                oldItem.equalsDisplayedItem(newItem)
        }
    }
}
