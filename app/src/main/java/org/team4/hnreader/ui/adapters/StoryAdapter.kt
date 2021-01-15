package org.team4.hnreader.ui.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.databinding.FragmentPostBinding
import org.team4.hnreader.ui.activities.CommentsActivity
import java.net.URI

class StoryAdapter(private val dataSet: List<Story>) :
    RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {
    class StoryViewHolder(binding: FragmentPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvTitle = binding.tvTitle
        val tvInfo = binding.tvInfo
        val tvUrl = binding.tvUrl
        val tvVotes = binding.tvVotes
        var story: Story? = null

        init {
            binding.urlBtn.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(story?.url))
                startActivity(binding.root.context, browserIntent, null)
            }
            binding.commentsBtn.setOnClickListener {
                val intentToComments = Intent(binding.root.context, CommentsActivity::class.java).apply {
                    putExtra("story", story)
                }
                startActivity(binding.root.context, intentToComments, null)
            }
            binding.shareBtn.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Check this Hacker News post: ${story?.url}")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(binding.root.context, shareIntent, null)
            }
            binding.saveBtn.setOnClickListener {
                Toast.makeText(binding.root.context, "Saved item in bookmarks", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): StoryViewHolder {
        val binding =
            FragmentPostBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: StoryViewHolder, position: Int) {
        var domain = URI(dataSet[position].url).host
        domain = if (domain.startsWith("www.")) domain.substring(4) else domain

        viewHolder.tvTitle.text = dataSet[position].title
        viewHolder.tvUrl.text = domain
        viewHolder.tvInfo.text = "by ${dataSet[position].by}, 3 hours ago"
        viewHolder.tvVotes.text = "${dataSet[position].score} points, ${dataSet[position].numComments} comments"
        viewHolder.story = dataSet[position]
    }

    override fun getItemCount() = dataSet.size
}
