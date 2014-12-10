package com.example.Tasks.tasks.Editing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.*;
import android.widget.*;
import com.example.Tasks.tasks.Database.GroupDBAdapter;
import com.example.Tasks.R;

import java.util.ArrayList;
import java.util.List;

public class EditingTask extends Activity {
    private GroupDBAdapter db;
    private String oldTitle, selectedID, selectedPriority, selectedTitle;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.edit_task);
        db = new GroupDBAdapter(this);
        db.open();

        final EditText title = (EditText) findViewById(R.id.taskTitleText);
        final DatePicker date = (DatePicker) findViewById(R.id.datepick);
        final EditText note = (EditText) findViewById(R.id.textarea);
        final EditText colla = (EditText) findViewById(R.id.collaList);
        final ImageButton addColla = (ImageButton) findViewById(R.id.addColla);
        Spinner priority = (Spinner) findViewById(R.id.prio_spinner);
        Spinner groups = (Spinner) findViewById(R.id.groupSpinner);

        /****** GETTING OLD DETAILS ******/
        Cursor c = db.getTaskDetails();
        startManagingCursor(c);
        c.moveToFirst();

        title.setText(c.getString(c.getColumnIndex("title")));  // add title
        String d = c.getString(c.getColumnIndex("due_date"));
        String[] separateDate = d.split("/");
        date.init(Integer.parseInt(separateDate[2]), Integer.parseInt(separateDate[0]) - 1,
                Integer.parseInt(separateDate[1]), null);  // add date

        note.setText(c.getString(c.getColumnIndex("note")));  // add note
        int priorityPosition = selectedPriority(c.getString(c.getColumnIndex("priority")));
        String selectedGroupID = c.getString(c.getColumnIndex("groupID"));
        oldTitle = getSelectedGroupTitle(selectedGroupID);  // get old title of the group
        int stt = c.getInt(c.getColumnIndex("completed"));
        boolean status = true;
        if (stt == 1) {  // get old status
            status = true;
        } else {
            status = false;
        }

        // add group names to spinner
        final List<String> list = getAllGroups();
        ArrayAdapter<String> gAA = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        gAA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groups.setAdapter(gAA);

        // add priorities to spinner
        ArrayAdapter<CharSequence> pAA = ArrayAdapter.createFromResource(this, R.array.priority_spinner,
                android.R.layout.simple_spinner_item);
        pAA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priority.setAdapter(pAA);
        priority.setSelection(priorityPosition);  // set the default priority as the old one

        colla.setText(c.getString(c.getColumnIndex("collaborator"))); // add collaborators

        ((CheckBox) findViewById(R.id.completeStt)).setChecked(status);  // add status
        /****** GETTING OLD DETAILS ******/

        // set id of the selected group
        groups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedTitle = list.get(position);
                Cursor cursor = db.getGroupID(selectedTitle);
                startManagingCursor(cursor);
                cursor.moveToFirst();
                selectedID = cursor.getString(cursor.getColumnIndex("_id"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                showDialog(2);
            }
        });

        priority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {  // set priority
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    selectedPriority = "1 - HIGH";
                } else if (position == 1) {
                    selectedPriority = "2 - Medium";
                } else {
                    selectedPriority = "3 - Low";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        addColla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  /* show contact list */
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, 1);
            }
        });

        Button apply = (Button) findViewById(R.id.taskOkButton);  // apply changes
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTitle = title.getText().toString();
                String newDate = (date.getMonth() + 1) + "/" + date.getDayOfMonth() + "/" + date.getYear();
                String newNote = note.getText().toString();
                String newCollaResult = colla.getText().toString();
                boolean check = ((CheckBox) findViewById(R.id.completeStt)).isChecked();

                if (!newTitle.equals("")) {
                    db.updateTask(newTitle, selectedID, newDate, newNote, selectedPriority, newCollaResult, check);
                    Toast.makeText(getApplicationContext(), "Task updated successfully",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    showDialog(1);
                }
            }
        });
    }

    @Override
    public Dialog onCreateDialog(int id) {  /* setup dialog */
        switch (id) {
            case 1:
                return new AlertDialog.Builder(this)
                        .setTitle("The title is empty. Please enter a title for the task")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dismissDialog(1);
                            }
                        }).create();
        }
        return null;
    }

    @Override
    public void onActivityResult(int request, int result, Intent data) {  /* get contact's name and phone number */
        super.onActivityResult(request, result, data);
        switch (request) {
            case 1:
                if (result == Activity.RESULT_OK) {
                    Uri contactDetails = data.getData();
                    Cursor cursor = managedQuery(contactDetails, null, null, null, null);

                    if (cursor.moveToFirst()) {
                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.
                                HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor contact = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                            contact.moveToFirst();
                            String number = contact.getString(contact.getColumnIndex(ContactsContract.
                                    CommonDataKinds.Phone.NUMBER));  // get contact's number
                            String name = contact.getString(contact.getColumnIndex(ContactsContract.
                                    Contacts.DISPLAY_NAME));  // get contact's name
                            // display them on the UI
                            String collaResult = name + ":" + number + " ; ";
                            ((EditText) findViewById(R.id.collaList)).append(collaResult);
                        }
                    }
                }
        }
    }

    public ArrayList<String> getAllGroups() {  /* get name of current groups */
        ArrayList<String> list = new ArrayList<String>();

        Cursor cursor = db.getAllGroups("Default");
        startManagingCursor(cursor);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String next = cursor.getString(cursor.getColumnIndex("title"));
            if (next.equals(oldTitle)) {
                list.add(0, next);  // set old group's name at the top of spinner
            } else {
                list.add(next);
            }
        }

        return list;
    }

    public int selectedPriority(String name) {  /* get priority name */
        if (name.equals("1 - HIGH")) {
            return 0;
        } else if (name.equals("2 - Medium")) {
            return 1;
        } else {
            return 2;
        }
    }

    public String getSelectedGroupTitle(String id) {  /* get the group title of the task */
        Cursor cursor = db.getGroupDetails(id);
        startManagingCursor(cursor);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex("title"));
    }
}
