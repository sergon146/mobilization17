package com.sergon146.mobilization17.languagechoose;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.data.TranslationDataSource;
import com.sergon146.mobilization17.languagechoose.adapter.LanguageAdapter;
import com.sergon146.mobilization17.languagechoose.listener.RecyclerItemClickListener;
import com.sergon146.mobilization17.pojo.Language;
import com.sergon146.mobilization17.util.Const;
import com.sergon146.mobilization17.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChooseLanguageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LanguageAdapter adapter;
    private List<Language> languages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);
        languages = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.langs_recycler);
        initData();

        initActionBar();
    }

    private void initData() {
        TranslationDataSource repository = Util.provideTranslateRepository(getApplicationContext());
        repository.loadLangs((Locale.getDefault().getLanguage()))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Language>>() {
                    @Override
                    public void onNext(List<Language> languageList) {
                        languages = languageList;
                        adapter.setLanguages(languages);
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(Const.LOG_CHOOSE, "Languages loaded ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(Const.LOG_CHOOSE, "Load langs: " + e);
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.error),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LanguageAdapter(languages);
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

    private void initActionBar() {
        switch (getIntent().getAction()) {
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
                finish();
                return true;
            default:
                return false;
        }
    }
}
