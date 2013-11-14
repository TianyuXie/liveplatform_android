package com.pplive.liveplatform.ui.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.db.CacheManager;
import com.pplive.liveplatform.ui.widget.attr.IHidable;

public class SearchBar extends LinearLayout implements IHidable {
    final static String TAG = "SearchBar";

    private final static String LIST_ITEM_KEY = "ItemTitle";

    private ListView mRecordListView;

    private Button mCloseButton;

    private CacheManager mCacheManager;

    private EditText mSearchEditText;

    private ViewGroup mRoot;

    private boolean mShowing;

    private List<String> mRecords;

    private List<Map<String, Object>> mRecordItems;

    private SimpleAdapter mRecordItemAdapter;

    public SearchBar(Context context) {
        this(context, null);
    }

    public SearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCacheManager = CacheManager.getInstance(context);
        mRecordItems = new ArrayList<Map<String, Object>>();
        mRecords = new ArrayList<String>();

        LayoutInflater inflater = LayoutInflater.from(context);
        mRoot = (ViewGroup) inflater.inflate(R.layout.widget_searchbar, this);
        mCloseButton = (Button) mRoot.findViewById(R.id.btn_searchbar_close);

        mSearchEditText = (EditText) mRoot.findViewById(R.id.edittext_searchbar);
        mSearchEditText.setOnKeyListener(searchOnKeyListener);
        mSearchEditText.setOnFocusChangeListener(onFocusChangeListener);

        mRecordListView = (ListView) mRoot.findViewById(R.id.listview_searchbar_records);
        mRecordItemAdapter = new SimpleAdapter(context, mRecordItems, R.layout.layout_searchbar_listitem, new String[] { LIST_ITEM_KEY },
                new int[] { R.id.textview_searchbar_item });
        mRecordListView.setAdapter(mRecordItemAdapter);
        mRecordListView.setOnItemClickListener(onItemClickListener);
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
                if (!TextUtils.isEmpty(keyword)) {
                    mCacheManager.updateCache(keyword);
                }
                return true;
            }
            return false;
        }
    };

    private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mRecordItems.clear();
                mRecords.clear();
                mRecords.addAll(mCacheManager.getSearchCache(5));
                for (String record : mRecords) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(LIST_ITEM_KEY, record);
                    mRecordItems.add(map);
                }
                mRecordItemAdapter.notifyDataSetChanged();
                mRecordListView.setVisibility(VISIBLE);
            } else {
                mRecordListView.setVisibility(INVISIBLE);
            }
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position < mRecords.size()) {
                String text = mRecords.get(position);
                if (!TextUtils.isEmpty(text)) {
                    mSearchEditText.setText(text);
                }
            }
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
