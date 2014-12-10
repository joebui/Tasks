package com.example.Tasks.tasks;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.Tasks.R;

public class CustomAdapter extends SimpleCursorAdapter {
    private int layout;

    public CustomAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
        this.layout = layout;
    }

    @Override
    public View newView(final Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout, parent, false);
        TextView title = (TextView) view.findViewById(R.id.rowTitle);
        TextView date = (TextView) view.findViewById(R.id.rowDate);

        Cursor c = getCursor();  // get the query's result
        int completed = c.getInt(c.getColumnIndex("completed"));  // get completed stt
        String stt = c.getString(c.getColumnIndex("priority"));
        if (completed == 1) {  // cross out the title if the task is completed
            if (stt.equals("1 - HIGH")) {  // set the title red if it is high priority task
                title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                title.setTextColor(Color.YELLOW);
                title.setText(c.getString(c.getColumnIndex("title")));
                date.setText(c.getString(c.getColumnIndex("due_date")));
            } else {
                title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                title.setText(c.getString(c.getColumnIndex("title")));
                date.setText(c.getString(c.getColumnIndex("due_date")));
            }
        } else {
            if (stt.equals("1 - HIGH")) {
                title.setTextColor(Color.YELLOW);
                title.setText(c.getString(c.getColumnIndex("title")));
                date.setText(c.getString(c.getColumnIndex("due_date")));
            } else {
                title.setText(c.getString(c.getColumnIndex("title")));
                date.setText(c.getString(c.getColumnIndex("due_date")));
            }
        }

        return view;
    }
}
