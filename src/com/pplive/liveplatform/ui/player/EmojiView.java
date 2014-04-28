package com.pplive.liveplatform.ui.player;

import android.app.Service;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.pplive.liveplatform.R;

public class EmojiView extends GridView implements android.widget.AdapterView.OnItemClickListener {

    static final String TAG = EmojiView.class.getSimpleName();

    private EmojiAdapter mEmojiAdapter;

    private OnEmojiClickListener mOnEmojiClickListener;

    public EmojiView(Context context) {
        this(context, null);
    }

    public EmojiView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmojiView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mEmojiAdapter = new EmojiAdapter(context);
        this.setAdapter(mEmojiAdapter);
        this.setOnItemClickListener(this);
    }

    public void setOnEmojiClickListener(OnEmojiClickListener listener) {
        mOnEmojiClickListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.d(TAG, "onItemClick: " + mEmojiAdapter.getItem(position));

        if (null != mOnEmojiClickListener) {
            mOnEmojiClickListener.onClick(mEmojiAdapter.getItem(position));
        }

    }

    public static interface OnEmojiClickListener {

        void onClick(String emoji);
    }
}

class EmojiAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    public EmojiAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return Emoji.EMOJI_KEY_LIST.size();
    }

    @Override
    public String getItem(int position) {
        return "/" + Emoji.EMOJI_KEY_LIST.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.item_emoji, null);

            ViewHolder holder = new ViewHolder();
            holder.mImageView = (ImageView) convertView.findViewById(R.id.image_emoji_icon);
            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.mImageView.setImageResource(Emoji.EMOJI_ICON_LIST.get(position));

        return convertView;
    }

    static class ViewHolder {
        ImageView mImageView;
    }

}