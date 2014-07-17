package com.pplive.liveplatform.ui.navigate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.pplive.android.pulltorefresh.FallListHelper.LoadListener;
import com.pplive.liveplatform.Code;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.adapter.FeedProgramAdapter;
import com.pplive.liveplatform.adapter.ProgramAdapter;
import com.pplive.liveplatform.adapter.UserAdapter;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.api.live.model.Feed;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.core.network.NetworkManager;
import com.pplive.liveplatform.core.network.NetworkManager.NetworkState;
import com.pplive.liveplatform.dialog.RefreshDialog;
import com.pplive.liveplatform.task.home.GetFeedProgramsHelper;
import com.pplive.liveplatform.task.home.GetRecommendProgramsHelper;
import com.pplive.liveplatform.task.home.GetRecommendUsersHelper;
import com.pplive.liveplatform.ui.LivePlayerActivity;
import com.pplive.liveplatform.ui.LoginActivity;
import com.pplive.liveplatform.util.ViewUtil;

public class HomeFragment extends Fragment implements OnCheckedChangeListener {

    static final String TAG = HomeFragment.class.getSimpleName();

    private Activity mActivity;

    private UserManager mUserManager;

    private RadioGroup mRadioGroup;

    private PullToRefreshGridView mRecommendProgramContainer;

    private ProgramAdapter mRecommendPorgramAdapter;

    private GetRecommendProgramsHelper mGetRecommendProgramsHelper;

    private PullToRefreshGridView mFeedProgramContainer;

    private FeedProgramAdapter mFeedProgramAdapter;

    private GetFeedProgramsHelper mGetFeedPrograqmsHelper;

    private View mEmptyNoNetwork;

    private View mEmptyRecommendUser;

    private ListView mUserContainer;

    private UserAdapter mUserAdapter;

    private GetRecommendUsersHelper mGetRecommendUsersHelper;

    private boolean mRecommedInited = false;

    private boolean mFeedInited = false;

