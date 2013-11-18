package com.pplive.liveplatform.ui.home.program;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.widget.RefreshGridView;
import com.pplive.liveplatform.vo.program.Program;

public class RefreshContainer extends LinearLayout {
    static final String TAG = "RefreshContainer";

    private List<Program> mPrograms;
    private ProgramAdapter mAdapter;
    private RefreshGridView mGridView;

    public RefreshContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPrograms = new ArrayList<Program>();
        mAdapter = new ProgramAdapter(context, mPrograms);

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.layout_home_container, this);
        mGridView = (RefreshGridView) root.findViewById(R.id.gridview_home_results);
        LinearLayout head = (LinearLayout) root.findViewById(R.id.layout_pull_header);
        head.addView(mGridView.getHeader(), new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mGridView.setAdapter(mAdapter);
        mGridView.setOnRefreshListener(onRefreshListener);
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

    private RefreshGridView.OnRefreshListener onRefreshListener = new RefreshGridView.OnRefreshListener() {

        @Override
        public void onRefresh() {
            new AsyncTask<Void, Void, Void>() {
                protected Void doInBackground(Void... params) {
                    //TODO
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    mGridView.onRefreshComplete();
                }
            }.execute();
        }
    };
}
