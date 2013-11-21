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
    private LiveListView.ViewHolder mSelectedViewHolder;

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
            mSelectedViewHolder.setSelected(false);
        }

        mSelectedItemPosition = position;
        mSelectedViewHolder = (ViewHolder) view.getTag();
        mSelectedViewHolder.setSelected(true);
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

                final ViewHolder holder = new ViewHolder();
                holder.mImagePrelive = (ImageView) convertView.findViewById(R.id.image_prelive);
                holder.mBtnPreliveDelete = (ImageButton) convertView.findViewById(R.id.btn_prelive_delete);
                holder.mBtnDelete = (ImageButton) convertView.findViewById(R.id.btn_delete);
                holder.mTextLiveTitle = (TextView) convertView.findViewById(R.id.text_live_title);
                
                holder.mPosition = position;
                
                holder.mBtnPreliveDelete.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        
                        holder.showOrHideDeleteBtn();
                    }
                });
                
                holder.mBtnDelete.setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClickBtnDelete");
                    }
                });

                convertView.setTag(holder);
            } else {
                final ViewHolder holder = (ViewHolder) convertView.getTag();
                holder.mPosition = position;
                holder.reset();
            }

            return convertView;
        }

    }

    static class ViewHolder {
        int mPosition;

        ImageView mImagePrelive;
        ImageButton mBtnPreliveDelete;
        ImageButton mBtnDelete;
        TextView mTextLiveTitle;

        void setSelected(boolean selected) {
            if (selected) {
                mBtnPreliveDelete.setVisibility(View.VISIBLE);
            } else {
                reset();
            }
        }

        void showOrHideDeleteBtn() {
            boolean selected = mBtnPreliveDelete.isSelected();

            mBtnPreliveDelete.setSelected(!selected);

            ViewUtil.showOrHide(mBtnDelete, false);
        }

        void reset() {
            mBtnPreliveDelete.setSelected(false);
            mBtnPreliveDelete.setVisibility(View.INVISIBLE);
            mBtnDelete.setVisibility(View.INVISIBLE);
        }
    }
    
    interface OnLiveSelectedListener {
        void onSelected();
    }
}
