package com.sergon146.mobilization17.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.util.Const;

public class FavouriteFragment extends Fragment {

    public static FavouriteFragment newInstance(int page) {
        FavouriteFragment favouriteFragment = new FavouriteFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(Const.ARGUMENT_PAGE_NUMBER, page);
        favouriteFragment.setArguments(arguments);
        return favouriteFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout translateLayout = (LinearLayout) inflater.inflate(R.layout.fragment_favourite, container, false);
        return translateLayout;
    }

}