    private RefreshDialog mRefreshDialog;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActivity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mActivity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_home2, container, false);

        mRadioGroup = (RadioGroup) layout.findViewById(R.id.radio_group);
        mRadioGroup.setOnCheckedChangeListener(this);

        mRecommendProgramContainer = (PullToRefreshGridView) layout.findViewById(R.id.recommend_program_container);
        mRecommendProgramContainer.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {

            @Override
            public void onRefresh(PullToRefreshBase<GridView> refreshView) {
                mGetRecommendProgramsHelper.refresh();
            }
        });

        mRecommendProgramContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mRecommendPorgramAdapter) {
                    Program program = mRecommendPorgramAdapter.getItem(position);

                    Intent intent = new Intent(getActivity(), LivePlayerActivity.class);
                    intent.putExtra(LivePlayerActivity.EXTRA_PROGRAM, program);
                    startActivity(intent);
                }
            }
        });

        mRecommendPorgramAdapter = new ProgramAdapter(mActivity);
        mRecommendProgramContainer.setAdapter(mRecommendPorgramAdapter);
        mGetRecommendProgramsHelper = new GetRecommendProgramsHelper(mActivity, mRecommendPorgramAdapter);
        mGetRecommendProgramsHelper.setLoadListener(new LoadListener() {

            @Override
            public void onLoadStart() {
                mRefreshDialog.show();
            }

            @Override
            public void onLoadSucceed() {
                mRefreshDialog.dismiss();
                mRecommendProgramContainer.onRefreshComplete();
            }

            @Override
            public void onLoadFailed() {
                mRefreshDialog.dismiss();
                mRecommendProgramContainer.onRefreshComplete();

                if (NetworkState.DISCONNECTED == NetworkManager.getCurrentNetworkState()) {
                    mRecommendProgramContainer.setEmptyView(mEmptyNoNetwork);
                } else {
                    mRecommendProgramContainer.setEmptyView(null);
                }
            }
        });

        mFeedProgramContainer = (PullToRefreshGridView) layout.findViewById(R.id.feed_program_container);
        mFeedProgramContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mFeedProgramAdapter) {
                    Feed feed = mFeedProgramAdapter.getItem(position);

                    Intent intent = new Intent(getActivity(), LivePlayerActivity.class);
                    intent.putExtra(LivePlayerActivity.EXTRA_PROGRAM, feed.getProgram());
                    startActivity(intent);
                }
            }
        });

        mFeedProgramContainer.setOnRefreshListener(new OnRefreshListener2<GridView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                mGetFeedPrograqmsHelper.refresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                mGetFeedPrograqmsHelper.append();
            }

        });

        mFeedProgramAdapter = new FeedProgramAdapter(mActivity);
        mFeedProgramContainer.setAdapter(mFeedProgramAdapter);

        mGetFeedPrograqmsHelper = new GetFeedProgramsHelper(mActivity, mFeedProgramAdapter);
        mGetFeedPrograqmsHelper.setLoadListener(new LoadListener() {

            @Override
            public void onLoadStart() {
                mRefreshDialog.show();
            }

            @Override
            public void onLoadSucceed() {
                mRefreshDialog.dismiss();
                mFeedProgramContainer.onRefreshComplete();

                if (mFeedProgramAdapter.getCount() > 0) {
                    mFeedProgramContainer.setEmptyView(null);
                } else {
                    mFeedProgramContainer.setEmptyView(mEmptyRecommendUser);
                }
            }

            @Override
            public void onLoadFailed() {
                mRefreshDialog.dismiss();
                mFeedProgramContainer.onRefreshComplete();

                if (NetworkState.DISCONNECTED == NetworkManager.getCurrentNetworkState()) {
                    mFeedProgramContainer.setEmptyView(mEmptyNoNetwork);
                } else {
                    mFeedProgramContainer.setEmptyView(null);
                }
            }
        });

        GridLayoutAnimationController glac = (GridLayoutAnimationController) AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.home_gridview_flyin);
        mRecommendProgramContainer.getRefreshableView().setLayoutAnimation(glac);
        mFeedProgramContainer.getRefreshableView().setLayoutAnimation(glac);

        mEmptyNoNetwork = View.inflate(mActivity, R.layout.empty_no_network, null);
        mEmptyRecommendUser = View.inflate(mActivity, R.layout.empty_recommend_user, null);

        mUserContainer = (ListView) mEmptyRecommendUser.findViewById(R.id.user_container);
        mUserAdapter = new UserAdapter(mActivity);
        mUserContainer.setAdapter(mUserAdapter);
        mGetRecommendUsersHelper = new GetRecommendUsersHelper(mActivity, mUserAdapter);

        mRefreshDialog = new RefreshDialog(mActivity);

        mUserManager = UserManager.getInstance(mActivity);

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        if (!isLogin()) {
            ViewUtil.check(mRadioGroup, R.id.radio_btn_recommend);
        }

        showContent(mRadioGroup.getCheckedRadioButtonId());
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

        if (mRefreshDialog.isShowing()) {
            mRefreshDialog.dismiss();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        if (checkedId == R.id.radio_btn_recommend || (checkedId == R.id.radio_btn_focuson && isLogin())) {

            showContent(checkedId);
        } else {

            Intent intent = new Intent(mActivity, LoginActivity.class);
            startActivityForResult(intent, Code.REQUEST_GET_FEED);
        }
    }

    private boolean isLogin() {
        return mUserManager.isLogin();
    }

    private void showContent(int checkedId) {
        if (R.id.radio_btn_recommend == checkedId) {
            Log.d(TAG, "checked: R.id.radio_btn_recommend");

            mRecommendProgramContainer.setVisibility(View.VISIBLE);
            mFeedProgramContainer.setVisibility(View.GONE);

            if (!mRecommedInited) {
                mGetRecommendProgramsHelper.refresh();

                mRecommedInited = true;
            }
        } else if (R.id.radio_btn_focuson == checkedId) {
            Log.d(TAG, "checked: R.id.radio_btn_focuson");

            mRecommendProgramContainer.setVisibility(View.GONE);
            mFeedProgramContainer.setVisibility(View.VISIBLE);

            if (!mFeedInited) {
                String token = mUserManager.getToken();
                String username = mUserManager.getUsernamePlain();

                mGetFeedPrograqmsHelper.loadFeedPrograms(token, username);
                mGetRecommendUsersHelper.loadRecommendUsers(token, username);

                mFeedInited = true;
            }
        }
    }

}
