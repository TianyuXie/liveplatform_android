package com.pplive.liveplatform.ui.homepage.program;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.homepage.IAnswerView;
import com.pplive.liveplatform.vo.program.Program;

public class ProgramView implements IAnswerView {
    private JsonObject response;

    @Override
    public View getView(Context context) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_homepage_answer_container, null);
        LinearLayout parentLayout = (LinearLayout) layout.findViewById(R.id.layout_answer_results);
        addChildView(context, parentLayout);
        return layout;
    }

    @Override
    public void setData(Object data) {
        this.response = (JsonObject) data;
    }

    @Override
    public void addChildView(Context context, LinearLayout parentLayout) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        JsonElement list = response.get("data").getAsJsonObject().get("list");
        Gson gson = new Gson();
        List<Program> programs = gson.fromJson(list, new TypeToken<List<Program>>() {
        }.getType());
        int realsize = programs.size();
        int showsize = (realsize % 2 == 1) ? realsize + 1 : realsize;
        LinearLayout container = null;
        for (int i = 0; i < showsize; i++) {
            if (i % 2 == 0) {
                container = new LinearLayout(context);
            }
            if (i < realsize) {
                ProgramItemView itemView = new ProgramItemView();
                itemView.setData(programs.get(i));
                container.addView(itemView.getView(context), lp);
            } else {
                View fakeview = new RelativeLayout(context);
                container.addView(fakeview, lp);
            }
            if (i % 2 == 1) {
                parentLayout.addView(container, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        }
    }
}
