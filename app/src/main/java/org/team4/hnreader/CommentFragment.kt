package org.team4.hnreader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

private const val COMMENT_USER = "username"
private const val COMMENT_CONTENT = "content"
private const val COMMENT_DATE = "date"

class CommentFragment : Fragment() {
    private var commentUser: String? = null
    private var commentContent: String? = null
    private var commentDate: Int? = 0

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
    ): View? {
        return inflater.inflate(R.layout.fragment_comment, container, false)
    }

    companion object {
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