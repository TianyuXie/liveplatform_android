package com.pplive.liveplatform.ui.homepage.program;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.homepage.IAnswerView;

public class ProgramView implements IAnswerView {
    private JSONArray response;

    @Override
    public View getView(Context context) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_homepage_answer_container, null);
        LinearLayout parentLayout = (LinearLayout) layout.findViewById(R.id.linearlayout_answer_results);
        addChildView(context, parentLayout);
        return layout;
    }

    @Override
    public void setData(Object data) {
        this.response = (JSONArray) data;
    }

    @Override
    public void addChildView(Context context, LinearLayout parentLayout) {
        try {
            LinearLayout container = null;
            for (int i = 0; i < response.length(); i++) {
                if (i % 2 == 0) {
                    container = new LinearLayout(context);
                }
                ProgramItemView itemView = new ProgramItemView();
                itemView.setData(response.get(i));
                container.addView(itemView.getView(context));
                if (i % 2 == 1 || i == response.length() - 1) {
                    parentLayout.addView(container, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
