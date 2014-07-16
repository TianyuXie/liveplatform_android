package com.pplive.liveplatform.adapter;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.pplive.android.image.AsyncImageView;
import com.pplive.android.pulltorefresh.RefreshAdapter;
import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.dialog.DialogManager;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.user.RemoveProgramTask;
import com.pplive.liveplatform.util.DisplayUtil;
import com.pplive.liveplatform.util.TimeHelper;

public class PersonalProgramAdapter extends RefreshAdapter<Program> {

    static final String TAG = PersonalProgramAdapter.class.getSimpleName();

    private Context mContext;

    private LayoutInflater mInflater;

    private int mWidth;

    private int mHeight;

    public PersonalProgramAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        mWidth = (int) (DisplayUtil.getWidthPx(context) * 0.4f);
        mHeight = (int) (mWidth * 3f / 4f);
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

        updateView(position, convertView, parent);

        return convertView;
    }

    private void updateView(final int position, final View convertView, final ViewGroup parent) {

        ViewHolder holder = (ViewHolder) convertView.getTag();
        final Program program = getItem(position);

        holder.textTitle.setText(program.getTitle());
        holder.imagePreview.setImageAsync(program.getRecommendCover());
        holder.textTime.setText(TimeHelper.getAboutStartTime(mContext.getResources(), program.getStartTime()));
        holder.imageLive.setVisibility(program.isLiving() ? View.VISIBLE : View.GONE);
        holder.textTags.setText(program.getTags());
        holder.textViewer.setText(String.valueOf(program.getViewers()));

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                UserManager manager = UserManager.getInstance(mContext);

                if (manager.isLogin(program.getOwner())) {
                    Dialog dialog = DialogManager.alertDeleteDialog(mContext, program.getTitle(), new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            long pid = program.getId();

                            RemoveProgramTask task = new RemoveProgramTask();
                            task.addTaskListener(new Task.BaseTaskListener() {
                            });

                            TaskContext taskContext = new TaskContext();
                            taskContext.set(Extra.KEY_TOKEN, UserManager.getInstance(mContext).getToken());
                            taskContext.set(Extra.KEY_PROGRAM_ID, pid);

                            task.execute(taskContext);

                            //                            mProgramContainer.closeOpenedItem();

                            if (parent instanceof SwipeListView) {

                                SwipeListView swipe = (SwipeListView) parent;
                                swipe.closeOpenedItems();

                            }

                            remove(position);
                        }
                    });
                    dialog.show();
                }
            }
        });
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
