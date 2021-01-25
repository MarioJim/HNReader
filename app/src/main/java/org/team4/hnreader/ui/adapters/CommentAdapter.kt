package org.team4.hnreader.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.databinding.FragmentCommentBinding
import org.team4.hnreader.utils.DateTimeUtils

class CommentAdapter(private val dataSet: List<FlattenedComment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    class CommentViewHolder(binding: FragmentCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvCommentInfo = binding.tvCommentInfo
        val tvContent = binding.tvContent
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CommentViewHolder {
        val binding =
            FragmentCommentBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: CommentViewHolder, position: Int) {
        val timeAgo = DateTimeUtils.timeAgo(dataSet[position].created_at)
        viewHolder.tvCommentInfo.text = "${dataSet[position].author}, $timeAgo, depth ${dataSet[position].depth}"
        viewHolder.tvContent.text = dataSet[position].text
    }

    override fun getItemCount() = dataSet.size
}
