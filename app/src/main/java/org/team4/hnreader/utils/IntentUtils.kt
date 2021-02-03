package org.team4.hnreader.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.graphics.drawable.toBitmap
import org.team4.hnreader.R
import org.team4.hnreader.ShareBroadcastReceiver


object IntentUtils {
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

            val shareIcon = getDrawable(context, R.drawable.ic_share)!!.toBitmap()
            setActionButton(shareIcon, "Share via...", pendingIntent)

            val primaryColorValue = TypedValue()
            context.theme.resolveAttribute(R.attr.colorPrimary, primaryColorValue, true)
            val params = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(primaryColorValue.data)
                .build()
            setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, params)
        }
}
