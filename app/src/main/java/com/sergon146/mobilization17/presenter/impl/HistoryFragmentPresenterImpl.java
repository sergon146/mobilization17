package com.sergon146.mobilization17.presenter.impl;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.adapter.HistoryAdapter;
import com.sergon146.mobilization17.db.DbBackend;
import com.sergon146.mobilization17.fragment.HistoryFragment;
import com.sergon146.mobilization17.pojo.Translate;
import com.sergon146.mobilization17.presenter.HistoryFragmentPresenter;

public class HistoryFragmentPresenterImpl implements HistoryFragmentPresenter {
    private HistoryFragment fragment;
    private HistoryAdapter adapter;
    private RecyclerView recyclerView;
    private DbBackend backend;

    public HistoryFragmentPresenterImpl(HistoryFragment fragment) {
        this.fragment = fragment;
        backend = new DbBackend(fragment.getContext());
    }

    @Override
    public void initRecycler(View layout) {
        recyclerView = (RecyclerView) layout.findViewById(R.id.history_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
        adapter = new HistoryAdapter(backend.getHistory(), this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void getNewData() {
        adapter.setTranslates(backend.getHistory());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void updateTranslate(Translate translate) {
        backend.updateTranslateFavourite(translate);
    }

    @Override
    public void deleteHistory() {
        backend.deleteHistory();
        Toast.makeText(fragment.getContext(),
                fragment.getString(R.string.del_history),
                Toast.LENGTH_SHORT).show();
    }

    public void showDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
        builder.setMessage(fragment.getString(R.string.history_dialog_title))
                .setNegativeButton(fragment.getString(R.string.no),
                        (dialog, idV) -> dialog.cancel())
                .setPositiveButton(fragment.getString(R.string.yes),
                        (dialog, idV) -> {
                            dialog.cancel();
                            deleteHistory();
                            recyclerView.removeAllViews();
                            v.setVisibility(View.GONE);
                        })
                .create()
                .show();
    }

}
