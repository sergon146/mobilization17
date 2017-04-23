package com.sergon146.mobilization17.translate.history.favourite;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.pojo.Translate;
import com.sergon146.mobilization17.translate.history.HistoryContract;
import com.sergon146.mobilization17.translate.history.adapter.HistoryAdapter;

import java.util.Collections;
import java.util.List;

public class FavouriteFragment extends Fragment implements HistoryContract.View {
    private HistoryContract.Presenter mPresenter;
    HistoryContract.TranslateItemListener itemListener = new HistoryContract.TranslateItemListener() {
        @Override
        public void onFavouriteClick(Translate translate) {
            mPresenter.setFavourite(translate);
        }

        @Override
        public void onItemClick(Translate translate) {
            mPresenter.deleteTranslate(translate);
        }
    };
    private EditText etSearch;
    private ImageView ivClearSearch;
    private ImageView ivTrash;
    private LinearLayout mEmptyData;
    private RecyclerView mRecyclerView;
    private HistoryAdapter mAdapter;

    public FavouriteFragment() {
    }

    public static FavouriteFragment newInstance() {
        return new FavouriteFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new HistoryAdapter(Collections.emptyList(), itemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    public void setPresenter(HistoryContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);
        etSearch = (EditText) rootView.findViewById(R.id.editText);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mPresenter != null) {
                    mPresenter.search(s.toString());
                }

            }
        });
        ivClearSearch = (ImageView) rootView.findViewById(R.id.ivClearSearch);
        ivClearSearch.setOnClickListener(v -> clearSearch());
        mEmptyData = (LinearLayout) rootView.findViewById(R.id.emptyData);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ivTrash = (ImageView) rootView.findViewById(R.id.trash);
        ivTrash.setOnClickListener(v -> mPresenter.showDialog(getContext()));

        return rootView;
    }

    @Override
    public void hideSearchView() {
        ((View) etSearch.getParent()).setVisibility(View.GONE);
    }

    @Override
    public void clearSearch() {
        etSearch.setText("");
    }

    @Override
    public void hideTrash() {
        ivTrash.setVisibility(View.GONE);
    }

    @Override
    public void showTrash() {
        ivTrash.setVisibility(View.VISIBLE);
    }

    @Override
    public void showData(List<Translate> translateList) {
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.updateData(translateList);
    }

    @Override
    public void hideData() {
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showEmpty() {
        mEmptyData.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmpty() {
        mEmptyData.setVisibility(View.GONE);
    }
}
