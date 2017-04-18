package com.sergon146.mobilization17.viewholder;

import android.content.Context;
import android.widget.EditText;

public class TranslateViewHolder {
    private TranslateViewHolder viewHolder = new TranslateViewHolder();
    private Context context;
    public EditText sourceEditText;

    private TranslateViewHolder() {
    }

    public TranslateViewHolder(Context context) {
        this.context = context;
    }

    public TranslateViewHolder getViewHolder() {
        return viewHolder;
    }

}
