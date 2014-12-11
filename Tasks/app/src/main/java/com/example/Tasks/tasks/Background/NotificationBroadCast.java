package com.example.Tasks.tasks.Background;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.*;
import android.util.Log;

import com.example.Tasks.R;
import com.example.Tasks.tasks.Database.GroupDBAdapter;
import com.example.Tasks.tasks.TaskActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationBroadCast extends IntentService {
    private Context context;

    public NotificationBroadCast(Context context) {
        super("hfkjhfjksd");
        this.context = context;
    }

    public NotificationBroadCast() {
        super("hfsjkdfds");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        GroupDBAdapter db = new GroupDBAdapter(context);
        db.open();
        Cursor c = db.getAllTasks();  // get all tasks in database
        Date thisDate = new Date();  // get today's date
        SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy");

        byte code = 0;  // code represents notification
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {  // search through the query
            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaa");
            String currentDate = c.getString(c.getColumnIndex("due_date"));
            String priority = c.getString(c.getColumnIndex("priority"));
            int stt = c.getInt(c.getColumnIndex("completed"));
            // show notification of tasks that have the due date as today, high priority and not completed
            if (currentDate.equals(f.format(thisDate)) && priority.equals("1 - HIGH") &&
                    stt == 0) {
                String taskName = c.getString(c.getColumnIndex("title"));
                String note = c.getString(c.getColumnIndex("note"));
                String gID = c.getString(c.getColumnIndex("groupID"));
                db.setChosenGroupID(gID);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setSmallIcon(R.drawable.notifilogo)
                        .setContentTitle(taskName)  // task name
                        .setContentText(note)  // note
                        .setContentInfo("The due date is today")
                        // open the group this task belongs to
                        .setContentIntent(PendingIntent.getActivity(context, code,
                                new Intent(context, TaskActivity.class), PendingIntent.FLAG_ONE_SHOT));

                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                        .notify(code, builder.build());
                code++;  // increment code to display another notification if there is more than 1 task
            }
        }
    }
}
