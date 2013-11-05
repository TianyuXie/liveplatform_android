package com.pplive.liveplatform.ui.homepage.program;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;

import com.pplive.liveplatform.ui.homepage.IAnswerItemView;

public class ProgramItemView implements IAnswerItemView, View.OnClickListener {

    private JSONObject[] programObjects;

    @Override
    public View getView(Context context) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setData(Object data) {
        programObjects = (JSONObject[]) data;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

}
