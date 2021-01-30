package org.team4.hnreader.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.team4.hnreader.data.model.DisplayedItem
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.databinding.FragmentFlattenedCommentBinding
import org.team4.hnreader.databinding.FragmentStoryBinding

class StoryDetailsAdapter(
    private val items: List<DisplayedItem>,
    private val showCommentMenuCallback: (comment: FlattenedComment) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemViewType(position: Int) = when (items[position]) {
        is Story -> STORY_VIEW_TYPE
        is FlattenedComment -> FLAT_COMMENT_VIEW_TYPE
        else -> throw Exception("ViewType for item ${items[position]} not implemented")
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            STORY_VIEW_TYPE -> {
                val binding = FragmentStoryBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false,
                )
                StoryViewHolder(binding)
            }
            FLAT_COMMENT_VIEW_TYPE -> {
                val binding = FragmentFlattenedCommentBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false,
                )
                FlattenedCommentViewHolder(binding) { showCommentMenuCallback(it) }
            }
            else -> throw Exception("ViewHolder for viewType $viewType not implemented")
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is StoryViewHolder) {
            viewHolder.bindTo(items[position] as Story)
        } else if (viewHolder is FlattenedCommentViewHolder) {
            viewHolder.bindTo(items[position] as FlattenedComment)
        }
    }

    override fun getItemCount() = items.size

    companion object {
        private const val STORY_VIEW_TYPE = 1
        private const val FLAT_COMMENT_VIEW_TYPE = 2
    }
}
