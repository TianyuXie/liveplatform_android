package com.pplive.liveplatform.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.rest.model.LiveModeEnum;
import com.pplive.liveplatform.core.rest.model.Program;
import com.pplive.liveplatform.ui.home.UserpageProgramAdapter;

public class UserpageActivity extends Activity {

    private List<Program> mPrograms;
    private ListView mListView;
    private UserpageProgramAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_userpage);

        mPrograms = new ArrayList<Program>();
        for (int i = 0; i < 10; i++) {
            mPrograms.add(new Program("hello", LiveModeEnum.CAMERA, "test", 12345));
        }
        mAdapter = new UserpageProgramAdapter(this, mPrograms);

        findViewById(R.id.btn_userpage_back).setOnClickListener(onBackBtnClickListener);
        findViewById(R.id.btn_userpage_settings).setOnClickListener(onSettingsBtnClickListener);
        mListView = (ListView) findViewById(R.id.list_userpage_program);
        mListView.setAdapter(mAdapter);
    }

    private View.OnClickListener onBackBtnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private View.OnClickListener onSettingsBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(UserpageActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
    };

}
