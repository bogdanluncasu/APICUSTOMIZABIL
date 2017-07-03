package com.th.swatch.treasurehunt.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.th.swatch.treasurehunt.Models.ChallengeModel;
import com.th.swatch.treasurehunt.Models.TaskModel;
import com.th.swatch.treasurehunt.R;
import com.th.swatch.treasurehunt.Services.AcceptChallengeTask;

import java.util.List;

/**
 * Created by swatch on 18.06.2017.
 */

public class TasksListAdapter extends BaseAdapter {
    Context context;
    List<TaskModel> tasks;
    public TasksListAdapter(Context context, List<TaskModel> tasks){
        this.context=context;
        this.tasks=tasks;
    }
    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return tasks.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View taskView =
                ((Activity)context).getLayoutInflater()
                        .inflate(R.layout.tasks_list_item, parent, false);

        final TaskModel task = (TaskModel) getItem(position);
        TextView title = ((TextView) taskView.findViewById(R.id.title));
        if (title != null) {
            title.setText(task.getTitle());
        }

        if (!task.isActive()){
            taskView.setBackground(context.getResources().getDrawable(R.drawable.rounded_corners));
        }
        return taskView;
    }
}
