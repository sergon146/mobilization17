package com.sergon146.mobilization17.languagechoose.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.pojo.Language;

import java.util.List;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {
    private List<Language> languages;

    public LanguageAdapter(List<Language> languages) {
        this.languages = languages;
    }

    @Override
    public LanguageViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.language_item, viewGroup, false);
        return new LanguageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LanguageViewHolder holder, int i) {
        holder.itemView.setId(languages.get(i).getId());
        holder.name.setText(languages.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    class LanguageViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        LanguageViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.lang_name);
        }
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
        notifyDataSetChanged();
    }
}