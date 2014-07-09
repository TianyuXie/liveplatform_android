package com.pplive.liveplatform.adapter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pplive.android.image.RoundedImageView;
import com.pplive.android.pulltorefresh.RefreshAdapter;
import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.FollowAPI;
import com.pplive.liveplatform.core.api.live.model.User;
import com.pplive.liveplatform.ui.UserpageActivity;

public class UserAdapter extends RefreshAdapter<User> {

    static final String TAG = UserAdapter.class.getSimpleName();

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
            holder.btnFollow = (ImageButton) convertView.findViewById(R.id.btn_follow);

            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();

        updateView(holder, position);

        return convertView;
    }

    private void updateView(ViewHolder holder, int position) {
        final User user = getItem(position);
        holder.imageUserIcon.setImageAsync(user.getIcon());
        holder.textNickname.setText(user.getDisplayName());
        holder.imageUserIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, UserpageActivity.class);
                intent.putExtra(Extra.KEY_USERNAME, user.getUsername());
                intent.putExtra(Extra.KEY_ICON_URL, user.getIcon());
                intent.putExtra(Extra.KEY_NICKNAME, user.getDisplayName());
                mContext.startActivity(intent);
            }
        });

        holder.btnFollow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (R.id.btn_follow == v.getId()) {
                    boolean selected = v.isSelected();
                    if (!selected) {
                        ImageButton btn = (ImageButton) v;

                        btn.setSelected(true);

                        follow(user);
                    }
                }
            }
        });

        int relation = user.getRelation();
        if (1 == relation || 2 == relation) {
            holder.btnFollow.setVisibility(View.VISIBLE);
            holder.btnFollow.setSelected(true);
        } else if (0 == relation) {
            holder.btnFollow.setVisibility(View.VISIBLE);
            holder.btnFollow.setSelected(false);
        } else {
            holder.btnFollow.setVisibility(View.GONE);
        }

    }

    private void follow(final User user) {
        UserManager manager = UserManager.getInstance(mContext);
        if (manager.isLogin()) {
            user.setRelation(1);

            final String coToken = manager.getToken();
            final String username = manager.getUsernamePlain();

            AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected Boolean doInBackground(Void... params) {

                    try {
                        return FollowAPI.getInstance().follow(coToken, username, user.getId());

                    } catch (LiveHttpException e) {

                        Log.w(TAG, e.toString());
                    }

                    return false;
                }

            };

            task.execute();
        }
    }

    static class ViewHolder {

        RoundedImageView imageUserIcon;
        TextView textNickname;
        ImageButton btnFollow;
    }
}
