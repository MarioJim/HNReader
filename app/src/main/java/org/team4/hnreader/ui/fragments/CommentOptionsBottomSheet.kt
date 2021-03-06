package org.team4.hnreader.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import org.team4.hnreader.R
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.data.remote.FirestoreHelper
import org.team4.hnreader.databinding.FragmentCommentOptionsBottomSheetBinding
import org.team4.hnreader.utils.IntentUtils
import org.team4.hnreader.utils.TextUtils

class CommentOptionsBottomSheet(
    private val comment: FlattenedComment,
    private val clipboardManager: ClipboardManager,
) : BottomSheetDialogFragment() {
    private var _binding: FragmentCommentOptionsBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCommentOptionsBottomSheetBinding.inflate(inflater, container, false)

        val firestoreHelper = FirestoreHelper.getInstance()
        firestoreHelper.checkIfCommentIsBookmark(comment, {
            if (it)
                setBookmarkButtonToRemove()
            else
                setBookmarkButtonToAdd()
        }, {
            toastMessage(it.message.toString())
        })

        if (FirebaseAuth.getInstance().currentUser == null) {
            binding.btnBookmarkComment.visibility = View.GONE
        }

        binding.btnBookmarkComment.setOnClickListener {
            firestoreHelper.checkIfCommentIsBookmark(comment, { check ->
                if (check) {
                    firestoreHelper.removeCommentFromBookmarks(comment, {
                        setBookmarkButtonToAdd()
                        dismiss()
                    }, {
                        toastMessage(it.message.toString())
                        dismiss()
                    })
                } else {
                    firestoreHelper.addCommentToBookmarks(comment, {
                        setBookmarkButtonToRemove()
                        dismiss()
                    }, {
                        toastMessage(it.message.toString())
                        dismiss()
                    })
                }
            }, {
                toastMessage(it.message.toString())
                dismiss()
            })
        }

        binding.btnCopyCommentText.setOnClickListener {
            val text = TextUtils.fromHTML(comment.text)
            val clip = ClipData.newPlainText("Hacker News comment", text)
            clipboardManager.setPrimaryClip(clip)
            dismiss()
        }

        binding.btnShareComment.setOnClickListener {
            val text = "Check this Hacker News comment: ${comment.getUrl()}"
            val shareIntent = IntentUtils.buildShareIntent(text)
            startActivity(shareIntent)
            dismiss()
        }

        return binding.root
    }

    private fun setBookmarkButtonToAdd() {
        binding.btnBookmarkComment.text = getString(R.string.add_bookmark)
        binding.btnBookmarkComment.icon = ResourcesCompat.getDrawable(
            binding.root.context.resources,
            R.drawable.ic_star_outline,
            binding.root.context.theme,
        )
    }

    private fun setBookmarkButtonToRemove() {
        binding.btnBookmarkComment.text = getString(R.string.remove_bookmark)
        binding.btnBookmarkComment.icon = ResourcesCompat.getDrawable(
            binding.root.context.resources,
            R.drawable.ic_star_filled,
            binding.root.context.theme,
        )
    }

    private fun toastMessage(message: String) {
        Toast.makeText(binding.root.context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "CommentOptionsBottomSheet"
    }
}
