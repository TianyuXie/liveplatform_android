package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.util.DisplayUtil;

public class AnimDoor extends RelativeLayout {
    private ViewGroup mRoot;

    private ImageView mLeftDoorImageView;

    private ImageView mRightDoorImageView;

    private float mAnimX;

    public AnimDoor(Context context) {
        this(context, null);
    }

    public AnimDoor(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        mRoot = (ViewGroup) inflater.inflate(R.layout.widget_animdoor, this);
        mLeftDoorImageView = (ImageView) mRoot.findViewById(R.id.image_animdoor_left);
        mRightDoorImageView = (ImageView) mRoot.findViewById(R.id.image_animdoor_right);
    }

    public void setDoorResource(int left, int right) {
        setDoorDrawable(getContext().getResources().getDrawable(left), getContext().getResources().getDrawable(right));
    }

    public void setDoorDrawable(Drawable left, Drawable right) {
        mLeftDoorImageView.setImageDrawable(left);
        mRightDoorImageView.setImageDrawable(right);
    }

    public void setFactor(float factor) {
        ViewGroup.LayoutParams llp = mLeftDoorImageView.getLayoutParams();
        ViewGroup.LayoutParams rlp = mRightDoorImageView.getLayoutParams();
        mAnimX = rlp.width = llp.width = (int) Math.round(DisplayUtil.getWidthPx(getContext()) / 2.0 * factor);
        mLeftDoorImageView.requestLayout();
        mRightDoorImageView.requestLayout();
    }
}
