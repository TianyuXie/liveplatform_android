package com.pplive.liveplatform.ui.home.program;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.home.IAnswerView;
import com.pplive.liveplatform.vo.program.Program;

public class ProgramView implements IAnswerView {
    private Context mContext;

    private List<Program> mPrograms;
    private ProgramAdapter mAdapter;

    public ProgramView(Context context) {
        mContext = context;
        mPrograms = new ArrayList<Program>();
        mAdapter = new ProgramAdapter(context, mPrograms);
    }

    @Override
    public View getView() {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(mContext).inflate(
                R.layout.layout_home_container, null);
        GridView gridView = (GridView) layout.findViewById(R.id.gridview_answer_results);
        gridView.setAdapter(mAdapter);
        return layout;
    }

    @Override
    public void updateData(Object data) {
        JsonElement list = ((JsonObject) data).get("data").getAsJsonObject().get("list");
        Collection<Program> newPrograms = new Gson().fromJson(list,
                new TypeToken<Collection<Program>>() {
                }.getType());
        mPrograms.addAll(newPrograms);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void addChildView(Context context, ViewGroup parentLayout) {
        // Do nothing
    }
}
