package com.example.Tasks.tasks.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GroupDBAdapter {
    protected static final String keyID = "_id";
    protected static final String keyTitle = "title";
    protected static final String DB1_NAME = "group_table";
    protected static final int DB1_VERSION = 1;

    protected static final String taskID = "_id";
    protected static final String groupID = "groupID";
    protected static final String taskTitle = "title";
    protected static final String dateGap = "date_gap";
    protected static final String dueDate = "due_date";
    protected static final String extra = "note";
    protected static final String priority = "priority";
    protected static final String collas = "collaborator";
    protected static final String status = "completed";
    protected static final String DB2_NAME = "task_table";
    protected static final int DB2_VERSION = 1;

    private static String chosenGroupID;
    private static String chosenTaskID;
    private GroupDBHelper helper_1;
    private GroupDBHelper helper_2;
    private SQLiteDatabase db_1;
    private SQLiteDatabase db_2;
    private Context context;

    /**********/
    public static class GroupDBHelper extends SQLiteOpenHelper {
        public GroupDBHelper(Context c, String n, SQLiteDatabase.CursorFactory cf, int version) {
            super(c, n, cf, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE group_table ( " +  // create group table
                    "_id text PRIMARY KEY, " +
                    "title text NOT NULL );");
            db.execSQL("INSERT INTO group_table VALUES ( " +  // set 4 default groups
                    "'gD1', " +
                    "'Personal' );");
            db.execSQL("INSERT INTO group_table VALUES ( " +
                    "'gD2', " +
                    "'Workplace' );");
            db.execSQL("INSERT INTO group_table VALUES ( " +
                    "'gD3', " +
                    "'School' );");
            db.execSQL("INSERT INTO group_table VALUES ( " +
                    "'gD4', " +
                    "'Home' );");

            db.execSQL("CREATE TABLE task_table ( " +  // create task table
                    "_id text, " +
                    "groupID text REFERENCES group_table(_id), " +
                    "title text NOT NULL, " +
                    "due_date text, " +
                    "date_gap long, " +
                    "note text, " +
                    "priority text, " +
                    "collaborator text, " +
                    "completed boolean," +
                    "PRIMARY KEY(_id, groupID) );");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i2) {  // upgrade table
            db.execSQL("DROP TABLE IF EXISTS group_table");
            db.execSQL("DROP TABLE IF EXISTS task_table");
            onCreate(db);
        }
    }
    /**********/

    public GroupDBAdapter(Context context) {  // constructor
        this.context = context;
    }

    public GroupDBAdapter open() {  /* open database in order to modify it */
        helper_1 = new GroupDBHelper(context, DB1_NAME, null, DB1_VERSION);
        helper_2 = new GroupDBHelper(context, DB2_NAME, null, DB2_VERSION);
        db_1 = helper_1.getWritableDatabase();
        db_2 = helper_2.getWritableDatabase();
        return this;
    }

    public void close() {  /* close database */
        helper_1.close();
        helper_2.close();
    }

    /********* GROUP METHODS *********/
    public long addGroup(String title) {  /* add a new group */
        // generate random characters
        Random r = new Random();
        int a = r.nextInt(25) + 65;
        int b = r.nextInt(25) + 97;
        int c = r.nextInt(1000);

        ContentValues cv = new ContentValues();
        cv.put(keyID, "G" + (char) a + (char) b + c);
        cv.put(keyTitle, title);
        return db_1.insert(DB1_NAME, null, cv);
    }

    public Cursor getGroupDetails() {  /* get details of a group */
        return db_1.rawQuery("select _id, title from group_table where _id = \"" + chosenGroupID + "\"", null);
    }

    public Cursor getGroupDetails(String id) {  /* get details of a group based on id */
        return db_1.rawQuery("select _id, title from group_table where _id = \"" + id + "\"", null);
    }

    public Cursor getGroupID(String title) {  /* get id of a group based on title */
        return db_1.rawQuery("select _id, title from group_table where title = \"" + title + "\"", null);
    }

    public int updateGroup(String newTitle) {  /* update details of group */
        ContentValues cv = new ContentValues();
        cv.put(keyTitle, newTitle);

        return db_1.update(DB1_NAME, cv, keyID + " LIKE ?", new String[] {chosenGroupID});
    }

    public boolean deleteGroup() {  /* delete a group */
        return db_1.delete(DB1_NAME, keyID + " = \"" + chosenGroupID + "\"", null) > 0;
    }

    public Cursor getAllGroups(String sort) {  /* query all group names in the DB */
        if (sort.equals("Default")) {  // inserting order
            return db_1.rawQuery("SELECT * FROM group_table", null);
        } else if (sort.equals("Alphabetical")) {  // alphabetical order
            return db_1.rawQuery("SELECT * FROM group_table ORDER BY title", null);
        } else { return null; }
    }
    /********* GROUP METHODS *********/

    /********* TASK METHODS *********/
    public long addTask(String title, String group, String due, String note, String prio,
                        String colla, boolean stt) {  /* add task */
        Random r = new Random();  // random ID
        int a = r.nextInt(25) + 65;
        int b = r.nextInt(25) + 97;
        int c = r.nextInt(1000);

        ContentValues cv = new ContentValues();
        cv.put(taskID, "T" + (char) a + (char) b + c);
        cv.put(taskTitle, title);
        cv.put(groupID, group);
        cv.put(dueDate, formatDate(due));
        cv.put(dateGap, new Date(due).getTime());
        cv.put(extra, note);
        cv.put(priority, prio);
        cv.put(collas, colla);
        cv.put(status, stt);
        return db_2.insert(DB2_NAME, null, cv);
    }

    public Cursor getGroupTasks(String sort) {  /* get tasks of a particular group */
        if (sort.equals("Default")) {  // inserting order
            return db_2.rawQuery("SELECT _id, title, due_date, completed, priority " +
                    "FROM task_table WHERE groupID = " + "\"" + chosenGroupID + "\"", null);
        } else if (sort.equals("Due Date")) {  // date order (ascending)
            return db_2.rawQuery("SELECT _id, title, due_date, completed, priority " +
                    "FROM task_table WHERE groupID = " + "\"" + chosenGroupID + "\" ORDER BY date_gap", null);
        } else if (sort.equals("Priority")) {  // priority order (high first)
            return db_2.rawQuery("SELECT _id, title, due_date, completed, priority " +
                    "FROM task_table WHERE groupID = " + "\"" + chosenGroupID + "\" ORDER BY priority", null);
        } else if (sort.equals("Completed")) {  // completed tasks
            return db_2.rawQuery("SELECT _id, title, due_date, completed, priority " +
                    "FROM task_table WHERE completed = 1", null);
        } else { return null; }
    }

    public Cursor getAllTasks() {
        return db_2.rawQuery("SELECT * FROM task_table", null);
    }

    public Cursor getAllTasksWithLimit() {
        return db_2.rawQuery("SELECT * FROM task_table WHERE completed = 0 " +
                "ORDER BY date_gap", null);
    }

    public boolean deleteTasksOfGroup() {  /* delete tasks of a group */
        return db_2.delete(DB2_NAME, groupID + " = \"" + chosenGroupID + "\"", null) > 0;
    }

    public Cursor getTaskDetails() {  /* get all details of a task */
        return db_2.rawQuery("SELECT _id, groupID, title, due_date, note, priority, collaborator, completed " +
                "FROM task_table WHERE _id = \"" + chosenTaskID + "\"", null);
    }

    public boolean deleteTask() {  /* delete a task */
        return db_2.delete(DB2_NAME, taskID + " = \"" + chosenTaskID + "\"", null) > 0;
    }

    public int updateTask(String title, String group, String due, String note, String prio, String collaResult,
                          boolean stt) { /* update task */
        ContentValues cv = new ContentValues();
        cv.put(taskTitle, title);
        cv.put(groupID, group);
        cv.put(dueDate, formatDate(due));
        cv.put(dateGap, new Date(due).getTime());
        cv.put(extra, note);
        cv.put(priority, prio);
        cv.put(collas, collaResult);
        cv.put(status, stt);

        return db_2.update(DB2_NAME, cv, taskID + " LIKE ?", new String[] {chosenTaskID});
    }
    /********* TASK METHODS *********/

    public String formatDate(String due) {  // format date
        Date newDate = new Date(due);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String d = dateFormat.format(newDate);
        System.out.println(d);
        return d;
    }

    public String getKeyTitle() { return keyTitle; }

    public String getTaskTitle() { return taskTitle; }

    public String getDueDate() { return dueDate; }

    public void setChosenGroupID(String id) {
        chosenGroupID = id;
    }

    public void setChosenTaskID(String id) { chosenTaskID = id; }
}
