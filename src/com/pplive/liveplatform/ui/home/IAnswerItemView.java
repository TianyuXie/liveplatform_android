package com.pplive.liveplatform.ui.home;

import android.content.Context;
import android.view.View;

public interface IAnswerItemView {

    public abstract View getView(Context context);

    public abstract void setData(Object data);
}
