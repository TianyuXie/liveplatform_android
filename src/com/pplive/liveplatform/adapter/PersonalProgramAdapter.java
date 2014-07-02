package com.pplive.liveplatform.adapter;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pplive.android.image.AsyncImageView;
import com.pplive.android.pulltorefresh.RefreshAdapter;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.util.DisplayUtil;
import com.pplive.liveplatform.util.TimeHelper;

public class PersonalProgramAdapter extends RefreshAdapter<Program> {

    private Context mContext;

    private LayoutInflater mInflater;

    private OnItemDeleteListener mOnItemDeleteListener;

    private int mWidth;

    private int mHeight;

    public PersonalProgramAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        mWidth = (int) (DisplayUtil.getWidthPx(context) * 0.4f);
        mHeight = (int) (mWidth * 3f / 4f);
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        mOnItemDeleteListener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_personal_program, parent, false);

            holder = new ViewHolder();
            holder.imagePreview = (AsyncImageView) convertView.findViewById(R.id.image_program_preview);
            holder.textTitle = (TextView) convertView.findViewById(R.id.text_program_title);
            holder.textViewer = (TextView) convertView.findViewById(R.id.text_program_viewer);
            holder.textTime = (TextView) convertView.findViewById(R.id.text_date);
            holder.imageLive = (ImageView) convertView.findViewById(R.id.image_live);
            holder.textTags = (TextView) convertView.findViewById(R.id.text_program_tags);
            holder.btnDelete = (Button) convertView.findViewById(R.id.btn_delete);

            ViewGroup.LayoutParams lp = holder.imagePreview.getLayoutParams();
            lp.width = mWidth;
            lp.height = mHeight;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != mOnItemDeleteListener) {
                    mOnItemDeleteListener.onDelete(position);
                }
            }
        });

        updateView(holder, getItem(position));

        return convertView;
    }

    private void updateView(ViewHolder holder, Program data) {
        holder.textTitle.setText(data.getTitle());
        holder.imagePreview.setImageAsync(data.getRecommendCover(), R.drawable.program_default_image);
        holder.textTime.setText(TimeHelper.getAboutStartTime(mContext.getResources(), data.getStartTime()));
        holder.imageLive.setVisibility(data.isLiving() ? View.VISIBLE : View.GONE);
        holder.textTags.setText(data.getTags());
        holder.textViewer.setText(String.valueOf(data.getViewers()));
    }

    public interface OnItemDeleteListener {
        void onDelete(int position);
    }

    static class ViewHolder {
        AsyncImageView imagePreview;

        TextView textTime;

        TextView textTitle;

        TextView textViewer;

        TextView textTags;

        ImageView imageLive;

        Button btnDelete;
    }

}
