package com.sergon146.mobilization17.favourite;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.adapter.HistoryAdapter;
import com.sergon146.mobilization17.pojo.Translate;

import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavFragment extends Fragment implements FavouriteContract.View {
    private FavouriteContract.Presenter presenter;
    private EditText editTextSearch;
    private ImageView clearSearch; //todo add
    private ImageView clearData;
    private TextView tvEmpty;
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;

    public FavFragment() {
    }

    public static FavFragment newInstance() {
        return new FavFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new HistoryAdapter(Collections.emptyList());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);
        editTextSearch = (EditText) rootView.findViewById(R.id.editText);
        clearSearch = (ImageView) rootView.findViewById(R.id.ivClearSearch);
//        clearData = (ImageView) rootView.findViewById(R.id.ivClearSearch);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
//        tvEmpty = (ImageView) rootView.findViewById(R.id.ivClearSearch);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.unsubscribe();
    }

    @Override
    public void setPresenter(FavouriteContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showSearchView() {
        ((View) editTextSearch.getParent()).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSearchView() {
        ((View) editTextSearch.getParent()).setVisibility(View.GONE);
    }

    @Override
    public void showData(List<Translate> translateList) {
        recyclerView.setVisibility(View.VISIBLE);
        adapter.updateData(translateList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void hideData() {
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showEmpty() {
        tvEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmpty() {
        tvEmpty.setVisibility(View.GONE);
    }
}
