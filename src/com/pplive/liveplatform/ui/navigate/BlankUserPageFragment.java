package com.pplive.liveplatform.ui.navigate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.LoginActivity;
import com.pplive.liveplatform.ui.RegisterActivity;

public class BlankUserPageFragment extends Fragment {

    private ImageButton mBtnLogin;

    private ImageButton mBtnRegister;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_blank_userpage, container, false);

        mBtnLogin = (ImageButton) layout.findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);

            }
        });

        mBtnRegister = (ImageButton) layout.findViewById(R.id.btn_register);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                getActivity().startActivity(intent);

            }
        });

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
