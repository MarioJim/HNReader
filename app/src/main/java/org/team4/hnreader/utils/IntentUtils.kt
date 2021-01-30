package org.team4.hnreader.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import org.team4.hnreader.R
import org.team4.hnreader.ShareBroadcastReceiver

class IntentUtils {
    companion object {
        fun buildShareIntent(text: String): Intent = Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            },
            "Share URL"
        )

        fun generateCustomTabsIntentBuilder(context: Context) =
            CustomTabsIntent.Builder().apply {
                val shareIntent = Intent(
                    context,
                    ShareBroadcastReceiver::class.java,
                )
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    shareIntent,
                    0,
                )
                val shareIcon = BitmapFactory.decodeResource(
                    context.resources,
                    android.R.drawable.ic_menu_share,
                )
                setActionButton(shareIcon, "Share via...", pendingIntent)

                val params = CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(context.getColor(R.color.purple_500))
                    .build()
                setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, params)
            }
    }
}
