package com.pplive.liveplatform.ui.home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public interface IAnswerView {

    public abstract View getView();

    public abstract void updateData(Object data);

    public abstract void addChildView(Context context, ViewGroup parentLayout);
}
