package org.team4.hnreader.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.team4.hnreader.data.model.Comment
import org.team4.hnreader.databinding.FragmentCommentBinding

class CommentAdapter(private val dataSet: List<Comment>): RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    class CommentViewHolder(binding: FragmentCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvCommentInfo: TextView = binding.tvCommentInfo
        val tvContent: TextView = binding.tvContent
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = FragmentCommentBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: CommentViewHolder, position: Int) {
        viewHolder.tvCommentInfo.text = "${dataSet[position].by}, ${dataSet[position].time}"
        viewHolder.tvContent.text = dataSet[position].text
    }

    override fun getItemCount() = dataSet.size
}