package com.sergon146.mobilization17.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.presenter.HistoryFragmentPresenter;
import com.sergon146.mobilization17.presenter.impl.HistoryFragmentPresenterImpl;
import com.sergon146.mobilization17.util.Const;

public class HistoryFragment extends Fragment {
    private HistoryFragmentPresenter presenter;
    private LinearLayout layout;

    public static HistoryFragment newInstance(int page) {
        HistoryFragment historyFragment = new HistoryFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(Const.ARGUMENT_PAGE_NUMBER, page);
        historyFragment.setArguments(arguments);
        return historyFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new HistoryFragmentPresenterImpl(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_history, container, false);
        presenter.initRecycler(layout);
        ImageView trash = (ImageView) layout.findViewById(R.id.history_trash);
        trash.setOnClickListener(v -> presenter.showDialog(v));
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.getNewData();
    }

    public ImageView getTrash() {
        return (ImageView) layout.findViewById(R.id.history_trash);
    }
}


