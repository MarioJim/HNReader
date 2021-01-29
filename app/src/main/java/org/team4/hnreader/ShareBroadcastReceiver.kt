package org.team4.hnreader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ShareBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val url = intent.dataString

        if (url != null) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check this Hacker News post: $url")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share URL")
            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context!!.startActivity(shareIntent)
        }
    }
}