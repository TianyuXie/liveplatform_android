package com.pplive.liveplatform.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.util.DisplayUtil;
import com.pplive.liveplatform.util.TimeHelper;
import com.pplive.liveplatform.util.TimeUtil;
import com.pplive.liveplatform.widget.image.AsyncImageView;

public class ProgramAdapter extends BaseAdapter implements LoadDataInterface<Program> {

    static final String TAG = ProgramAdapter.class.getSimpleName();

    static final float RATIO = 4f / 3f;

    private Context mContext;

    private LayoutInflater mInflater;

    private int mHeight;

    private List<Program> mPrograms = new ArrayList<Program>();

    public ProgramAdapter(Context context) {
        Log.d(TAG, "ProgramAdpater");

        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        mHeight = (int) (DisplayUtil.getWidthPx(context) / 2.0f / RATIO);
    }

    @Override
    public void refreshData(Collection<Program> programs) {
        Log.d(TAG, "refreshData");

        mPrograms.clear();
        mPrograms.addAll(programs);

        notifyDataSetChanged();
    }

    @Override
    public void appendData(Collection<Program> programs) {
        Log.d(TAG, "appendData");

        mPrograms.addAll(programs);

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        return mPrograms.size();
    }

    @Override
    public Program getItem(int position) {
        return mPrograms.get(position);
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
            holder.imagePreview = (AsyncImageView) convertView.findViewById(R.id.image_program_preview);
            holder.textTimeDown = (TextView) convertView.findViewById(R.id.text_program_timedown);
            holder.textTitle = (TextView) convertView.findViewById(R.id.text_program_title);
            holder.textTags = (TextView) convertView.findViewById(R.id.text_program_tags);
            holder.textCount = (TextView) convertView.findViewById(R.id.text_program_viewcount);
            holder.imageLive = (ImageView) convertView.findViewById(R.id.image_live);
            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();

        updateView(holder, mPrograms.get(position));
        return convertView;
    }

    private void updateView(ViewHolder holder, Program data) {
        ViewGroup.LayoutParams lp = holder.imagePreview.getLayoutParams();
        lp.height = mHeight;

        holder.textTags.setText(data.getTags());
        holder.textTitle.setText(data.getTitle());
        holder.textCount.setText(String.valueOf(data.getViews()));
        holder.imagePreview.setImageAsync(data.getRecommendCover(), R.drawable.program_default_image);

        if (data.isPrelive()) {
            holder.textTimeDown.setVisibility(View.VISIBLE);
            holder.textTimeDown.setCompoundDrawablesWithIntrinsicBounds(R.drawable.program_coming_icon, 0, 0, 0);
            holder.textTimeDown.setText(TimeUtil.stringForCountdown(data.getStartTime() - System.currentTimeMillis()));
            holder.imageLive.setVisibility(View.GONE);
        } else if (data.isVOD()) {
            holder.textTimeDown.setVisibility(View.VISIBLE);
            holder.textTimeDown.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.textTimeDown.setText(TimeHelper.getAboutStartTime(mContext, data.getRealStartTime()));
            holder.imageLive.setVisibility(View.GONE);
        } else {
            holder.imageLive.setVisibility(View.VISIBLE);
            holder.textTimeDown.setVisibility(View.GONE);
        }
    }

    static class ViewHolder {

        AsyncImageView imagePreview;

        TextView textTimeDown;

        TextView textTitle;

        TextView textTags;

        TextView textCount;

        ImageView imageLive;
    }

}
