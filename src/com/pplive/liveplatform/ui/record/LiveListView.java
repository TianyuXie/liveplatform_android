package com.pplive.liveplatform.ui.record;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.rest.model.Program;
import com.pplive.liveplatform.core.rest.service.ProgramService;
import com.pplive.liveplatform.ui.widget.HorizontalListView;

public class LiveListView extends HorizontalListView implements OnItemClickListener {

    private static final String TAG = LiveListView.class.getSimpleName();

    private LiveListAdapter mLiveListAdapter;
    private OnLiveSelectedListener mOnLiveSelectedListener;

    private int mSelectedItemPosition = -1;
    private LiveListItemView mSelectedLiveItem;

    public LiveListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if (View.VISIBLE == visibility) {
            mLiveListAdapter.refresh();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        
        mLiveListAdapter = new LiveListAdapter(getContext());
        super.setAdapter(mLiveListAdapter);
        super.setOnItemClickListener(this);
    }

    public void setOnLiveSelectedListener(OnLiveSelectedListener listener) {
        mOnLiveSelectedListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick position: " + position + "; id: " + id);

        if (position == mSelectedItemPosition) {
            return;
        }

        if (null != mSelectedLiveItem) {
            mSelectedLiveItem.setSelected(false);
        }

        mSelectedItemPosition = position;
        mSelectedLiveItem = (LiveListItemView) view;
        mSelectedLiveItem.setSelected(true);
    }

    class LiveListAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        private List<Program> mPreLiveList;

        private GetProgramsTask mGetProgramsTask;

        public LiveListAdapter(Context context) {
            mInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        }

        public void refresh() {
            Log.d(TAG, "Refresh 1");
            if (null == mGetProgramsTask) {
                Log.d(TAG, "Refresh 2");
                mGetProgramsTask = new GetProgramsTask();
                mGetProgramsTask.execute();
            }
        }

        @Override
        public int getCount() {
            return mPreLiveList == null ? 0 : mPreLiveList.size();
        }

        @Override
        public Program getItem(int position) {
            return mPreLiveList == null ? null : mPreLiveList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.layout_live_itemview, null);
            }

            if (convertView instanceof LiveListItemView) {
                LiveListItemView liveItem = (LiveListItemView) convertView;
                liveItem.setOnLiveSelectedListener(mOnLiveSelectedListener);
                liveItem.setOnLiveDeletedListener(new OnLiveDeletedListener() {
                    
                    @Override
                    public void onLiveDeleted(final Program program) {
                        for (int i = 0, len = mPreLiveList.size(); i < len; ++i) {
                            Program p = mPreLiveList.get(i);
                            if (p.getId() == program.getId()) {
                                mPreLiveList.remove(i);
                                notifyDataSetChanged();
                                break;
                            }
                        }
                        
                        AsyncTask<Void, Void, Void> delTask = new AsyncTask<Void, Void, Void>() {
                            
                            @Override
                            protected Void doInBackground(Void... params) {
                                
                                ProgramService.getInstance().deleteProgramById(program.getId());
                                
                                return null;
                            }
                        };
                        
                        delTask.execute();
                    }
                });
                liveItem.setProgram(getItem(position));
            }

            return convertView;
        }

        class GetProgramsTask extends AsyncTask<Void, Void, List<Program>> {

            @Override
            protected List<Program> doInBackground(Void... params) {
                Log.d(TAG, "doInBackground");

                List<Program> programs = ProgramService.getInstance().getProgramsByOwner("xiety0001");

                return programs;
            }

            protected void onPostExecute(java.util.List<Program> result) {
                Log.d(TAG, "onPostExecute");

                if (null != result) {
                    mPreLiveList = result;
                    notifyDataSetChanged();
                }

                mGetProgramsTask = null;
            };
        }
    }

    interface OnLiveDeletedListener {
        void onLiveDeleted(Program program);
    }
}
