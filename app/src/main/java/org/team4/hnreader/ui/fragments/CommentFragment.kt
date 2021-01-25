package org.team4.hnreader.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.databinding.FragmentCommentBinding
import org.team4.hnreader.utils.DateTimeUtils

class CommentFragment : Fragment() {
    private var _binding: FragmentCommentBinding? = null
    private val binding get() = _binding!!

    private var comment: FlattenedComment = FlattenedComment(
        "User",
        1610744647,
        0,
        -1,
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin laoreet diam eget tellus commodo imperdiet. In vitae augue auctor magna consequat pharetra. Quisque posuere sed neque vitae tempor. Morbi lectus orci, efficitur et libero nec, vestibulum tempor urna."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            comment = it.getSerializable(ARG_COMMENT) as FlattenedComment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCommentBinding.inflate(inflater, container, false)

        val timeAgo = DateTimeUtils.timeAgo(comment.created_at)
        binding.tvCommentInfo.text = "${comment.author}, $timeAgo"
        binding.tvContent.text = comment.text

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_COMMENT = "comment"

        @JvmStatic
        fun newInstance(comment: FlattenedComment) =
            CommentFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_COMMENT, comment)
                }
            }
    }
}
