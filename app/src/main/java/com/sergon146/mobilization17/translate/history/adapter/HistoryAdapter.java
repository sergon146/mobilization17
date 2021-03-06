package com.sergon146.mobilization17.translate.history.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.pojo.Translate;
import com.sergon146.mobilization17.translate.history.HistoryContract;
import com.sergon146.mobilization17.util.ViewUtil;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.TranslateViewHolder> {
    private List<Translate> translates;
    private HistoryContract.TranslateItemListener itemListener;

    public HistoryAdapter(List<Translate> translates, HistoryContract.TranslateItemListener itemListener) {
        this.translates = translates;
        this.itemListener = itemListener;
    }

    @Override
    public TranslateViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_item, viewGroup, false);
        return new TranslateViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TranslateViewHolder holder, int i) {
        Translate translate = translates.get(i);
        holder.itemView.setId(i);
        holder.source.setText(translate.getSourceText());
        holder.target.setText(translate.getTargetText());
        holder.langs.setText(translate.getSourceLangCode() + " - " + translate.getTargetLangCode());
        if (translate.isFavourite()) {
            holder.favourite.setImageResource(R.drawable.ic_active_favourite);
        } else {
            holder.favourite.setImageResource(R.drawable.ic_inactive_favourite);
        }
    }

    @Override
    public int getItemCount() {
        return translates.size();
    }

    /**
     * Update adapter data if necessary
     *
     * @param translateList list of translations
     */
    public void updateData(List<Translate> translateList) {
        setTranslates(translateList);
    }

    private void setTranslates(List<Translate> translates) {
        if (this.translates != translates) {
            this.translates = translates;
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(TranslateViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    private void showDialog(Context context, View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setNeutralButton(context.getString(R.string.delete),
                (dialog, idV) -> {
                    dialog.cancel();
                    itemListener.onItemClick(translates.get(v.getId()));
                    translates.remove(v.getId());
                    notifyItemRemoved(v.getId());
                })
                .create()
                .show();
    }

    class TranslateViewHolder extends RecyclerView.ViewHolder {


        TextView source, target, langs;
        ImageView favourite;

        TranslateViewHolder(View itemView) {
            super(itemView);
            source = (TextView) itemView.findViewById(R.id.source_text);
            target = (TextView) itemView.findViewById(R.id.target_text);
            langs = (TextView) itemView.findViewById(R.id.langs);
            favourite = (ImageView) itemView.findViewById(R.id.favourite);
            favourite.setOnClickListener(v -> {
                Translate translate = translates.get(itemView.getId());

                if (translate.isFavourite()) {
                    favourite.setImageResource(R.drawable.ic_inactive_favourite);
                } else {
                    favourite.setImageResource(R.drawable.ic_active_favourite);
                }
                ViewUtil.animateClick(v, R.anim.click_scale);
                translate.setFavourite(!translate.isFavourite());
                itemListener.onFavouriteClick(translate);

            });
            itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> showDialog(v.getContext(), v));
        }

    }
}