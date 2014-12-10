package com.example.Tasks.tasks.Adding;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.*;
import android.widget.*;

import com.example.Tasks.R;
import com.example.Tasks.tasks.Database.GroupDBAdapter;

import java.util.ArrayList;
import java.util.List;

public class AddingTask extends Activity {
    private GroupDBAdapter gDB;
    private Cursor c;
    private String selectedID, selectedPriority, selectedTitle;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.add_task);
        gDB = new GroupDBAdapter(this);
        gDB.open();

        final Spinner spinner = (Spinner) findViewById(R.id.groupSpinner);  /* add group names to group spinner */
        final List<String> list = getAllGroups();
        ArrayAdapter<String> gAA = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        gAA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(gAA);

        Spinner s = (Spinner) findViewById(R.id.prio_spinner);  /* add priorities to priority spinner */
        ArrayAdapter<CharSequence> pAA = ArrayAdapter.createFromResource(this, R.array.priority_spinner,
                android.R.layout.simple_spinner_item);
        pAA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(pAA);

        final EditText title = (EditText) findViewById(R.id.taskTitleText);
        final DatePicker date = (DatePicker) findViewById(R.id.datepick);
        final EditText note = (EditText) findViewById(R.id.textarea);
        Button add = (Button) findViewById(R.id.taskOkButton);
        ImageButton addColla = (ImageButton) findViewById(R.id.addColla);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {  /* set id of the selected group */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedTitle = list.get(position);
                c = gDB.getGroupID(selectedTitle);
                startManagingCursor(c);
                c.moveToFirst();
                selectedID = c.getString(c.getColumnIndex("_id"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {  showDialog(2); }
        });

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {  /* set priority */
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

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  /* add new task */
                String newTitle = title.getText().toString();
                String newDate = (date.getMonth() + 1) + "/" + date.getDayOfMonth() + "/" + date.getYear();
                String newNote = note.getText().toString();
                boolean check = ((CheckBox) findViewById(R.id.completeStt)).isChecked();

                if (!newTitle.equals("")) {
                    EditText contacts = (EditText) findViewById(R.id.collaList);
                    String info = contacts.getText().toString();  // get name and number of each collaborator
                    gDB.addTask(newTitle, selectedID, newDate, newNote, selectedPriority, info, check);

                    if (!info.equals("")) {
                        String[] contact = info.split(" ; ");
                        SmsManager smsManager = SmsManager.getDefault();

                        for (byte i = 0; i < contact.length; i++) {
                            String[] values = contact[i].split(":");
                            // send the following message to each collaborator
                            String message = "Hello, you are invited to join the following task: \n" +
                                    "Title: " + newTitle + "\n" +
                                    "Due Date: " + newDate + "\n" +
                                    "Group: " + selectedTitle + "\n" +
                                    "Task Note: " + newNote + "\n" +
                                    "Priority Level: " + selectedPriority + "\n" +
                                    "Completed: " + check;
                            sendMessage(values[1], message);
                        }
                    }
                    finish();
                } else {
                    showDialog(1);
                }
            }
        });
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

    @Override
    public Dialog onCreateDialog(int id) {  /* setup dialog */
        switch (id) {
            case 1:
                return new AlertDialog.Builder(this)
                        .setTitle("The title is empty. Please type a title for the task")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dismissDialog(1);
                            }
                        }).create();
            case 2:
                return new AlertDialog.Builder(this)
                        .setTitle("No group selected. Please choose one group.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dismissDialog(1);
                            }
                        }).create();
        }
        return null;
    }

    public void sendMessage(String number, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(number, null, message, null, null);
    }

    public ArrayList<String> getAllGroups() {  /* get name of all groups */
        ArrayList<String> list = new ArrayList<String>();

        c = gDB.getAllGroups("Default");
        startManagingCursor(c);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            list.add(c.getString(c.getColumnIndex("title")));
        }

        return list;
    }
}
