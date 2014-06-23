package com.pplive.liveplatform.ui.navigate;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.AdapterView;
import android.widget.GridView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.SearchService;
import com.pplive.liveplatform.core.service.live.model.FallList;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.ui.LivePlayerActivity;
import com.pplive.liveplatform.ui.adpater.ProgramAdapter;

public class HomeFragment extends Fragment {

    static final String TAG = HomeFragment.class.getSimpleName();

    private PullToRefreshGridView mContainer;

    private ProgramAdapter mAdapter;

    private boolean mInited = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_home2, container, false);

        mContainer = (PullToRefreshGridView) layout.findViewById(R.id.program_container);
        mContainer.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {

            @Override
            public void onRefresh(PullToRefreshBase<GridView> refreshView) {
                AsyncTaskGetRecommendProgramList task = new AsyncTaskGetRecommendProgramList();

                task.execute();
            }
        });

        mContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mAdapter) {
                    Program program = mAdapter.getItem(position);

                    Intent intent = new Intent(getActivity(), LivePlayerActivity.class);
                    intent.putExtra(LivePlayerActivity.EXTRA_PROGRAM, program);
                    startActivity(intent);
                }
            }
        });

        GridLayoutAnimationController glac = (GridLayoutAnimationController) AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.home_gridview_flyin);
        mContainer.getRefreshableView().setLayoutAnimation(glac);

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ProgramAdapter(getActivity());
        mContainer.setAdapter(mAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mInited) {
            AsyncTaskGetRecommendProgramList task = new AsyncTaskGetRecommendProgramList();

            task.execute();

            mInited = true;
        }
    }

    class AsyncTaskGetRecommendProgramList extends AsyncTask<Void, Void, List<Program>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mContainer.setRefreshing();
        }

        @Override
        protected List<Program> doInBackground(Void... params) {
            FallList<Program> programs = null;
            try {
                programs = SearchService.getInstance().getRecommandedProgram();
            } catch (LiveHttpException e) {
            }

            return null != programs ? programs.getList() : null;
        }

        @Override
        protected void onPostExecute(List<Program> programs) {

            mContainer.onRefreshComplete();

            if (null != programs) {
                mAdapter.refreshData(programs);
            }
        }
    }

}
