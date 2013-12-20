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
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.ProgramService;
import com.pplive.liveplatform.core.service.live.model.LiveStatusEnum;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.ui.record.event.EventProgramAdded;
import com.pplive.liveplatform.ui.record.event.EventProgramDeleted;
import com.pplive.liveplatform.ui.widget.HorizontalListView;

import de.greenrobot.event.EventBus;

public class LiveListView extends HorizontalListView implements OnItemClickListener {

    private static final String TAG = LiveListView.class.getSimpleName();

    private LiveListAdapter mLiveListAdapter;

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

            EventBus.getDefault().register(this);
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
                liveItem.setProgram(getItem(position));
            }

            return convertView;
        }

        public void onEvent(EventProgramDeleted event) {
            final Program program = event.getObject();

            for (int i = 0, len = mPreLiveList.size(); i < len; ++i) {
                Program p = mPreLiveList.get(i);
                if (p.getId() == program.getId()) {
                    mPreLiveList.remove(i);
                    notifyDataSetChanged();
                    break;
                }
            }

            AsyncTask<Void, Void, Boolean> delTask = new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected Boolean doInBackground(Void... params) {
                    String token = UserManager.getInstance(getContext()).getToken();
                    try {
                        
                        return ProgramService.getInstance().deleteProgramById(token, program.getId());
                    } catch (LiveHttpException e) {
                        
                    }
                    
                    return false;
                }
            };

            delTask.execute();
        }

        public void onEvent(EventProgramAdded event) {
            final Program program = event.getObject();

            if (null != program) {
                mPreLiveList.add(0, program);
                notifyDataSetChanged();
            }
        }

        class GetProgramsTask extends AsyncTask<Void, Void, List<Program>> {

            @Override
            protected List<Program> doInBackground(Void... params) {
                Log.d(TAG, "doInBackground");
                String username = UserManager.getInstance(getContext()).getUsernamePlain();
                
                try {
                    List<Program> programs = ProgramService.getInstance().getProgramsByOwner(username, LiveStatusEnum.NOT_START);
    
                    return programs;
                } catch (LiveHttpException e) {
                    
                }
                
                return null;
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
