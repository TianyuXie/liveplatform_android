package com.pplive.liveplatform.ui.home.program;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.widget.AdvancedGridView;
import com.pplive.liveplatform.vo.program.Program;

public class RefreshContainer extends LinearLayout {
    static final String TAG = "RefreshContainer";

    private List<Program> mPrograms;
    private ProgramAdapter mAdapter;
    private AdvancedGridView mGridView;

    public RefreshContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPrograms = new ArrayList<Program>();
        mAdapter = new ProgramAdapter(context, mPrograms);

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.layout_home_container, this);
        mGridView = (AdvancedGridView) root.findViewById(R.id.gridview_answer_results);
        mGridView.setAdapter(mAdapter);
    }

    public RefreshContainer(Context context) {
        this(context, null);
    }

    public void refreshData(Object data) {
        JsonElement list = ((JsonObject) data).get("data").getAsJsonObject().get("list");
        Collection<Program> newPrograms = new Gson().fromJson(list, new TypeToken<Collection<Program>>() {
        }.getType());
        mPrograms.clear();
        mPrograms.addAll(newPrograms);
        mAdapter.notifyDataSetChanged();
    }

    public void setOnReachBottomListener(AdvancedGridView.OnReachBottomListener l) {
        mGridView.setOnReachBottomListener(l);
    }
}
