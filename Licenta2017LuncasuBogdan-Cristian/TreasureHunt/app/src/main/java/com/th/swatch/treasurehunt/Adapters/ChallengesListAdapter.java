package com.th.swatch.treasurehunt.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.th.swatch.treasurehunt.ChallengeActivity;
import com.th.swatch.treasurehunt.Models.ChallengeModel;
import com.th.swatch.treasurehunt.R;
import com.th.swatch.treasurehunt.Services.AcceptChallengeTask;

import java.util.List;

/**
 * Created by swatch on 14.06.2017.
 */

public class ChallengesListAdapter extends BaseAdapter {
    LayoutInflater inflater;
    List<ChallengeModel> challenges;
    String apiUrl;
    Context context;
    public ChallengesListAdapter(LayoutInflater inflater, List<ChallengeModel> challenges, String apiUrl, Context context){
        this.inflater=inflater;
        this.challenges=challenges;
        this.apiUrl=apiUrl;
        this.context=context;
    }

    @Override
    public int getCount() {
        return challenges.size();
    }

    @Override
    public Object getItem(int position) {
        return challenges.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View challengesView = inflater.inflate(R.layout.challenges_list_item, parent, false);
        final ChallengeModel challenge = (ChallengeModel) getItem(position);
        TextView title = ((TextView) challengesView.findViewById(R.id.title));
        if (title != null) {
            title.setText(challenge.getTitle());
        }

        Button accept=(Button) challengesView.findViewById(R.id.accept_button);

        if (challenge.isAccepted()){
            accept.setEnabled(false);
            accept.setVisibility(View.INVISIBLE);
        }else{
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AcceptChallengeTask(apiUrl,challenge.getId(), (Activity) context).execute();
                }
            });
        }
        return challengesView;
    }
}
