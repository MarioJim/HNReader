package org.team4.hnreader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.team4.hnreader.utils.IntentUtils

class ShareBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val url = intent.dataString

        if (url != null) {
            val text = "Check this Hacker News post: $url"
            val shareIntent = IntentUtils.buildShareIntent(text)
            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context!!.startActivity(shareIntent)
        }
    }
}
