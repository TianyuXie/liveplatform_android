package com.pplive.liveplatform;

import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pplive.liveplatform.core.cache.SearchCacheManager;
import com.pplive.liveplatform.ui.SearchResultActivity;
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
                String keyword = mEditSearchInput.getText().toString();

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
        mExpandableListView.expandGroup(0);
        mExpandableListView.expandGroup(1);

        mCacheManager = SearchCacheManager.getInstance(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAdapter.setSearchHistoryKeywords(mCacheManager.getSearchCache(10));
    }
    
    private void search(String keyword) {
        if (!TextUtils.isEmpty(keyword)) {

            mCacheManager.updateCache(keyword);

            Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
            intent.putExtra(SearchResultActivity.KEY_SEARCH_KEY_WORD, keyword);

            startActivity(intent);
        }
    }

    class SearchExpandableListAdapter extends BaseExpandableListAdapter {

        private String[] keywords = { "NBA", "英超" };

        private List<String> mSearchHistoryKeywords;

        private LayoutInflater mInflater;

        public SearchExpandableListAdapter(Context context) {
            mInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
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
            return 0 == groupPosition ? keywords.length : null != mSearchHistoryKeywords ? mSearchHistoryKeywords.size() : 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getChild(int groupPosition, int childPosition) {
            
            String keyword = null;
            if (1 == groupPosition) {
                keyword = mSearchHistoryKeywords.get(childPosition);
            } else {
                keyword = keywords[childPosition];
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
                holder.mTextView = (TextView) convertView.findViewById(R.id.text);

                convertView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.mTextView.setText(groupPosition == 0 ? "热词推荐" : "搜索记录");

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
            if (1 == groupPosition) {
                holder.mTextView.setText(mSearchHistoryKeywords.get(childPosition));
            } else {
                holder.mTextView.setText(keywords[childPosition]);
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return true;
        }
    }

    static class ViewHolder {
        TextView mTextView;
    }
}
