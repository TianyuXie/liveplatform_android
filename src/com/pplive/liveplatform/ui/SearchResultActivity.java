package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.GridView;
import android.widget.RadioGroup;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.adapter.ProgramAdapter;
import com.pplive.liveplatform.core.search.ProgramSearchHelper;
import com.pplive.liveplatform.core.search.ProgramSearchHelper.LoadListener;
import com.pplive.liveplatform.widget.TopBarView;
import com.pplive.liveplatform.widget.dialog.RefreshDialog;

public class SearchResultActivity extends Activity {

    static final String TAG = SearchResultActivity.class.getSimpleName();

    public final static String KEY_SEARCH_KEY_WORD = "search_key_word";

    private TopBarView mTopBarView;

    private PullToRefreshGridView mProgramContainer;

    private ProgramAdapter mProgramAdapter;

    private ProgramSearchHelper mProgramSearchHelper;

    private RadioGroup mRadioGroup;

    private RefreshDialog mRefreshDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_result);

        mTopBarView = (TopBarView) findViewById(R.id.top_bar);
        mTopBarView.setLeftBtnOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mProgramContainer = (PullToRefreshGridView) findViewById(R.id.program_container);
        mProgramContainer.setOnRefreshListener(new OnRefreshListener2<GridView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                refreshProgram();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                appendProgram();
            }
        });

        mProgramAdapter = new ProgramAdapter(this);
        mProgramContainer.setAdapter(mProgramAdapter);

        mProgramSearchHelper = new ProgramSearchHelper(mProgramAdapter);
        mProgramSearchHelper.setLoadListener(new LoadListener() {

            @Override
            public void onLoadStart() {
                mRefreshDialog.show();
            }

            @Override
            public void onLoadSucceed() {
                mRefreshDialog.hide();
                mProgramContainer.onRefreshComplete();
            }

            @Override
            public void onLoadFailed() {
                mRefreshDialog.hide();
                mProgramContainer.onRefreshComplete();
            }
        });

        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "checkedId: " + checkedId);

                updateTitle();
            }
        });

        GridLayoutAnimationController glac = (GridLayoutAnimationController) AnimationUtils.loadLayoutAnimation(getApplicationContext(),
                R.anim.home_gridview_flyin);
        mProgramContainer.getRefreshableView().setLayoutAnimation(glac);

        mRefreshDialog = new RefreshDialog(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String keyword = intent.getStringExtra(KEY_SEARCH_KEY_WORD);

        updateTitle(mRadioGroup.getCheckedRadioButtonId(), keyword);
        searchProgramByKeyword(keyword);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mRefreshDialog.isShowing()) {
            mRefreshDialog.dismiss();
        }
    }

    private void updateTitle() {
        updateTitle(mProgramSearchHelper.getKeyword());
    }

    private void updateTitle(String keyword) {
        updateTitle(mRadioGroup.getCheckedRadioButtonId(), keyword);
    }

    private void updateTitle(int checkedId, String keyword) {
        int resId = mapViewIdToResId(checkedId);

        mTopBarView.setTitle(getString(resId, keyword));
    }

    private void searchProgramByKeyword(String keyword) {
        mProgramSearchHelper.searchByKeyword(keyword);
    }

    private void refreshProgram() {
        mProgramSearchHelper.refresh();
    }

    private void appendProgram() {
        mProgramSearchHelper.append();
    }

    private int mapViewIdToResId(int id) {
        if (R.id.radio_btn_program == id) {
            return R.string.search_program_title_fmt;
        } else if (R.id.radio_btn_user == id) {
            return R.string.search_user_title_fmt;
        }

        return R.string.search_program_title_fmt;
    }

}
