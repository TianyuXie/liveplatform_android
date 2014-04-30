package com.pplive.liveplatform.ui;

import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.cache.SearchCacheManager;
import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.SearchService;
import com.pplive.liveplatform.ui.widget.EnterSendEditText;

public class SearchActivity extends Activity {

    static final String TAG = SearchActivity.class.getSimpleName();

    private EnterSendEditText mEditSearchInput;

    private ImageButton mBtnClose;

    private ExpandableListView mExpandableListView;

    private SearchExpandableListAdapter mAdapter;

    private SearchCacheManager mCacheManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);

        mEditSearchInput = (EnterSendEditText) findViewById(R.id.search_input_bar_edit_view);
        mEditSearchInput.setOnEnterListener(new EnterSendEditText.OnEnterListener() {

            @Override
            public boolean onEnter(View v) {
                String keyword = mEditSearchInput.getText().toString().trim();

                search(keyword);

                return true;
            }
        });

        mBtnClose = (ImageButton) findViewById(R.id.search_input_bar_close_btn);
        mBtnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });

        mExpandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view);
        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                String keyword = mAdapter.getChild(groupPosition, childPosition);

                search(keyword);

                return true;
            }
        });

        mAdapter = new SearchExpandableListAdapter(getApplicationContext());
        mExpandableListView.setAdapter(mAdapter);
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        mExpandableListView.expandGroup(0);
        mExpandableListView.expandGroup(1);

        mCacheManager = SearchCacheManager.getInstance(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();

        AsyncTask<Void, Void, List<String>> task = new AsyncTask<Void, Void, List<String>>() {

            @Override
            protected List<String> doInBackground(Void... params) {

                List<String> searchKeyWords = null;
                try {
                    searchKeyWords = SearchService.getInstance().getSearchWordsList();
                } catch (LiveHttpException e) {
                    Log.w(TAG, e.toString());
                }

                return searchKeyWords;
            }

            @Override
            protected void onPostExecute(List<String> result) {

                if (null != result) {
                    mAdapter.setSearchKeyWords(result);
                }
            }
        };

        task.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAdapter.setSearchHistoryKeywords(mCacheManager.getSearchCache(5));

    }

    private void search(String keyword) {
        if (!TextUtils.isEmpty(keyword)) {

            mCacheManager.updateCache(keyword);

            Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
            intent.putExtra(SearchResultActivity.KEY_SEARCH_KEY_WORD, keyword);

            startActivity(intent);
        } else {

            Toast.makeText(this, R.string.search_keyword_empty, Toast.LENGTH_SHORT).show();
        }
    }

    class SearchExpandableListAdapter extends BaseExpandableListAdapter {

        private List<String> mSearchKeywords;

        private List<String> mSearchHistoryKeywords;

        private LayoutInflater mInflater;

        public SearchExpandableListAdapter(Context context) {
            mInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        }

        public void setSearchKeyWords(List<String> list) {
            mSearchKeywords = list;

            notifyDataSetChanged();
        }

        public void setSearchHistoryKeywords(List<String> list) {
            mSearchHistoryKeywords = list;

            notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {

            List<String> list = null;
            if (0 == groupPosition) {
                list = mSearchKeywords;
            } else {
                list = mSearchHistoryKeywords;
            }

            return null != list ? list.size() : 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getChild(int groupPosition, int childPosition) {

            String keyword = null;
            if (0 == groupPosition) {
                keyword = mSearchKeywords.get(childPosition);
            } else {
                keyword = mSearchHistoryKeywords.get(childPosition);
            }

            return keyword;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.item_search_group, null, false);

                ViewHolder holder = new ViewHolder();
                holder.mTextView = (TextView) convertView.findViewById(R.id.group_text);
                holder.mImageButton = (ImageButton) convertView.findViewById(R.id.btn_clear_phase);

                convertView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.mTextView.setText(groupPosition == 0 ? "热词推荐" : "搜索记录");
            holder.mImageButton.setVisibility(groupPosition == 0 ? View.GONE : View.VISIBLE);
            holder.mImageButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    setSearchHistoryKeywords(null);
                    mCacheManager.clearSearchCache();
                }
            });

            if (0 == groupPosition) {
                holder.mImageButton.setVisibility(View.GONE);
            } else {
                holder.mImageButton.setVisibility(getChildrenCount(groupPosition) > 0 ? View.VISIBLE : View.GONE);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.item_search_keyword, null, false);

                ViewHolder holder = new ViewHolder();
                holder.mTextView = (TextView) convertView.findViewById(R.id.text);

                convertView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (0 == groupPosition) {
                holder.mTextView.setText(mSearchKeywords.get(childPosition));

            } else {
                holder.mTextView.setText(mSearchHistoryKeywords.get(childPosition));

            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public void onGroupCollapsed(int groupPosition) {
        }
    }

    static class ViewHolder {
        TextView mTextView;

        ImageButton mImageButton;
    }
}
