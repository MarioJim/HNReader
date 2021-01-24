package org.team4.hnreader.ui.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.model.StoryWithURL
import org.team4.hnreader.databinding.FragmentStoryBinding
import org.team4.hnreader.ui.activities.CommentsActivity
import org.team4.hnreader.utils.DateTimeUtils
import org.team4.hnreader.utils.URLUtils

class StoryAdapter(private val dataSet: List<Story>) :
    RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {
    class StoryViewHolder(binding: FragmentStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvTitle = binding.tvTitle
        val tvInfo = binding.tvInfo
        val tvUrl = binding.tvUrl
        val tvVotes = binding.tvVotes
        var story: Story? = null

        init {
            binding.urlBtn.setOnClickListener {
                if (story is StoryWithURL) {
                    val url = (story as StoryWithURL).url
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(binding.root.context, browserIntent, null)
                }
            }
            binding.commentsBtn.setOnClickListener {
                val intentToComments =
                    Intent(binding.root.context, CommentsActivity::class.java).apply {
                        putExtra("story", story)
                    }
                startActivity(binding.root.context, intentToComments, null)
            }
            binding.shareBtn.setOnClickListener {
                val url = if (story is StoryWithURL) {
                    (story as StoryWithURL).url
                } else {
                    "https://news.ycombinator.com/item?id=${story?.id}"
                }
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Check this Hacker News post: $url")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(binding.root.context, shareIntent, null)
            }
            binding.saveBtn.setOnClickListener {
                Toast.makeText(binding.root.context, "Saved item in bookmarks", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): StoryViewHolder {
        val binding =
            FragmentStoryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: StoryViewHolder, position: Int) {
        viewHolder.tvTitle.text = dataSet[position].title
        if (dataSet[position] is StoryWithURL) {
            val url = (dataSet[position] as StoryWithURL).url
            viewHolder.tvUrl.text = URLUtils.getDomain(url)
        }
        val timeAgo = DateTimeUtils.timeAgo(dataSet[position].created_at)
        viewHolder.tvInfo.text = "by ${dataSet[position].author}, $timeAgo"
        viewHolder.tvVotes.text =
            "${dataSet[position].points} points, ${dataSet[position].numComments} comments"
        viewHolder.story = dataSet[position]
    }

    override fun getItemCount() = dataSet.size


}
