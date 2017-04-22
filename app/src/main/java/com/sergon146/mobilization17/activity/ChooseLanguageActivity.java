package com.sergon146.mobilization17.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.adapter.LanguageAdapter;
import com.sergon146.mobilization17.data.source.local.DbBackend;
import com.sergon146.mobilization17.listner.RecyclerItemClickListener;
import com.sergon146.mobilization17.pojo.Language;
import com.sergon146.mobilization17.util.Const;

import java.util.ArrayList;
import java.util.Locale;

public class ChooseLanguageActivity extends AppCompatActivity {
    ArrayList<Language> languages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);
        Intent intent = getIntent();
        initActionBar(intent);
        initData();
    }

    private void initData() {
        DbBackend backend = new DbBackend(this);
        languages = backend.getCashedLangs(Locale.getDefault().getLanguage());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.langsRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LanguageAdapter adapter = new LanguageAdapter(languages);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), (view, position) -> {
                    Intent intent = new Intent();
                    intent.putExtra(Const.LANGUAGE, languages.get(position).getName());
                    intent.putExtra(Const.CODE, languages.get(position).getCode());
                    setResult(RESULT_OK, intent);
                    finish();
                })
        );
    }

    private void initActionBar(Intent intent) {
        switch (intent.getAction()) {
            case Const.SOURCE:
                setTitle(getString(R.string.source_title));
                break;
            case Const.TARGET:
                setTitle(getString(R.string.target_title));
                break;
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return false;
        }
    }
}
