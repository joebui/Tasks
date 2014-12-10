package com.example.Tasks.tasks.Adding;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.Tasks.R;
import com.example.Tasks.tasks.Database.GroupDBAdapter;

public class AddingGroup extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_group);

        final GroupDBAdapter gDB = new GroupDBAdapter(this);
        gDB.open();
        Button adding = (Button) findViewById(R.id.groupOkButton);
        final EditText groupTitle = (EditText) findViewById(R.id.groupTitleText);
        // add new group
        adding.setOnClickListener(new View.OnClickListener() {  // add new group when touching "Add" button
            @Override
            public void onClick(View view) {
                String t = groupTitle.getText().toString().trim();
                if (t.equals("")) {
                    showDialog(1);  // show dialog if title is empty
                } else {
                    gDB.addGroup(t);
                    Toast.makeText(getApplicationContext(), "Group added successfully", Toast.LENGTH_SHORT).show();
                    gDB.close();
                    finish();
                }
            }
        });
    }

    @Override
    public Dialog onCreateDialog(int id) {  /* setup dialog */
        switch (id) {
            case 1:
                return new AlertDialog.Builder(this)
                        .setTitle("The title is empty. Please type a title for the group")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dismissDialog(1);  // discard the dialog
                            }
                        }).create();
        }
        return null;
    }
}
