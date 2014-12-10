package com.example.Tasks.tasks.Editing;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.example.Tasks.tasks.Database.GroupDBAdapter;
import com.example.Tasks.R;

public class EditingGroup extends Activity {
    private GroupDBAdapter gDB;
    private Cursor c;
    private EditText editText;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.edit_group);
        gDB = new GroupDBAdapter(this);
        gDB.open();

        getGroupDetails();

        editText = (EditText) findViewById(R.id.editGroupTitleText);
        Button apply = (Button) findViewById(R.id.applyChanges);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  // update group's title
                gDB.updateGroup(editText.getText().toString().trim());
                Toast.makeText(getApplicationContext(), "Group updated successfully",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void getGroupDetails() {  /* get the group's title from DB */
        editText = (EditText) findViewById(R.id.editGroupTitleText);
        c = gDB.getGroupDetails();
        startManagingCursor(c);
        c.moveToFirst();
        String title = c.getString(c.getColumnIndex("title"));
        editText.setText(title);
    }
}
