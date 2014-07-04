package com.pplive.liveplatform.adapter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pplive.android.image.RoundedImageView;
import com.pplive.android.pulltorefresh.RefreshAdapter;
import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.api.live.model.User;
import com.pplive.liveplatform.ui.UserpageActivity;

public class UserAdapter extends RefreshAdapter<User> {

    private Context mContext;
    private LayoutInflater mInflater;

    public UserAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.item_user, parent, false);

            holder = new ViewHolder();
            holder.imageUserIcon = (RoundedImageView) convertView.findViewById(R.id.image_user_icon);
            holder.textNickname = (TextView) convertView.findViewById(R.id.text_nickname);

            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();

        updateView(holder, getItem(position));

        holder.imageUserIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                User user = getItem(position);

                Intent intent = new Intent(mContext, UserpageActivity.class);
                intent.putExtra(Extra.KEY_LOGIN_NAME, user.getUsername());
                intent.putExtra(Extra.KEY_ICON_URL, user.getIcon());
                intent.putExtra(Extra.KEY_NICKNAME, user.getDisplayName());
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    private void updateView(ViewHolder holder, User user) {
        holder.imageUserIcon.setImageAsync(user.getIcon());
        holder.textNickname.setText(user.getDisplayName());
    }

    static class ViewHolder {

        RoundedImageView imageUserIcon;
        TextView textNickname;
    }
}
