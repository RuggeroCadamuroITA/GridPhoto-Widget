package com.example.photowidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import java.io.File

open class BaseWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            val folder = File(context.filesDir, "cropped")
            val files = folder.listFiles()

            if (!files.isNullOrEmpty()) {
                val bitmap = BitmapFactory.decodeFile(files.random().absolutePath)
                views.setImageViewBitmap(R.id.widget_image, bitmap)
            }

            val intent = Intent(context, this::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(id))
            }
            val pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_image, pi)
            appWidgetManager.updateAppWidget(id, views)
        }
    }
}

class PhotoWidget1x1 : BaseWidgetProvider()
class PhotoWidget2x2 : BaseWidgetProvider()
class PhotoWidget1x2 : BaseWidgetProvider()