package com.example.Tasks.tasks.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.Tasks.R;
import com.example.Tasks.tasks.Adding.AddingTask;

public class TasksWidgets extends AppWidgetProvider {
    @Override
    public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds ) {
        for (byte i = 0; i < appWidgetIds.length; i++) {  // update all current widgets
            PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, AddingTask.class), 0);
            RemoteViews v = new RemoteViews(context.getPackageName(), R.layout.widget_component);
            // go to AddingTask activity when clicking "add" icon
            v.setOnClickPendingIntent(R.id.addTaskWidget, pi);

            // display incomplete tasks and sort by due date
            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            v.setRemoteAdapter(appWidgetIds[i], R.id.widgetList, intent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], v);  // update widget
        }
    }
}