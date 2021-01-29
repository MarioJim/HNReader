package org.team4.hnreader.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.data.remote.FirestoreHelper
import org.team4.hnreader.databinding.FragmentCommentOptionsBottomSheetBinding

class CommentOptionsBottomSheet(private val comment: FlattenedComment) :
    BottomSheetDialogFragment() {
    private var _binding: FragmentCommentOptionsBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCommentOptionsBottomSheetBinding.inflate(inflater, container, false)

        binding.btnBookmarkComment.setOnClickListener {
            FirestoreHelper.getInstance().addCommentToBookmarks(comment) {
                Toast.makeText(binding.root.context, it.second, Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnShareComment.setOnClickListener {
            val url = "https://news.ycombinator.com/item?id=${comment.id}"
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check this Hacker News comment: $url")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        return binding.root
    }

    companion object {
        const val TAG = "CommentOptionsBottomSheet"
    }
}
