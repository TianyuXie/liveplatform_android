package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.db.CacheManager;
import com.pplive.liveplatform.ui.widget.attr.IHidable;

public class SearchBar extends LinearLayout implements IHidable {
    private Button mCloseButton;

    private EditText mSearchEditText;

    private CacheManager mCacheManager;

    private ViewGroup mRoot;

    private boolean mShowing;

    public SearchBar(Context context) {
        this(context, null);
    }

    public SearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCacheManager = new CacheManager(context.getApplicationContext());
        LayoutInflater inflater = LayoutInflater.from(context);
        mRoot = (ViewGroup) inflater.inflate(R.layout.widget_searchbar, this);
        mCloseButton = (Button) mRoot.findViewById(R.id.btn_searchbar_close);
        mSearchEditText = (EditText) mRoot.findViewById(R.id.edittext_searchbar);
        mSearchEditText.setOnKeyListener(searchOnKeyListener);
        mShowing = (getVisibility() == VISIBLE);
    }

    private View.OnKeyListener searchOnKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                String keyword = mSearchEditText.getText().toString();
                Log.d("SearchBar", keyword);
                if (!TextUtils.isEmpty(keyword)) {
                    mCacheManager.updateCache(keyword);
                }
                return true;
            }
            return false;
        }
    };

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        mCloseButton.setOnClickListener(l);
    }

    @Override
    public void hide() {
        hide(true);
    }

    @Override
    public void hide(boolean gone) {
        if (mShowing) {
            mRoot.setVisibility(gone ? GONE : INVISIBLE);
            mShowing = false;
        }
    }

    @Override
    public void show() {
        if (!mShowing) {
            mRoot.setVisibility(VISIBLE);
            mShowing = true;
        }
    }

    @Override
    @Deprecated
    public void setVisibility(int visibility) {
        if (visibility == VISIBLE) {
            mShowing = true;
        } else {
            mShowing = false;
        }
        super.setVisibility(visibility);
    }

}
