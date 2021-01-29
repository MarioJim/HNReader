package org.team4.hnreader.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val firestoreHelper = FirestoreHelper.getInstance()
        firestoreHelper.checkIfCommentIsBookmark(comment) { exist ->
            if (exist) {
                binding.btnBookmarkComment.text = "Remove from bookmarks"
            } else {
                binding.btnBookmarkComment.text = "Add to bookmarks"
            }
        }

        binding.btnBookmarkComment.setOnClickListener {
            firestoreHelper.checkIfCommentIsBookmark(comment) { exist ->
                if (exist) {
                    firestoreHelper.removeCommentFromBookmarks(comment) {
                        // TODO: Handle result
                    }
                    binding.btnBookmarkComment.text = "Add to bookmarks"
                } else {
                    firestoreHelper.addCommentToBookmarks(comment) {
                        // TODO: Handle result
                    }
                    binding.btnBookmarkComment.text = "Remove from bookmarks"
                }
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
