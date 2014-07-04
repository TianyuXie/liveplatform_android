package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.adapter.ProgramAdapter;
import com.pplive.liveplatform.adapter.UserAdapter;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.core.search.BaseSearchHelper.LoadListener;
import com.pplive.liveplatform.core.search.SearchProgramHelper;
import com.pplive.liveplatform.core.search.SearchUserHelper;
import com.pplive.liveplatform.widget.TopBarView;
import com.pplive.liveplatform.widget.dialog.RefreshDialog;

public class SearchResultActivity extends Activity {

    static final String TAG = SearchResultActivity.class.getSimpleName();

    public final static String KEY_SEARCH_KEY_WORD = "search_key_word";

    private TopBarView mTopBarView;

    private PullToRefreshGridView mProgramContainer;

    private ProgramAdapter mProgramAdapter;

    private SearchProgramHelper mSearchProgramHelper;

    private PullToRefreshListView mUserContainer;

    private UserAdapter mUserAdapter;

    private SearchUserHelper mSearchUserHelper;

    private RadioGroup mRadioGroup;

    private RefreshDialog mRefreshDialog;

    private String mKeyword;

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

        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "checkedId: " + checkedId);

                updateTitle();
                search();
            }
        });

        mProgramContainer = (PullToRefreshGridView) findViewById(R.id.program_container);
        mProgramContainer.setOnRefreshListener(new OnRefreshListener2<GridView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                mSearchProgramHelper.refresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                mSearchUserHelper.append();
            }
        });

        mProgramContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mProgramAdapter) {
                    Program program = mProgramAdapter.getItem(position);

                    Intent intent = new Intent(getApplicationContext(), LivePlayerActivity.class);
                    intent.putExtra(LivePlayerActivity.EXTRA_PROGRAM, program);
                    startActivity(intent);
                }
            }
        });

        mProgramAdapter = new ProgramAdapter(this);
        mProgramContainer.setAdapter(mProgramAdapter);

        mSearchProgramHelper = new SearchProgramHelper(mProgramAdapter);
        mSearchProgramHelper.setLoadListener(new LoadListener() {

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

        GridLayoutAnimationController glac = (GridLayoutAnimationController) AnimationUtils.loadLayoutAnimation(getApplicationContext(),
                R.anim.home_gridview_flyin);
        mProgramContainer.getRefreshableView().setLayoutAnimation(glac);

        mUserContainer = (PullToRefreshListView) findViewById(R.id.user_container);
        mUserContainer.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mSearchUserHelper.refresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mSearchUserHelper.append();
            }
        });

        mUserAdapter = new UserAdapter(this);
        mUserContainer.setAdapter(mUserAdapter);

        mSearchUserHelper = new SearchUserHelper(mUserAdapter);
        mSearchUserHelper.setLoadListener(new LoadListener() {

            @Override
            public void onLoadStart() {
                mRefreshDialog.show();
            }

            @Override
            public void onLoadSucceed() {
                mRefreshDialog.hide();
                mUserContainer.onRefreshComplete();
            }

            @Override
            public void onLoadFailed() {
                mRefreshDialog.hide();
                mUserContainer.onRefreshComplete();
            }
        });

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
        mKeyword = intent.getStringExtra(KEY_SEARCH_KEY_WORD);

        updateTitle();
        search();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mRefreshDialog.isShowing()) {
            mRefreshDialog.dismiss();
        }
    }

    private void updateTitle() {
        int resId = mapViewIdToResId(mRadioGroup.getCheckedRadioButtonId());

        mTopBarView.setTitle(getString(resId, mKeyword));
    }

    private int mapViewIdToResId(int id) {
        if (R.id.radio_btn_program == id) {
            return R.string.search_program_title_fmt;
        } else if (R.id.radio_btn_user == id) {
            return R.string.search_user_title_fmt;
        }

        return R.string.search_program_title_fmt;
    }

    private void search() {
        int checkedId = mRadioGroup.getCheckedRadioButtonId();
        if (R.id.radio_btn_program == checkedId) {
            mUserContainer.setVisibility(View.GONE);

            mProgramContainer.setVisibility(View.VISIBLE);

            mSearchProgramHelper.searchByKeyword(mKeyword);
        } else if (R.id.radio_btn_user == checkedId) {

            mProgramContainer.setVisibility(View.GONE);

            mUserContainer.setVisibility(View.VISIBLE);

            mSearchUserHelper.searchByKeyword(mKeyword);
        }
    }

}
