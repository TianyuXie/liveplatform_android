package com.pplive.liveplatform.ui.record;

import android.app.Service;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.widget.HorizontalListView;
import com.pplive.liveplatform.util.ViewUtil;

public class LiveListView extends HorizontalListView implements OnItemSelectedListener {

    private static final String TAG = LiveListView.class.getSimpleName();

    private int mSelectedItemPosition = -1;
    private LiveListItemView mSelectedLiveItem;

    public LiveListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        super.setAdapter(new LiveListAdapter(getContext()));
        super.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected position: " + position + "; id: " + id);

        if (position == mSelectedItemPosition) {
            return;
        }

        if (mSelectedItemPosition >= 0) {
            mSelectedLiveItem.setSelected(false);
        }

        mSelectedItemPosition = position;
        mSelectedLiveItem = (LiveListItemView) view;
        mSelectedLiveItem.setSelected(true);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    class LiveListAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public LiveListAdapter(Context context) {
            mInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 10;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.layout_live_itemview, null);

            } else {
                ((LiveListItemView)convertView).reset();
            }

            return convertView;
        }

    }

    interface OnLiveSelectedListener {
        void onSelected();
    }
}
