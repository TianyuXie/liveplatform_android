package com.pplive.liveplatform.ui.navigate;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.SearchService;
import com.pplive.liveplatform.core.service.live.model.FallList;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.ui.home.ProgramContainer;
import com.pplive.liveplatform.ui.widget.refresh.RefreshGridView.OnUpdateListener;

public class HomeFragment extends Fragment {

    private ProgramContainer mContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_home2, container, false);

        mContainer = (ProgramContainer) layout.findViewById(R.id.layout_channel_body);
        mContainer.setOnUpdateListener(new OnUpdateListener() {
            
            @Override
            public void onScrollDown(boolean isDown) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onRefresh() {
                mContainer.onRefreshComplete();
            }
            
            @Override
            public void onAppend() {
                // TODO Auto-generated method stub
                
            }
        });
        mContainer.setStatusVisibility(View.GONE);

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();

        AsyncTask<Void, Void, java.util.List<Program>> task = new AsyncTask<Void, Void, java.util.List<Program>>() {

            @Override
            protected java.util.List<Program> doInBackground(Void... params) {

                FallList<Program> programs = null;
                try {
                    programs = SearchService.getInstance().getRecommandedProgram();
                } catch (LiveHttpException e) {

                }

                return programs.getList();
            }

            @Override
            protected void onPostExecute(java.util.List<Program> programs) {
                mContainer.refreshData(programs, true);
            }

        };

        task.execute();
    }
    
}
