package com.sergon146.mobilization17.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.adapter.HistoryAdapter;
import com.sergon146.mobilization17.db.DbBackend;
import com.sergon146.mobilization17.pojo.Translate;
import com.sergon146.mobilization17.util.Const;

import java.util.List;

public class HistoryFragment extends Fragment {
    private DbBackend backend;
    private RecyclerView recyclerView;

    public static HistoryFragment newInstance(int page) {
        HistoryFragment historyFragment = new HistoryFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(Const.ARGUMENT_PAGE_NUMBER, page);
        historyFragment.setArguments(arguments);
        return historyFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout historyLayout = (LinearLayout) inflater.inflate(R.layout.fragment_history, container, false);
        List<Translate> translateList = backend.getHistory();

        recyclerView = (RecyclerView) historyLayout.findViewById(R.id.history_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        HistoryAdapter adapter = new HistoryAdapter(translateList);
        recyclerView.setAdapter(adapter);
        return historyLayout;
    }
}


