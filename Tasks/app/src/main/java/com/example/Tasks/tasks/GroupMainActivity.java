package com.example.Tasks.tasks;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.example.Tasks.R;
import com.example.Tasks.tasks.Adding.AddingGroup;
import com.example.Tasks.tasks.Adding.AddingTask;
import com.example.Tasks.tasks.Background.NotificationBroadCast;
import com.example.Tasks.tasks.Database.GroupDBAdapter;
import com.example.Tasks.tasks.Editing.EditingGroup;

import java.util.Calendar;

public class GroupMainActivity extends ListActivity {
    private GroupDBAdapter gDB;
    private Cursor c;
    private static byte sortType;  // key value for sorting types

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main);
        /*setAlarm();*/
        gDB = new GroupDBAdapter(this);
        gDB.open();

        if (sortType == 0) {
            getAllData("Default");
        } else {
            getAllData("Alphabetical");
        }

        ListView lv = (ListView) findViewById(android.R.id.list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) getListAdapter().getItem(position);  // get the selected group
                String groupId = cursor.getString(cursor.getColumnIndexOrThrow("_id"));  // get the id
                gDB.setChosenGroupID(groupId);  // set the id
                startActionMode(callback);  // display contextual bar when clicking
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {  /* display action bar */
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_group, m);
        return super.onCreateOptionsMenu(m);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  /* action for action bar's buttons */
        switch (item.getItemId()) {
            case R.id.add_new_group:  // add group
                Intent toAddingGroup = new Intent(this, AddingGroup.class);
                startActivity(toAddingGroup);
                return true;
            case R.id.add_new_task:  // add task
                Intent toAddingTask = new Intent(this, AddingTask.class);
                startActivity(toAddingTask);
                return true;
            case R.id.byAlphabet:  // sort by alphabetical order
                sortType = 1;
                finish();
                startActivity(getIntent());
                return true;
            case R.id.byDefault:  // sort by default
                sortType = 0;
                finish();
                startActivity(getIntent());
                return true;
            default:
                return false;
        }
    }

    private void getAllData(String sort) {  /* get all data */
        c = gDB.getAllGroups(sort);
        startManagingCursor(c);
        String[] from = new String[] {gDB.getKeyTitle()};  // array to store the group titles
        int[] to = new int[] {R.id.row};  // display them in TextView format
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.group_row, c, from, to);
        setListAdapter(adapter);  // add to the ListView
    }

    private ActionMode.Callback callback = new ActionMode.Callback() {  /* contextual action bar */
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.list_view, menu);  // create menu items
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
            switch (item.getItemId()) {  // set action for menu items
                case R.id.editItem:  // edit group
                    Intent toEditingGroup = new Intent(GroupMainActivity.this, EditingGroup.class);
                    startActivity(toEditingGroup);
                    return true;
                case R.id.deleteItem:  // delete selected group
                    gDB.deleteGroup();
                    gDB.deleteTasksOfGroup();
                    finish();
                    startActivity(getIntent());  // refresh the activity
                    return true;
                case R.id.viewTasks:  // view tasks of a group
                    Intent toGroupTask = new Intent(GroupMainActivity.this, TaskActivity.class);
                    startActivity(toGroupTask);
                default: return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {}
    };

    public void setAlarm() {  /* activate background notification */
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int type = AlarmManager.ELAPSED_REALTIME;  // wake up device when notification comes
        long time = AlarmManager.INTERVAL_FIFTEEN_MINUTES;  // repeat every half of a day
        Intent i = new Intent(this, NotificationBroadCast.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.setInexactRepeating(type, time, time, pi);  // fire notification
    }
}
