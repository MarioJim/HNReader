package org.team4.hnreader.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.team4.hnreader.databinding.FragmentCommentBinding
import org.team4.hnreader.utils.DateTimeUtils

class CommentFragment : Fragment() {
    private var _binding: FragmentCommentBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private var commentUser: String? = "User"
    private var commentContent: String? =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin laoreet diam eget tellus commodo imperdiet. In vitae augue auctor magna consequat pharetra. Quisque posuere sed neque vitae tempor. Morbi lectus orci, efficitur et libero nec, vestibulum tempor urna."
    private var commentDate: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            commentUser = it.getString(COMMENT_USER)
            commentContent = it.getString(COMMENT_CONTENT)
            commentDate = it.getInt(COMMENT_DATE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentBinding.inflate(inflater, container, false)

        val timeAgo = DateTimeUtils.timeAgo(commentDate)
        binding.tvCommentInfo.text = "$commentUser, $timeAgo"
        binding.tvContent.text = commentContent

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val COMMENT_USER = "username"
        private const val COMMENT_CONTENT = "content"
        private const val COMMENT_DATE = "date"

        @JvmStatic
        fun newInstance(user: String, content: String, date: Int) =
            CommentFragment().apply {
                arguments = Bundle().apply {
                    putString(COMMENT_USER, user)
                    putString(COMMENT_CONTENT, content)
                    putInt(COMMENT_DATE, date)
                }
            }
    }
}
