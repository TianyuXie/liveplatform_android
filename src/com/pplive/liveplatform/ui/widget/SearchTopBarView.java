package com.pplive.liveplatform.ui.widget;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.SearchActivity;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

public class SearchTopBarView extends TopBarView {

    public SearchTopBarView(Context context) {
        this(context, null);
    }

    public SearchTopBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchTopBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        init();
    }

    private void init() {
        setRightBtnOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                getContext().startActivity(intent);
            }
        });

        setRightBtnImageResource(R.drawable.top_bar_search_btn);
        showRightBtn();
    }
}
