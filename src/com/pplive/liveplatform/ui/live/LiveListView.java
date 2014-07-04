package com.pplive.liveplatform.ui.live;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
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
import com.pplive.liveplatform.core.alarm.AlarmCenter;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.ProgramAPI;
import com.pplive.liveplatform.core.api.live.model.LiveStatusEnum;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.dialog.DialogManager;
import com.pplive.liveplatform.ui.live.event.EventProgramAdded;
import com.pplive.liveplatform.ui.live.event.EventProgramDeleted;
import com.pplive.liveplatform.ui.live.event.EventProgramSelected;
import com.pplive.liveplatform.widget.HorizontalListView;

import de.greenrobot.event.EventBus;

public class LiveListView extends HorizontalListView implements OnItemClickListener {

    private static final String TAG = LiveListView.class.getSimpleName();

    private LiveListAdapter mLiveListAdapter;

    public LiveListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if (View.VISIBLE != visibility) {
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
    public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
        Log.d(TAG, "onItemClick position: " + position + "; id: " + id);
        
        DialogManager.alertPrelive(getContext(), new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Program program = mLiveListAdapter.getItem(position);
                
                EventBus.getDefault().post(new EventProgramSelected(program));
            }
        }, null).show();
    }
    
    public void onEvent(EventProgramDeleted event) {
        final Program program = event.getObject();
        for (int i = 0, len = mLiveListAdapter.getCount(); i < len; ++i) {
            Program p = mLiveListAdapter.getItem(i);
            if (p.getId() == program.getId()) {
                mLiveListAdapter.removeItem(i);
                break;
            }
        }
        AlarmCenter.getInstance(getContext()).deletePrelive(program.getId());

        AsyncTask<Void, Void, Boolean> delTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                String token = UserManager.getInstance(getContext()).getToken();
                try {
                    return ProgramAPI.getInstance().deleteProgramById(token, program.getId());
                } catch (LiveHttpException e) {
                    
                }
                
                return false;
            }
        };

        delTask.execute();
    }

    public void onEvent(EventProgramAdded event) {
        final Program program = event.getObject();
        AlarmCenter.getInstance(getContext()).addPrelive(program);
        if (null != program) {
            mLiveListAdapter.addItem(0 /* position */ , program);
        }
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
        
        void addItem(int position, Program program) {
            if (null != mPreLiveList) {
                mPreLiveList.add(position, program);
                notifyDataSetChanged();
            }
        }
        
        void removeItem(int position) {
            if (null != mPreLiveList) {
                mPreLiveList.remove(position);
                notifyDataSetChanged();
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
                convertView = mInflater.inflate(R.layout.item_live_program, null);
            }

            if (convertView instanceof LiveListItemView) {
                LiveListItemView liveItem = (LiveListItemView) convertView;
                liveItem.setSelected(false);
                liveItem.setProgram(getItem(position));
            }

            return convertView;
        }

        class GetProgramsTask extends AsyncTask<Void, Void, List<Program>> {

            @Override
            protected List<Program> doInBackground(Void... params) {
                Log.d(TAG, "doInBackground");
                String username = UserManager.getInstance(getContext()).getUsernamePlain();
                String token = UserManager.getInstance(getContext()).getToken();
                
                try {
                    List<Program> programs = ProgramAPI.getInstance().getProgramsByOwner(token, username, LiveStatusEnum.NOT_START);
    
                    return programs;
                } catch (LiveHttpException e) {
                    
                }
                
                return null;
            }

            protected void onPostExecute(List<Program> programs) {
                Log.d(TAG, "onPostExecute");

                if (null != programs) {
                    
                    Collections.sort(programs, new Comparator<Program> () {
                        
                        @Override
                        public int compare(Program program1, Program program2) {
                            
                            return (int)(program1.getStartTime() - program2.getStartTime());
                        }
                    });
                    
                    
                    mPreLiveList = programs;
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
