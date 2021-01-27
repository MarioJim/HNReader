package org.team4.hnreader.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.databinding.FragmentCommentBinding
import org.team4.hnreader.ui.fragments.CommentOptionsBottomSheet
import org.team4.hnreader.utils.DateTimeUtils
import org.team4.hnreader.utils.TextUtils

class CommentAdapter(
    private val activity: AppCompatActivity,
    private val comments: List<FlattenedComment>,
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    class CommentViewHolder(binding: FragmentCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val root = binding.root
        val tvCommentInfo = binding.tvCommentInfo
        val tvContent = binding.tvContent
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CommentViewHolder {
        val binding =
            FragmentCommentBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: CommentViewHolder, position: Int) {
        val timeAgo = DateTimeUtils.timeAgo(comments[position].created_at)
        viewHolder.tvCommentInfo.text =
            "${comments[position].author}, $timeAgo, depth ${comments[position].depth}"
        viewHolder.tvContent.text = TextUtils.fromHTML(comments[position].text)

        viewHolder.root.setOnLongClickListener {
            CommentOptionsBottomSheet(comments[position]).show(
                activity.supportFragmentManager,
                CommentOptionsBottomSheet.TAG,
            )
            true
        }
    }

    override fun getItemCount() = comments.size
}
