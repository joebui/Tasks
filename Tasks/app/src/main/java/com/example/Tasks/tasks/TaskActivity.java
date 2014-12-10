package com.example.Tasks.tasks;

import android.app.*;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.Tasks.R;
import com.example.Tasks.tasks.Adding.AddingGroup;
import com.example.Tasks.tasks.Adding.AddingTask;
import com.example.Tasks.tasks.Database.GroupDBAdapter;
import com.example.Tasks.tasks.Editing.EditingTask;

public class TaskActivity extends ListActivity {
    private GroupDBAdapter gDB;
    private Cursor c;
    private static byte sorting;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.task_listview);
        gDB = new GroupDBAdapter(this);
        gDB.open();
        c = gDB.getGroupDetails();
        c.moveToFirst();
        setTitle(c.getString(c.getColumnIndex("title")));  // set group name to the title

        displayTasks();
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                c = (Cursor) getListAdapter().getItem(position);
                String taskID = c.getString(c.getColumnIndex("_id"));
                gDB.setChosenTaskID(taskID);  // set id
                startActionMode(callback);  // display contextual menu
            }
        });
    }

    @Override
    public void onResume() {  /* display tasks again when resuming the activity */
        super.onResume();
        displayTasks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {  /* display action bar */
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_task, m);
        return super.onCreateOptionsMenu(m);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  /* apply action to action bar's buttons */
        switch (item.getItemId()) {
            case R.id.add_new_group:  // add group
                Intent toAddingGroup = new Intent(this, AddingGroup.class);
                startActivity(toAddingGroup);
                return true;
            case R.id.add_new_task:  // add task
                Intent toAddingTask = new Intent(this, AddingTask.class);
                startActivity(toAddingTask);
                return true;
            case R.id.byDefault:  // inserting order
                sorting = 0;
                finish();
                startActivity(getIntent());
                return true;
            case R.id.byDate:  // sort by due date
                sorting = 1;
                finish();
                startActivity(getIntent());
                return true;
            case R.id.byPriority:  // sort by priority
                sorting = 2;
                finish();
                startActivity(getIntent());
                return true;
            case R.id.byCompleted:  // get completed tasks
                sorting = 3;
                finish();
                startActivity(getIntent());
                return true;
            default:
                return false;
        }
    }

    public void getAllTasks(String s) {  /* get all tasks of the group */
        c = gDB.getGroupTasks(s);
        startManagingCursor(c);
        String[] from = new String[] { gDB.getTaskTitle(), gDB.getDueDate() };
        int[] to = new int[] {R.id.rowTitle, R.id.rowDate};
        // use a custom SimpleCursorAdapter to customize the display of tasks
        CustomAdapter adapter = new CustomAdapter(this, R.layout.task_row, c, from, to);
        setListAdapter(adapter);
    }

    public void displayTasks() {  /* get all tasks in DB and display them */
        if (sorting == 0) {
            getAllTasks("Default");
        } else if (sorting == 1) {
            getAllTasks("Due Date");
        } else if (sorting == 2) {
            getAllTasks("Priority");
        } else if (sorting == 3) {
            getAllTasks("Completed");
        }
    }

    private ActionMode.Callback callback = new ActionMode.Callback() {  /* contextual action bar */
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.task_view, menu);  // create menu items
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
            switch (item.getItemId()) {  // set action for menu items
                case R.id.editItem:  // edit task
                    Intent toEditingGroup = new Intent(TaskActivity.this, EditingTask.class);
                    startActivity(toEditingGroup);
                    return true;
                case R.id.deleteItem:  // delete selected task
                    gDB.deleteTask();
                    finish();
                    startActivity(getIntent());  // refresh the activity
                    return true;
                default: return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {}
    };
}
