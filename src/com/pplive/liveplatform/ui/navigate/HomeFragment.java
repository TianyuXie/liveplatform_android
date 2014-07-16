package com.pplive.liveplatform.ui.navigate;

import android.app.Activity;
import android.app.DownloadManager.Request;
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
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.pplive.android.pulltorefresh.FallListHelper.LoadListener;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.Code;
import com.pplive.liveplatform.adapter.ProgramAdapter;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.core.network.NetworkManager;
import com.pplive.liveplatform.core.network.NetworkManager.NetworkState;
import com.pplive.liveplatform.dialog.RefreshDialog;
import com.pplive.liveplatform.task.home.GetRecommendProgramsHelper;
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

    private View mEmptyNoNetwork;

    private boolean mInited = false;

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

        mUserManager = UserManager.getInstance(mActivity);

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

        mRecommendPorgramAdapter = new ProgramAdapter(getActivity());
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

        mEmptyNoNetwork = View.inflate(getActivity(), R.layout.empty_no_network, null);

        GridLayoutAnimationController glac = (GridLayoutAnimationController) AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.home_gridview_flyin);
        mRecommendProgramContainer.getRefreshableView().setLayoutAnimation(glac);

        mRefreshDialog = new RefreshDialog(mActivity);

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        if (!isLogin()) {
            ViewUtil.check(mRadioGroup, R.id.radio_btn_recommend);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

        if (!mInited) {
            mGetRecommendProgramsHelper.refresh();

            mInited = true;
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult " + "resquestCode: " + requestCode + "; resultCode: " + resultCode);

        if (Code.REQUEST_GET_FEED == requestCode) {
            if (!isLogin()) {
                ViewUtil.check(mRadioGroup, R.id.radio_btn_recommend);
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        if (checkedId == R.id.radio_btn_recommend) {
            Log.d(TAG, "checked: R.id.radio_btn_recommend");
            mRecommendProgramContainer.setVisibility(View.VISIBLE);
            mFeedProgramContainer.setVisibility(View.GONE);
        } else if (checkedId == R.id.radio_btn_focuson) {
            Log.d(TAG, "checked: R.id.radio_btn_focuson");

            if (isLogin()) {
                mRecommendProgramContainer.setVisibility(View.GONE);
                mFeedProgramContainer.setVisibility(View.VISIBLE);
            } else {
                Intent intent = new Intent(mActivity, LoginActivity.class);
                startActivityForResult(intent, Code.REQUEST_GET_FEED);
            }
        }
    }

    private boolean isLogin() {
        return mUserManager.isLogin();
    }

}
