package com.example.Tasks.tasks.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.Tasks.R;
import com.example.Tasks.tasks.Database.GroupDBAdapter;

import java.util.ArrayList;

public class TaskViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private int appWidgetId;
    private ArrayList<String> title = new ArrayList<String>();  // store titles
    private ArrayList<String> date = new ArrayList<String>();  // store due_date

    public TaskViewFactory(Context c, Intent i) {
        context = c;
        appWidgetId = i.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        GroupDBAdapter db = new GroupDBAdapter(context);
        db.open();
        Cursor cursor = db.getAllTasksWithLimit();  // get incomplete tasks from DB
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            title.add(cursor.getString(cursor.getColumnIndex("title")));
            date.add(cursor.getString(cursor.getColumnIndex("due_date")));
        }

    }

    @Override
    public void onCreate() {}

    @Override
    public void onDataSetChanged() {}

    @Override
    public void onDestroy() {}

    @Override
    public int getCount() {
        return title.size();
    }  // return the size of array list

    @Override
    public RemoteViews getViewAt(int i) {  /* this method will go through the List and show each task
                                              in the listview */
        RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.widget_row);
        row.setTextViewText(R.id.rowTitle, title.get(i));
        row.setTextViewText(R.id.rowDate, date.get(i));
        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
