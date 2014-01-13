package com.pplive.liveplatform.ui.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.cache.CacheManager;

public class SearchBar extends LinearLayout {
    final static String TAG = "_SearchBar";

    private final static String LIST_ITEM_KEY = "ItemTitle";

    private Button mCloseButton;

    private Button mSearchButton;

    private CacheManager mCacheManager;

    private EnterSendEditText mSearchEditText;

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
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.widget_searchbar, this);
        mCloseButton = (Button) root.findViewById(R.id.btn_searchbar_close);
        mSearchButton = (Button) root.findViewById(R.id.btn_searchbar_search);

        mSearchEditText = (EnterSendEditText) root.findViewById(R.id.edit_searchbar);
        mSearchEditText.setOnEnterListener(onEnterListener);
        mSearchEditText.setOnFocusChangeListener(onFocusChangeListener);

        ListView mRecordListView = (ListView) root.findViewById(R.id.list_searchbar_records);
        mRecordItemAdapter = new SimpleAdapter(context, mRecordItems, R.layout.layout_searchbar_item, new String[] { LIST_ITEM_KEY },
                new int[] { R.id.text_searchbar_item });
        mRecordListView.setAdapter(mRecordItemAdapter);
        mRecordListView.setOnItemClickListener(onItemClickListener);

        findViewById(R.id.btn_searchbar_clear).setOnClickListener(onClearBtnClickListener);
    }

    private EnterSendEditText.OnEnterListener onEnterListener = new EnterSendEditText.OnEnterListener() {
        @Override
        public boolean onEnter(View v) {
            mCacheManager.updateCache(mSearchEditText.getText().toString());
            mSearchButton.performClick();
            return true;
        }
    };

    public void forcusEditText() {
        mSearchEditText.requestFocus();
    }

    public String getText() {
        return mSearchEditText.getEditableText().toString();
    }

    public void clearText() {
        mSearchEditText.setText("");
    }

    private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                Log.d(TAG, "onFocusChange: get focus");
                mRecordItems.clear();
                mRecords.clear();
                mRecords.addAll(mCacheManager.getSearchCache(5));
                for (String record : mRecords) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(LIST_ITEM_KEY, record);
                    mRecordItems.add(map);
                }
                updateRecordList();
                findViewById(R.id.layout_searchbar_records).setVisibility(VISIBLE);
                if (mCallbackListener != null) {
                    mCallbackListener.onShowRecord(true);
                }
            } else {
                hideRecordList();
            }
        }
    };

    private void hideRecordList() {
        findViewById(R.id.layout_searchbar_records).setVisibility(GONE);
        if (mCallbackListener != null) {
            mCallbackListener.onShowRecord(false);
        }
    }

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

    private View.OnClickListener onClearBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCacheManager.clearSearchCache();
            mRecordItems.clear();
            mRecords.clear();
            updateRecordList();
        }
    };

    private void updateRecordList() {
        if (mRecordItems.isEmpty()) {
            findViewById(R.id.btn_searchbar_clear).setVisibility(View.GONE);
        } else {
            findViewById(R.id.btn_searchbar_clear).setVisibility(View.VISIBLE);
        }
        mRecordItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        mCloseButton.setOnClickListener(l);
        mSearchButton.setOnClickListener(l);
    }

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        mSearchEditText.setFocusable(focusable);
        mSearchEditText.setFocusableInTouchMode(focusable);
    }

    @Override
    public void clearFocus() {
        Log.d(TAG, "clearFocus");
        super.clearFocus();
        mSearchEditText.clearFocus();
    }

    public interface Callback {
        public void onShowRecord(boolean status);
    }

    private Callback mCallbackListener;

    public void setCallbackListener(Callback listener) {
        this.mCallbackListener = listener;
    }
}
