package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.widget.TopBarView;

public class QualitySettingActivity extends Activity {

    private TopBarView mTopBarView;

    private RadioGroup mGroupQuality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_quality_setting);

        mTopBarView = (TopBarView) findViewById(R.id.top_bar);
        mTopBarView.setLeftBtnOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTopBarView.showLeftBtn();

        mGroupQuality = (RadioGroup) findViewById(R.id.group_quality);
        mGroupQuality.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                finish();
            }
        });
    }
}
