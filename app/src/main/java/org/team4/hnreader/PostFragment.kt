package org.team4.hnreader

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import kotlinx.android.synthetic.main.fragment_post.*;

private const val POST_TITLE = "Título de post"
private const val POST_URL = "news.ycombinator.com"
private const val POST_USER = "Usuario"

class PostFragment : Fragment() {
    private var postTitle: String? = null
    private var postURL: String? = null
    private var postUser: String? = null
    private var postVotes: Int? = null
    private var postNumComments: Int? = null
    private var postDate: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postTitle = it.getString(POST_TITLE)
            postURL = it.getString(POST_URL)
            postUser = it.getString(POST_USER)
            postVotes = 0
            postNumComments = 0
            postDate = 0
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                PostFragment().apply {
                    arguments = Bundle().apply {
                        putString(POST_TITLE, param1)
                        putString(POST_URL, param2)
                    }
                }
    }
}