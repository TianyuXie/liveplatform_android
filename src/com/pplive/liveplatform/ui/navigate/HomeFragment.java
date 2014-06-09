package com.pplive.liveplatform.ui.navigate;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.SearchService;
import com.pplive.liveplatform.core.service.live.model.FallList;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.ui.LivePlayerActivity;
import com.pplive.liveplatform.ui.widget.image.AsyncImageView;
import com.pplive.liveplatform.util.DisplayUtil;
import com.pplive.liveplatform.util.TimeHelper;
import com.pplive.liveplatform.util.TimeUtil;

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

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        GridLayoutAnimationController glac = (GridLayoutAnimationController) AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.home_gridview_flyin);
        mContainer.getRefreshableView().setLayoutAnimation(glac);

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

class ProgramAdapter extends BaseAdapter {

    static final String TAG = ProgramAdapter.class.getSimpleName();

    static final float RATIO = 4f / 3f;

    private Context mContext;

    private LayoutInflater mInflater;

    private int mHeight;

    private List<Program> mPrograms;

    public ProgramAdapter(Context context) {
        Log.d(TAG, "ProgramAdpater");

        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        mHeight = (int) (DisplayUtil.getWidthPx(context) / 2.0f / RATIO);
    }

    public void refreshData(List<Program> data) {
        Log.d(TAG, "refreshData");

        mPrograms = data;
        notifyDataSetChanged();
    }

    public void appendData(List<Program> data) {
        Log.d(TAG, "refreshData");

        if (null != mPrograms) {
            mPrograms.addAll(data);
        } else {
            mPrograms = data;
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int count = null != mPrograms ? mPrograms.size() : 0;

        Log.d(TAG, "getCount: " + count);

        return count;
    }

    @Override
    public Program getItem(int position) {
        return null != mPrograms ? mPrograms.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        Log.d(TAG, "getItemId");
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView position: " + position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_program, null);
            ViewHolder holder = new ViewHolder();
            holder.previewImageView = (AsyncImageView) convertView.findViewById(R.id.image_program_preview);
            holder.timedownTextView = (TextView) convertView.findViewById(R.id.text_program_timedown);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.text_program_title);
            holder.ownerTextView = (TextView) convertView.findViewById(R.id.text_program_owner);
            holder.viewcountTextView = (TextView) convertView.findViewById(R.id.text_program_viewcount);
            holder.liveImageView = (ImageView) convertView.findViewById(R.id.image_live);
            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();

        updateView(holder, mPrograms.get(position));
        return convertView;
    }

    private void updateView(ViewHolder holder, Program data) {
        ViewGroup.LayoutParams lp = holder.previewImageView.getLayoutParams();
        lp.height = mHeight;

        holder.ownerTextView.setText(data.getOwnerNickname());
        holder.titleTextView.setText(data.getTitle());
        holder.viewcountTextView.setText(String.valueOf(data.getViews()));
        holder.previewImageView.setImageAsync(data.getRecommendCover(), R.drawable.program_default_image);

        if (data.isPrelive()) {
            holder.timedownTextView.setVisibility(View.VISIBLE);
            holder.timedownTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.program_coming_icon, 0, 0, 0);
            holder.timedownTextView.setText(TimeUtil.stringForCountdown(data.getStartTime() - System.currentTimeMillis()));
            holder.liveImageView.setVisibility(View.GONE);
        } else if (data.isVOD()) {
            holder.timedownTextView.setVisibility(View.VISIBLE);
            holder.timedownTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.timedownTextView.setText(TimeHelper.getAboutStartTime(mContext, data.getRealStartTime()));
            holder.liveImageView.setVisibility(View.GONE);
        } else {
            holder.liveImageView.setVisibility(View.VISIBLE);
            holder.timedownTextView.setVisibility(View.GONE);
        }
    }

    static class ViewHolder {

        AsyncImageView previewImageView;

        TextView timedownTextView;

        TextView titleTextView;

        TextView ownerTextView;

        TextView viewcountTextView;

        ImageView liveImageView;
    }

}
