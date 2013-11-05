package com.pplive.liveplatform.ui.homepage;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

public interface IAnswerView {

    public abstract View getView(Context context);

    public abstract void setData(Object data);

    public abstract void addChildView(Context context, LinearLayout parentLayout);
}
