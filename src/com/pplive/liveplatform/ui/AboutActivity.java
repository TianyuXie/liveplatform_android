package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.widget.TopBarView;
import com.pplive.liveplatform.util.DisplayUtil;

public class AboutActivity extends Activity {

    private TopBarView mTopBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);

        mTopBarView = (TopBarView) findViewById(R.id.top_bar);
        mTopBarView.setLeftBtnOnClickListener(mOnBackBtnClickListener);
        mTopBarView.showLeftBtn();

        findViewById(R.id.image_about_icon).getLayoutParams().height = DisplayUtil.getWidthPx(this) * 3 / 4;
    }

    private View.OnClickListener mOnBackBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}
