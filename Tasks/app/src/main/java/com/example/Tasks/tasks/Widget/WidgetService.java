package com.example.Tasks.tasks.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new TaskViewFactory(this.getApplicationContext(), intent));
    }
}
